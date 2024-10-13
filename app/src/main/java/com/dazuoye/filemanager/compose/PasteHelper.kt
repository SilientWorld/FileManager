package com.dazuoye.filemanager.compose

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.channels.FileChannel

class PasteHelper {
  companion object {
    fun copyDirectory(sourceDir: File, destDir: File) {
      // creates the destination directory if it does not exist
      if (!destDir.exists()) {
        destDir.mkdirs()
      }

      // throws exception if the source does not exist
      require(sourceDir.exists()) { "sourceDir does not exist" }

      // throws exception if the arguments are not directories
      require(!(sourceDir.isFile || destDir.isFile)) { "Either sourceDir or destDir is not a directory" }

      copyDirectoryImpl(sourceDir, destDir)
    }

    private fun copyDirectoryImpl(sourceDir: File, destDir: File) {
      val items = sourceDir.listFiles()
      if (items != null && items.isNotEmpty()) {
        for (anItem: File in items) {
          if (anItem.isDirectory) {
            val newDir = File(destDir, anItem.name)
            newDir.mkdir()
            // copy the directory (recursive call)
            copyDirectory(anItem, newDir)
          } else {
            // copy the file
            val destFile = File(destDir, anItem.name)
            copySingleFile(anItem, destFile)
          }
        }
      }
    }

    private fun copySingleFile(sourceFile: File, destFile: File) {
      if (!destFile.exists()) {
        destFile.createNewFile()
      }
      var sourceChannel: FileChannel? = null
      var destChannel: FileChannel? = null

      try {
        sourceChannel = FileInputStream(sourceFile).channel
        destChannel = FileOutputStream(destFile).channel
        sourceChannel.transferTo(0, sourceChannel.size(), destChannel)
      } finally {
        sourceChannel?.close()
        destChannel?.close()
      }
    }
  }
}