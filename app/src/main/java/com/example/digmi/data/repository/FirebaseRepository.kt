package com.example.digmi.data.repository

import com.example.digmi.data.model.Photo

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.getValue

class FirebaseRepository {

    private val database: DatabaseReference = FirebaseDatabase.getInstance().getReference("photos")

    // Function to get photos from Firebase
    fun getAllPhotos(onSuccess: (List<Photo>) -> Unit, onFailure: (Exception) -> Unit) {
        database.get()
            .addOnSuccessListener { snapshot ->
                val photos = snapshot.children.mapNotNull {
                    it.getValue<Photo>()
                }
                onSuccess(photos)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
}
