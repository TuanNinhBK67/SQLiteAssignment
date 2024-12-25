package com.example.studentmanagementsqlite

import android.content.ContentValues
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import com.example.studentmanagementsqlite.adapter.Student
import com.example.studentmanagementsqlite.adapter.StudentAdapter
import com.example.studentmanagementsqlite.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var db: SQLiteDatabase? = null
    private lateinit var studentAdapter: StudentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            if (!Environment.isExternalStorageManager()) {
                val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                startActivity(intent)
            }

        val path = filesDir.path + "/studentDb"
        db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.CREATE_IF_NECESSARY)
        createTable()
        binding.buttonInsert.setOnClickListener {
            db?.beginTransaction()
            try {
                val name = binding.editName.text.toString()
                val id = binding.editId.text.toString()
                val cv = ContentValues()
                cv.put("name", name)
                cv.put("id", id)
                db?.insert("STUDENT", null, cv)
                db?.setTransactionSuccessful()
                loadStudents()
            } catch (ex: Exception) {
                ex.printStackTrace()
            } finally {
                db?.endTransaction()
            }
        }

        binding.buttonGetAll.setOnClickListener {
            loadStudents()
        }

        loadStudents()

    }

    private fun createTable() {
        db?.beginTransaction()
        try {
            db?.execSQL("create table STUDENT(" +
                    "recID integer primary key autoincrement," +
                    "name text," +
                    "id text)")
            db?.setTransactionSuccessful()
        } catch (ex: Exception) {
            ex.printStackTrace()
        } finally {
            db?.endTransaction()
        }
    }

    private fun loadStudents() {
        val students = mutableListOf<Student>()
        db?.let {
            val cs = it.query(
                "STUDENT",
                arrayOf("recID", "name", "id"),
                null, null, null, null, null
            )
            if (cs.moveToFirst()) {
                do {
                    val recID = cs.getInt(0)
                    val name = cs.getString(1)
                    val id = cs.getString(2)
                    students.add(Student(recID, name, id))
                } while (cs.moveToNext())
            }
            cs.close()
        }
        studentAdapter = StudentAdapter(this, students, ::updateStudent, ::deleteStudent)
        binding.listView.adapter = studentAdapter
    }
    private fun updateStudent(student: Student) {
        db?.beginTransaction()
        try {
            val cv = ContentValues()
            cv.put("name", student.name)
            cv.put("id", student.id)
            db?.update("STUDENT", cv, "recID = ?", arrayOf(student.recID.toString()))
            db?.setTransactionSuccessful()
            studentAdapter.notifyDataSetChanged()
        } catch (ex: Exception) {
            ex.printStackTrace()
        } finally {
            db?.endTransaction()
        }
        loadStudents()
    }

    private fun deleteStudent(student: Student) {
        db?.beginTransaction()
        try {
            db?.delete("STUDENT", "recID = ?", arrayOf(student.recID.toString()))
            db?.setTransactionSuccessful()
        } catch (ex: Exception) {
            ex.printStackTrace()
        } finally {
            db?.endTransaction()
        }
        loadStudents()
    }


    override fun onStop() {
        db?.close()
        db = null
        super.onStop()
    }
}