package com.example.digmi.ui


import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.digmi.ui.activity.BottomNavigationBar
import com.example.digmi.ui.activity.addScreens
import com.example.digmi.ui.viewmodel.ImageUploadViewModel
import androidx.compose.runtime.MutableState

@Composable
fun MainApp(
    imageUploadViewModel: ImageUploadViewModel,
    pickImageLauncher: ActivityResultLauncher<String>,
    takePictureLauncher: ActivityResultLauncher<Uri>,
    setupUriForCamera: () -> Unit,
    uriToUpload: MutableState<Uri?> // Add uriToUpload as a parameter
) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "add_photos",
            Modifier.padding(innerPadding)
        ) {
            addScreens(navController, imageUploadViewModel, pickImageLauncher, takePictureLauncher, setupUriForCamera, uriToUpload)
        }
    }
}
