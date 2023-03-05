package com.navgde.sdkextensions

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.navgde.sdkextensions.ui.theme.SDKExtensionsTheme

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class MainActivity : ComponentActivity() {

    // use old way for launching activity for result
    private val photoIntent = Intent(MediaStore.ACTION_PICK_IMAGES).apply {
        // default: images & videos
        type = "image/*"
        // GET MAX LIMIT
        // MediaStore.getPickImagesMaxLimit()
        // CUSTOM LENGTH
        // putExtra(MediaStore.EXTRA_PICK_IMAGES_MAX, 2)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SDKExtensionsTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    PhotoPickerResultComposable()
                }
            }
        }
    }
}

@Composable
fun PhotoPickerResultComposable() {
    val context = LocalContext.current

    var result by rememberSaveable { mutableStateOf<Uri?>(null) }

    val pickMedia = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        // Callback is invoked after the user selects a media item or closes the
        // photo picker.
        if (uri != null) {
            Log.d(TAG, "PhotoPickerResultComposable: RESULT_CODE $uri")
            result = uri
        } else {
            Log.d(TAG, "No media selected")
        }
    }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "\uD83C\uDF05 SDKExtensions Sample \uD83C\uDF05",
            style = TextStyle(fontWeight = FontWeight.ExtraBold, fontSize = 28.sp),
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Android 11\n\nSDKExtensions version 4(R4)",
            style = TextStyle(fontWeight = FontWeight.ExtraBold, fontSize = 28.sp),
        )

        Image(
            painter = painterResource(id = R.drawable.ic_android_black_24dp),
            contentDescription = "Android Logo",
            modifier = Modifier.size(100.dp),
        )

        Spacer(modifier = Modifier.height(16.dp))

        OpenPhotoPicker(
            openLauncher = {
                when {
                    ActivityResultContracts.PickVisualMedia.isPhotoPickerAvailable(context = context) -> pickMedia.launch(
                        PickVisualMediaRequest(
                            ActivityResultContracts.PickVisualMedia.ImageOnly,
                        ),
                    )

                    else -> Toast.makeText(context, "Photo picker not available", Toast.LENGTH_SHORT).show()
                }
            },
        )

        Spacer(modifier = Modifier.height(16.dp))

        AsyncImage(
            model = result,
            contentDescription = "Image from photo picker",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(200.dp, 200.dp)
                .clip(CircleShape),
        )

        Spacer(modifier = Modifier.height(48.dp))

        Row {
            Text(
                "By Nav Singh Android GDE",
                style = TextStyle(fontWeight = FontWeight.ExtraBold, fontSize = 28.sp),
            )
        }
    }
}

@Composable
fun OpenPhotoPicker(openLauncher: () -> Unit) {
    OutlinedButton(onClick = openLauncher) {
        Text("Open photo picker")
    }
}

/*
// Safely use extension APIs that are available with Android 11 (API level 30) Extensions Version 2, such as Photo Picker.

fun isPhotoPickerAvailable(): Boolean =
    when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> true

        Build.VERSION.SDK_INT >= Build.VERSION_CODES.R &&
            SdkExtensions.getExtensionVersion(Build.VERSION_CODES.R) >= 2 -> true

        else -> {
            false
        }
    }
*/

/*
// Check extension version
fun isExtensionVersion2OrHigherAvailable(): Boolean {
    return SdkExtensions.getExtensionVersion(Build.VERSION_CODES.R) >= 2
    // Safely use extension APIs that are available with Android 11 (API level 30) Extensions Version 2, such as Photo Picker.
}
*/

// Older way of checking the availability of PhotoPicker
fun isPhotoPickerAvailableBasedOnSDKVersion(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

fun isPhotoPickerAvailableDeprecatedWay(): Boolean = ActivityResultContracts.PickVisualMedia.isPhotoPickerAvailable()

// https://developer.android.com/jetpack/androidx/releases/activity?authuser=1#1.7.0-alpha04
fun isPhotoPickerAvailableRecommendedWay(context: Context): Boolean {
    return ActivityResultContracts.PickVisualMedia.isPhotoPickerAvailable(context = context)
}

const val TAG = "SDKExtensions"
