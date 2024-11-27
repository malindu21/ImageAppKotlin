package com.example.digmi.ui.viewmodel
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class ImageUploadViewModel : ViewModel() {
    var imageUrl = mutableStateOf<String?>(null)  // Uploaded image URL
    val isUploading = mutableStateOf(false)
    var selectedImageUri = mutableStateOf<Uri?>(null)  // Store the selected image URI

    private val storageReference = FirebaseStorage.getInstance().reference
    private val databaseReference = FirebaseDatabase.getInstance().reference

    // Called when an image is selected
    fun setImageUri(uri: Uri) {
        selectedImageUri.value = uri
    }

    // Upload image only when the button is clicked
    fun uploadSelectedImage() {
        val uri = selectedImageUri.value
        if (uri == null) {
            Log.e("ImageUploadViewModel", "No image selected for upload")
            return
        }

        isUploading.value = true
        val imageRef = storageReference.child("images/${System.currentTimeMillis()}")

        imageRef.putFile(uri)
            .addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    imageUrl.value = uri.toString()
                    saveUrlToDatabase(uri.toString()) // Save the URL to the database
                    selectedImageUri.value = null // Clear the selected image after upload
                }
            }
            .addOnFailureListener { exception ->
                isUploading.value = false
                Log.e("Firebase", "Error uploading image: ${exception.message}")
            }
    }

    private fun saveUrlToDatabase(url: String) {
        val imageId = databaseReference.push().key
        if (imageId != null) {
            databaseReference.child("images").child(imageId).setValue(url)
                .addOnSuccessListener {
                    isUploading.value = false
                }
                .addOnFailureListener { exception ->
                    isUploading.value = false
                    Log.e("Firebase", "Error saving URL: ${exception.message}")
                }
        }
    }

    fun cancelImageSelection() {
        selectedImageUri.value = null
    }
}
