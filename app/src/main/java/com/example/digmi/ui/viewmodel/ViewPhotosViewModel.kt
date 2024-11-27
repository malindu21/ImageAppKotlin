package com.example.digmi.ui.viewmodel
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ViewPhotosViewModel : ViewModel() {

    private val databaseRef: DatabaseReference = FirebaseDatabase.getInstance().reference

    // Mutable state flow to manage image URLs
    private val _imageURLs = MutableStateFlow<List<String>>(emptyList())
    val imageURLs: StateFlow<List<String>> = _imageURLs

    // Mutable state flow to manage loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Mutable state flow to manage errors
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // Fetch image URLs from Firebase Realtime Database
    fun fetchImageURLs() {
        _isLoading.value = true
        databaseRef.child("images").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val urls = mutableListOf<String>()
                for (childSnapshot in snapshot.children) {
                    val url = childSnapshot.getValue(String::class.java)
                    url?.let { urls.add(it) }
                }
                _imageURLs.value = urls
                _isLoading.value = false
            }

            override fun onCancelled(error: DatabaseError) {
                _error.value = error.message
                _isLoading.value = false
            }
        })
    }
}
