package com.example.digmi.ui.activity

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.digmi.ui.theme.DigmiTheme
import java.io.File
import androidx.compose.runtime.MutableState
import androidx.core.content.FileProvider
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.digmi.ui.viewmodel.ImageUploadViewModel
import com.example.digmi.R
import com.example.digmi.data.model.NavigationItem
import com.example.digmi.ui.MainApp
import com.example.digmi.ui.screen.FullImageScreen
import com.example.digmi.ui.screen.ImageUploadScreen
import com.example.digmi.ui.screen.PhotosScreen
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import okio.IOException

class MainActivity : ComponentActivity() {
    private val imageUploadViewModel: ImageUploadViewModel = ImageUploadViewModel()
    // Use state for URI
    private var uriToUpload = mutableStateOf<Uri?>(null)

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            uriToUpload.value = it
            imageUploadViewModel.setImageUri(it)  // Set the URI image
        }
    }

    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
        if (isSuccess) {
            uriToUpload.value?.let {
                imageUploadViewModel.setImageUri(it)  // Set the URI image
            }
        }
    }

    private fun setupUriForCamera() {
        val imageFile = createImageFile()
        uriToUpload.value = FileProvider.getUriForFile(
            this, "${packageName}.provider", imageFile
        )
    }

    private fun createImageFile(): File {
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return if (storageDir?.exists() == true) {
            File.createTempFile("JPEG_${System.currentTimeMillis()}_", ".jpg", storageDir)
        } else {
            throw IOException("Failed to create image file")
        }
    }

    private val requestCameraPermissionLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Log.d("MainActivity", "Camera permission granted")
                // Now request storage permission after camera permission is granted
            } else {
                Log.d("MainActivity", "Camera permission denied")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check and request camera permission
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

        setContent {
            DigmiTheme {
                MainApp(
                    imageUploadViewModel = imageUploadViewModel,
                    pickImageLauncher = pickImageLauncher,
                    takePictureLauncher = takePictureLauncher,
                    setupUriForCamera = ::setupUriForCamera,
                    uriToUpload = uriToUpload  // Pass the URI to composables
                )
            }
        }
    }
}

fun NavGraphBuilder.addScreens(
    navController: NavHostController,
    imageUploadViewModel: ImageUploadViewModel,
    pickImageLauncher: ActivityResultLauncher<String>,
    takePictureLauncher: ActivityResultLauncher<Uri>,
    setupUriForCamera: () -> Unit,
    uriToUpload: MutableState<Uri?>  // Add uriToUpload as a parameter
) {
    composable("add_photos") {
        ImageUploadScreen(
            onPickImage = { pickImageLauncher.launch("image/*") },
            onTakePicture = {
                setupUriForCamera()  // Prepare URI for camera photo
                uriToUpload.value?.let {
                    takePictureLauncher.launch(it)  // Launch camera with the URI
                } ?: run {
                    // Log or show an error if URI is null
                    Log.e("MainActivity", "URI is null, cannot take picture")
                } // Launch camera with the URI
            },
            onCancel = { imageUploadViewModel.cancelImageSelection() },
            imageUrl = imageUploadViewModel.imageUrl.value,
            isUploading = imageUploadViewModel.isUploading.value,
            imageUploadViewModel = imageUploadViewModel
        )
    }

    composable("view_photos") {
        PhotosScreen(navController = navController)
    }

    composable(
        route = "full_image/{imageUrl}",
        arguments = listOf(navArgument("imageUrl") { type = NavType.StringType })
    ) { backStackEntry ->
        val imageUrl = backStackEntry.arguments?.getString("imageUrl")
        FullImageScreen(navController = navController,imageUrl = imageUrl)
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    NavigationBar(
        containerColor = Color.White,
        contentColor = Color(0xFF6200EE)
    ) {
        val items = listOf(
            NavigationItem("add_photos", "Add Photos", painterResource(id = R.drawable.ic_gallary)),
            NavigationItem("view_photos", "View Photos", painterResource(id = R.drawable.ic_view_image))
        )

        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = navController.currentDestination?.route == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}


