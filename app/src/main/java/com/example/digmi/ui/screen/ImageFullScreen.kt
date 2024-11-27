package com.example.digmi.ui.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import coil.compose.AsyncImage
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun FullImageScreen(imageUrl: String?, navController: NavController) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        if (imageUrl != null) {
            // Display the image using Coil
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            // Display a message if imageUrl is null
            Text(
                text = "Image not found",
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center),
                textAlign = TextAlign.Center
            )
        }

        // Close button in the top-left corner
        IconButton(
            onClick = { navController.popBackStack() }, // Navigate back
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopEnd) // Position the button in the top-left corner
        ) {
            Icon(
                imageVector = Icons.Default.Close, // You can use a close icon from Material Icons
                contentDescription = "Close",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
