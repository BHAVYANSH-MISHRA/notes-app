package com.example.notesapp

import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var notesRv: RecyclerView
    private lateinit var adapter: NoteAdapter
    private val notes = mutableListOf<Note>()
    private lateinit var dao: NoteDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize DAO and load saved notes
        dao = NoteDatabase.get(this).noteDao()
        notes.addAll(dao.getAll())

        // Setup RecyclerView
        notesRv = findViewById(R.id.notesRecyclerView)
        adapter = NoteAdapter(notes) { note ->
            showDeleteDialog(note)
        }
        notesRv.layoutManager = LinearLayoutManager(this)
        notesRv.adapter = adapter

        // Setup FAB to add notes
        findViewById<FloatingActionButton>(R.id.addNoteButton).setOnClickListener {
            addNoteDialog()
        }
    }

    private fun addNoteDialog() {
        val input = EditText(this).apply { hint = "Enter note title" }
        AlertDialog.Builder(this)
            .setTitle("Add New Note")
            .setView(input)
            .setPositiveButton("Add") { dialog, _ ->
                val title = input.text.toString().trim()
                if (title.isNotEmpty()) {
                    val timestamp = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date())
                    val note = Note(title = title, timestamp = timestamp)
                    dao.insert(note)
                    notes.add(0, note)
                    adapter.notifyItemInserted(0)
                    notesRv.scrollToPosition(0)
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun showDeleteDialog(note: Note) {
        AlertDialog.Builder(this)
            .setTitle("Delete Note")
            .setMessage("Are you sure you want to delete this note?")
            .setPositiveButton("Delete") { dialog, _ ->
                dao.delete(note)
                val index = notes.indexOf(note)
                notes.removeAt(index)
                adapter.notifyItemRemoved(index)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .show()
    }
}
