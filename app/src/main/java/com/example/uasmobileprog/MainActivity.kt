// MainActivity.kt
package com.example.uasmobileprog

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.FirebaseFirestore
import android.Manifest
import android.content.pm.PackageManager

class MainActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var firestore: FirebaseFirestore
    private lateinit var noteEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        firestore = FirebaseFirestore.getInstance()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        noteEditText = findViewById(R.id.noteEditText)
        val saveButton: Button = findViewById(R.id.saveButton)
        saveButton.setOnClickListener {
            saveNote()
        }

        val openMapButton: Button = findViewById(R.id.openMapButton)
        openMapButton.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }

        val viewNotesButton: Button = findViewById(R.id.viewNotesButton)
        viewNotesButton.setOnClickListener {
            val intent = Intent(this, ViewNotesActivity::class.java)
            startActivity(intent)
        }
    }

    private fun saveNote() {
        val note = noteEditText.text.toString()
        if (note.isNotEmpty()) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            } else {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    location?.let {
                        val noteData = hashMapOf(
                            "note" to note,
                            "latitude" to it.latitude,
                            "longitude" to it.longitude
                        )
                        firestore.collection("notes")
                            .add(noteData)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Note saved", Toast.LENGTH_SHORT).show()
                                noteEditText.text.clear()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Failed to save note: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    } ?: run {
                        Toast.makeText(this, "Failed to get location", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else {
            Toast.makeText(this, "Please enter a note", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                saveNote()  // Try to save the note again if permission is granted
            } else {
                Toast.makeText(this, "Permission denied to access location", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
