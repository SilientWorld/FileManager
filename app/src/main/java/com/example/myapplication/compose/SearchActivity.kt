package com.example.myapplication.compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.myapplication.fileSystem.byTypeFileLister.DocumentLister
import com.example.myapplication.fileSystem.byTypeFileLister.ImageLister
import com.example.myapplication.fileSystem.byTypeFileLister.MusicLister
import com.example.myapplication.fileSystem.byTypeFileLister.VideoLister

class SearchActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val type = intent.extras?.getString("type")
    val searchTypeRegex: Regex? = when (type) {
      "music" -> MusicLister.regex
      "image" -> ImageLister.regex
      "video" -> VideoLister.regex
      "document" -> DocumentLister.regex
      else -> null
    }

    enableEdgeToEdge()
    setContent {

    }
  }
}
