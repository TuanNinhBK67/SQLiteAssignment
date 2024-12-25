package com.example.studentmanagementsqlite.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.TextView
import com.example.studentmanagementsqlite.R

class StudentAdapter(
    context: Context,
    private val students: List<Student>,
    private val onUpdate: (Student) -> Unit,
    private val onDelete: (Student) -> Unit
) : ArrayAdapter<Student>(context, 0, students) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)
        val student = students[position]

        val textViewName = view.findViewById<TextView>(R.id.textViewName)
        val textViewId = view.findViewById<TextView>(R.id.textViewId)
        val buttonUpdate = view.findViewById<ImageButton>(R.id.buttonUpdate)
        val buttonDelete = view.findViewById<ImageButton>(R.id.buttonDelete)

        textViewName.text = student.name
        textViewId.text = student.id

        buttonUpdate.setOnClickListener {
            onUpdate(student)
        }
        buttonDelete.setOnClickListener {
            onDelete(student)
        }

        return view
    }
}

data class Student(val recID: Int, val name: String, val id: String)