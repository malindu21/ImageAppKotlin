package com.example.digmi.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.example.digmi.R

@Composable
fun PhotoItemView(photo: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically // Vertically center items in the row
    ) {
        // Image thumbnail
        Image(
            painter = rememberImagePainter(photo),
            contentDescription = "Photo thumbnail",
            modifier = Modifier
                .size(120.dp) // Image size
                .padding(end = 16.dp)
        )

        // Photo URL in a TextField with a fixed width
        var text by remember { mutableStateOf(photo) }

        BasicTextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier
                .width(200.dp) // Fixed width for the text field
                .padding(8.dp)
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(4.dp))
                .border(1.dp, MaterialTheme.colorScheme.onSurface, RoundedCornerShape(4.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
            ),
            singleLine = true
        )

        // Eye icon for viewing photo
        IconButton(
            onClick = { onClick() },
            modifier = Modifier
                .padding(start = 8.dp) // Add space between text field and icon
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_view), // Replace with your actual icon
                contentDescription = "View Photo",
                tint = Color.Blue
            )
        }
    }
}
