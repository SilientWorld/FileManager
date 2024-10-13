package com.dazuoye.filemanager.compose

import android.Manifest.permission
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import com.dazuoye.filemanager.BuildConfig
import com.dazuoye.filemanager.R
import com.dazuoye.filemanager.fileSystem.byTypeFileLister.DocumentLister
import com.dazuoye.filemanager.fileSystem.byTypeFileLister.ImageLister
import com.dazuoye.filemanager.fileSystem.byTypeFileLister.MusicLister
import com.dazuoye.filemanager.fileSystem.byTypeFileLister.VideoLister
import com.dazuoye.filemanager.main_page

class RequirePermissionActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    val activity = this
    window.statusBarColor = getColor(R.color.WhiteSmoke)
    setContent {
      Column(
        modifier = Modifier
          .statusBarsPadding()
          .fillMaxHeight(0.9f)
          .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
      ) {
        Text(
          text = getString(
            if (VERSION.SDK_INT >= VERSION_CODES.R) {
              R.string.require_manage_storage
            } else {
              R.string.require_permission_readwrite
            }
          ),
          modifier = Modifier.padding(vertical = 10.dp),
          fontSize = 30.sp
        )
        Button(
          onClick = { // Ask for permission
            if (VERSION.SDK_INT >= VERSION_CODES.R) {
              if (!Environment.isExternalStorageManager()) {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                val uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                intent.setData(uri)
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
              }
            } else { // for legacy system
              val permissions =
                arrayOf(permission.READ_EXTERNAL_STORAGE, permission.WRITE_EXTERNAL_STORAGE)
              ActivityCompat.requestPermissions(
                activity, permissions, 100
              )
            }
          },
          colors = ButtonColors(
            containerColor = Color(0xFF039BE5),
            contentColor = Color.White,
            disabledContainerColor = Color.Gray,
            disabledContentColor = Color.White
          )
        ) {
          Text(text = getString(R.string.give_permission))
        }
      }
    }
  }

  override fun onResume() {
    super.onResume()
    if (checkPermissions(this)) {
      val intent = Intent(this, main_page::class.java)
      intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
      startActivity(intent)
      initSystem()
      finish()
    }
  }
}

fun checkPermissions(context: Context): Boolean {
  // Check storage permission
  if (VERSION.SDK_INT >= VERSION_CODES.R) {
    // Check manage storage on R+
    if (!Environment.isExternalStorageManager()) {
      return false
    }
    if (VERSION.SDK_INT >= VERSION_CODES.TIRAMISU) {
      val perm33 = arrayOf(
        permission.READ_MEDIA_AUDIO,
        permission.READ_MEDIA_VIDEO,
        permission.READ_MEDIA_IMAGES
      )
      perm33.forEach {
        if (context.checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED) {
          return false
        }
      }
    }
  } else {
    val permissions = arrayOf(permission.READ_EXTERNAL_STORAGE, permission.WRITE_EXTERNAL_STORAGE)
    permissions.forEach {
      if (context.checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED) {
        return false
      }
    }
  }
  return true
}

fun initSystem() {
  ImageLister.instance.initialize()
  VideoLister.instance.initialize()
  MusicLister.instance.initialize()
  DocumentLister.instance.initialize()
}
