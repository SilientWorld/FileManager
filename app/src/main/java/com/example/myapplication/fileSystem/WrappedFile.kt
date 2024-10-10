package com.example.myapplication.fileSystem

import android.content.Context
import android.icu.text.DecimalFormat
import android.text.format.DateFormat
import com.example.myapplication.fileSystem.byTypeFileLister.DocumentLister
import com.example.myapplication.fileSystem.byTypeFileLister.ImageLister
import com.example.myapplication.fileSystem.byTypeFileLister.MusicLister
import com.example.myapplication.fileSystem.byTypeFileLister.VideoLister
import java.io.File
import java.net.URLConnection
import java.nio.file.Files
import java.nio.file.attribute.BasicFileAttributes
import java.sql.Date
import java.sql.Timestamp
import java.time.Instant

class WrappedFile(private val f: File, skipCalculateDirectorySize: Boolean = false) {
  companion object {
    fun getSizeString(size: ULong): String {
      var sizeFirst = size
      var sizeLast = 0
      var unit = SizeUnit.B

      while (sizeFirst > 1024u && unit != SizeUnit.GB) {
        sizeLast = size.mod(1024u).toInt()
        sizeFirst /= 1024u
        unit = when (unit) {
          SizeUnit.B -> SizeUnit.KB
          SizeUnit.KB -> SizeUnit.MB
          SizeUnit.MB -> SizeUnit.GB
          else -> SizeUnit.B
        }
      }
      val s: Double = sizeFirst.toDouble() + sizeLast / 1000.0
      return "${DecimalFormat("#.##").format(s)} ${getUnit(unit)}"
    }

    private fun getUnit(unit: SizeUnit): String = when (unit) {
      SizeUnit.B -> "B"
      SizeUnit.KB -> "KB"
      SizeUnit.MB -> "MB"
      SizeUnit.GB -> "GB"
    }

    fun guessMime(ext: String): String {
      // Known ext
      val dotExt = ".$ext"
      if (dotExt.matches(ImageLister.regex)) {
        return "image/*"
      } else if (dotExt.matches(VideoLister.regex)) {
        return "video/*"
      } else if (dotExt.matches(MusicLister.regex)) {
        return "audio/*"
      } else if (dotExt.matches(DocumentLister.regex)) {
        return when (ext) {
          "xls" -> "application/vnd.ms-excel"
          "xlsx" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
          "doc" -> "application/msword"
          "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
          "ppt" -> "application/vnd.ms-powerpoint"
          "pptx" -> "application/vnd.openxmlformats-officedocument.presentationml.presentation"
          "txt" -> "text/plain"
          "htm", "html" -> "text/html"
          "pdf" -> "application/pdf"
          else -> "application/*"
        }
      } else return URLConnection.guessContentTypeFromName("f.$ext") ?: "text/plain"
    }
  }

  enum class Type {
    FILE,
    DIRECTORY
  }

  private enum class SizeUnit {
    B,
    KB,
    MB,
    GB
  }

  val name: String
  val nameWithoutExt: String
  val path: String
  val type: Type
  val lastModifiedTime: Instant
  val mime: String

  private var isSizeCalculated = true
  var size: Long
    private set

  init {
    if (!f.exists()) {
      throw RuntimeException("Cannot find file")
    }
    name = f.name
    nameWithoutExt = f.nameWithoutExtension
    path = f.path
    type = when (f.isDirectory) {
      true -> Type.DIRECTORY
      false -> Type.FILE
    }

    size = if (type == Type.FILE) {
      f.length()
    } else {
      if (skipCalculateDirectorySize) {
        isSizeCalculated = false
        0
      } else {
        getFolderSize(f)
      }
    }

    val attr: BasicFileAttributes = Files.readAttributes(
      f.toPath(),
      BasicFileAttributes::class.java
    )
    lastModifiedTime = attr.lastModifiedTime().toInstant()

    mime = if (f.isDirectory){
      "dir"
    }else {
      guessMime(f.extension)
    }
  }

  fun getSizeString(): String {
    if (size == 0L) {
      if (type == Type.DIRECTORY) {
        if (!isSizeCalculated){
          // Calculate Size
          size = getFolderSize(f)
          isSizeCalculated = true
        }else{
          return "0B"
        }
      } else {
        return "未知"
      }
    }
    return getSizeString(size.toULong())
  }

  fun getModifiedTimeString(context: Context): String {
    val ts = Timestamp.from(lastModifiedTime)
    val df = DateFormat.getDateFormat(context)
    val tf = DateFormat.getTimeFormat(context)
    val date = Date(ts.time)
    return df.format(date) + " " + tf.format(date)
  }
}