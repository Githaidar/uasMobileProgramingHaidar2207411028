// ViewNotesActivity.kt
package com.example.uasmobileprog

import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import android.widget.Button

class ViewNotesActivity : AppCompatActivity() {

    private lateinit var notesListView: ListView
    private lateinit var firestore: FirebaseFirestore
    private lateinit var notesList: MutableList<Note>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_notes)

        notesListView = findViewById(R.id.notesListView)
        firestore = FirebaseFirestore.getInstance()
        notesList = mutableListOf()

        fetchNotes()

        notesListView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val note = notesList[position]
            val intent = Intent(this, EditNoteActivity::class.java)
            intent.putExtra("noteId", note.id)
            intent.putExtra("note", note.note)
            intent.putExtra("latitude", note.latitude)
            intent.putExtra("longitude", note.longitude)
            startActivity(intent)
        }

        notesListView.onItemLongClickListener = AdapterView.OnItemLongClickListener { _, _, position, _ ->
            val note = notesList[position]
            deleteNote(note.id)
            true
        }
        val backToMainButton: Button = findViewById(R.id.backToMainButton)
        backToMainButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Close current activity
        }
    }

    private fun fetchNotes() {
        firestore.collection("notes")
            .get()
            .addOnSuccessListener { documents ->
                notesList.clear()
                for (document in documents) {
                    val note = Note(
                        document.id,
                        document.getString("note") ?: "",
                        document.getDouble("latitude") ?: 0.0,
                        document.getDouble("longitude") ?: 0.0
                    )
                    notesList.add(note)
                }
                val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, notesList.map {
                    "${it.note}\nLat: ${it.latitude}, Lon: ${it.longitude}"
                })
                notesListView.adapter = adapter
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to fetch notes: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }


    private fun deleteNote(noteId: String) {
        firestore.collection("notes").document(noteId)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Note deleted", Toast.LENGTH_SHORT).show()
                fetchNotes() // Refresh the list after deletion
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to delete note: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
