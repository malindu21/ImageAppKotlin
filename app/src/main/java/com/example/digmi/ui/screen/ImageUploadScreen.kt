package com.example.digmi.ui.screen

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberImagePainter
import com.example.digmi.R
import com.example.digmi.ui.viewmodel.ImageUploadViewModel

@Composable
fun ImageUploadScreen(
    onPickImage: () -> Unit,
    onTakePicture: () -> Unit,
    onCancel: () -> Unit,
    imageUrl: String?,
    isUploading: Boolean,
    imageUploadViewModel: ImageUploadViewModel
) {
    val selectedImageUri by imageUploadViewModel.selectedImageUri

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(16.dp)
                    .shadow(8.dp, shape = MaterialTheme.shapes.medium)
                    .background(Color.White),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (selectedImageUri == null) {
                    Text(
                        text = "No image selected",
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                } else {
                    Image(
                        painter = rememberImagePainter(selectedImageUri),
                        contentDescription = null,
                        modifier = Modifier
                            .size(200.dp)
                            .padding(16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceAround,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    IconButton(
                        onClick = onPickImage,
                        modifier = Modifier
                            .size(56.dp)
                            .background(Color(0xFF007AFF), shape = MaterialTheme.shapes.small)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_gallary),
                            contentDescription = "Pick Image",
                            tint = Color.White
                        )
                    }

                    IconButton(
                        onClick = onTakePicture,
                        modifier = Modifier
                            .size(56.dp)
                            .background(Color(0xFF34C759), shape = MaterialTheme.shapes.small)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_camera),
                            contentDescription = "Take Picture",
                            tint = Color.White
                        )
                    }

                    IconButton(
                        onClick = onCancel,
                        modifier = Modifier
                            .size(56.dp)
                            .background(Color(0xFFFF3B30), shape = MaterialTheme.shapes.small)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_cancel),
                            contentDescription = "Cancel",
                            tint = Color.White
                        )
                    }
                }

                if (selectedImageUri != null) {
                    Button(
                        onClick = { imageUploadViewModel.uploadSelectedImage() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor =  colorResource(id = R.color.button_purple),
                            contentColor = Color.White
                        )
                    ) {
                        if (isUploading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White
                            )
                        } else {
                            Text("Upload Image")
                        }
                    }
                }
            }
        }

}
