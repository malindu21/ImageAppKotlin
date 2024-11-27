package com.example.digmi.ui.viewmodel
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

class ImageUploadViewModel : ViewModel() {
    var imageUrl = mutableStateOf<String?>(null) // Uploaded image URL
    val isUploading = mutableStateOf(false) // Upload status
    var selectedImageUri = mutableStateOf<Uri?>(null) // Selected image URI

    private val storageReference = FirebaseStorage.getInstance().reference
    private val databaseReference = FirebaseDatabase.getInstance().reference
    private var uploadedImageHash = mutableStateOf<String?>(null) // Track uploaded image hash

    fun setImageUri(uri: Uri) {
        selectedImageUri.value = uri
    }

    private fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun calculateImageHash(data: ByteArray): String {
        return data.hashCode().toString()
    }

    private fun compressImage(context: Context, uri: Uri): ByteArray {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val originalBitmap = BitmapFactory.decodeStream(inputStream)

            // Scale down the image for faster processing
            val rotatedBitmap = correctImageOrientation(context, uri, originalBitmap)
            // Correct the orientation

            val outputStream = ByteArrayOutputStream()
            rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 60, outputStream) // Compress to 70% quality
            outputStream.toByteArray()
        } catch (e: Exception) {
            Log.e("ImageUpload", "Error compressing image: ${e.message}")
            ByteArray(0)
        }
    }



    fun correctImageOrientation(context: Context, uri: Uri, bitmap: Bitmap): Bitmap {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val exif = inputStream?.let { ExifInterface(it) }

            val orientation = exif?.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )

            val matrix = Matrix()
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
            }

            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        } catch (e: Exception) {
            Log.e("ImageUpload", "Error correcting image orientation: ${e.message}")
            bitmap // Return the original bitmap if there's an error
        }
    }


    fun uploadSelectedImage(context: Context) {
        isUploading.value = true

        val uri = selectedImageUri.value
        if (uri == null) {
            showToast(context, "No image selected!")
            return
        }

        // Show progress immediately when upload starts

        showToast(context, "Starting upload...")

        viewModelScope.launch {
            val imageRef = storageReference.child("images/${System.currentTimeMillis()}")

            try {
                // Compress the image first
                val compressedData = compressImage(context, uri)
                if (compressedData.isEmpty()) {
                    isUploading.value = false
                    showToast(context, "Failed to compress image.")
                    return@launch
                }

                // Calculate image hash and check if it was already uploaded
                val currentHash = calculateImageHash(compressedData)
                if (currentHash == uploadedImageHash.value) {
                    isUploading.value = false
                    showToast(context, "This image has already been uploaded!")
                    return@launch
                }

                // Start the image upload
                imageRef.putBytes(compressedData)
                    .addOnSuccessListener {
                        imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                            imageUrl.value = downloadUri.toString()
                            saveUrlToDatabase(downloadUri.toString(), context)
                            selectedImageUri.value = null
                            uploadedImageHash.value = currentHash // Update hash to prevent duplicate uploads
                        }
                        isUploading.value = false
                        showToast(context, "Image uploaded successfully!")
                    }
                    .addOnFailureListener { exception ->
                        isUploading.value = false
                        showToast(context, "Upload failed: ${exception.message}")
                        Log.e("Firebase", "Error uploading image: ${exception.message}")
                    }
            } catch (e: Exception) {
                isUploading.value = false
                showToast(context, "Error: ${e.message}")
                Log.e("ImageUpload", "Compression/Upload error: ${e.message}")
            }
        }
    }

    private fun saveUrlToDatabase(url: String, context: Context) {
        val imageId = databaseReference.push().key
        if (imageId != null) {
            databaseReference.child("images").child(imageId).setValue(url)
                .addOnSuccessListener {
                    showToast(context, "URL saved to database!")
                }
                .addOnFailureListener { exception ->
                    showToast(context, "Error saving URL: ${exception.message}")
                    Log.e("Firebase", "Error saving URL: ${exception.message}")
                }
        } else {
            showToast(context, "Error generating image ID!")
        }
    }

    fun cancelImageSelection() {
        selectedImageUri.value = null
        isUploading.value = false
    }
}
