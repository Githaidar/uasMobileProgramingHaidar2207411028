// EditNoteActivity.kt
package com.example.uasmobileprog

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class EditNoteActivity : AppCompatActivity() {

    private lateinit var editNoteEditText: EditText
    private lateinit var updateButton: Button
    private lateinit var deleteButton: Button
    private lateinit var firestore: FirebaseFirestore

    private var noteId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_note)

        editNoteEditText = findViewById(R.id.editNoteEditText)
        updateButton = findViewById(R.id.updateButton)
        deleteButton = findViewById(R.id.deleteButton)
        firestore = FirebaseFirestore.getInstance()

        noteId = intent.getStringExtra("noteId")
        val noteText = intent.getStringExtra("note")
        val latitude = intent.getDoubleExtra("latitude", 0.0)
        val longitude = intent.getDoubleExtra("longitude", 0.0)

        editNoteEditText.setText(noteText)

        updateButton.setOnClickListener {
            updateNote()
        }

        deleteButton.setOnClickListener {
            deleteNote()
        }
    }

    private fun updateNote() {
        val updatedNote = editNoteEditText.text.toString()
        if (noteId != null && updatedNote.isNotEmpty()) {
            firestore.collection("notes").document(noteId!!)
                .update("note", updatedNote)
                .addOnSuccessListener {
                    Toast.makeText(this, "Note updated", Toast.LENGTH_SHORT).show()
                    finish() // Close the activity after update
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to update note: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Note cannot be empty", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteNote() {
        if (noteId != null) {
            firestore.collection("notes").document(noteId!!)
                .delete()
                .addOnSuccessListener {
                    Toast.makeText(this, "Note deleted", Toast.LENGTH_SHORT).show()
                    finish() // Close the activity after deletion
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to delete note: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
