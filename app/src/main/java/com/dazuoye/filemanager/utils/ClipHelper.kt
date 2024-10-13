package com.dazuoye.filemanager.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.dazuoye.filemanager.BuildConfig
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.synchronized
import java.io.File

class ClipHelper private constructor(context: Context) {
  companion object {
    private const val label = "${BuildConfig.APPLICATION_ID}\$ClipHelper"
    private const val ENCODE_LABEL = "ClipHelper"

    private var instance: ClipHelper? = null

    @OptIn(InternalCoroutinesApi::class)
    fun getInstance(context: Context): ClipHelper =
      instance ?: synchronized(this) {
        instance ?: ClipHelper(context).also { instance = it }
      }
  }

  private val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
  private val contentResolver: ContentResolver = context.contentResolver

  fun copy(file: File, context: Context) {
    val uri = FileProvider.getUriForFile(
      context,
      context.applicationContext.packageName + ".provider",
      file
    )
    val clip = ClipData.newUri(contentResolver, label, uri)
    clipboard.setPrimaryClip(clip)
  }

  fun paste(): Uri? {
    val clip = clipboard.primaryClip
    clip?.run {
      val item: ClipData.Item = getItemAt(0)
      return item.uri
    }
    return null
  }

  fun copyFolder(folder: String) {
    val clip = ClipData.newPlainText("SingleFolderCopy", "$ENCODE_LABEL:${folder}")
    clipboard.setPrimaryClip(clip)
  }

  fun pasteFolder(): String? {
    val clip = clipboard.primaryClip
    val content = clip?.run {
      val item: ClipData.Item = getItemAt(0)
      item.text
    }
    if (content != null) {
      if (content.startsWith(ENCODE_LABEL)) {
        return content.split(':').last()
      }
    }
    return null
  }
}