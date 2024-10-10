package com.example.myapplication.fileSystem.byTypeFileLister

import android.os.Environment
import com.example.myapplication.fileSystem.WrappedFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class DocumentLister : Lister() {
  companion object {
    val instance by lazy { DocumentLister() }
    val directories = listOf("Documents", "Download")
    val regex = "\\.((xls|doc|ppt)(x|)|txt|htm(l|)|pdf)".toRegex()
  }

  val documentList = mutableListOf<String>()

  fun initialize(onFinished: (() -> Unit)? = null) {
    documentList.clear()
    CoroutineScope(Dispatchers.IO).launch {
      directories.forEach { dir ->
        walkDir(
          File("${Environment.getExternalStorageDirectory().path}/${dir}"),
          documentList,
          regex
        )
      }
      onFinished?.invoke()
    }
    return
  }

  fun dateOrderedList(): List<String> {
    val wrappedFileList = mutableListOf<WrappedFile>()
    documentList.forEach { wrappedFileList.add(WrappedFile(File(it))) }
    wrappedFileList.sortBy { it.lastModifiedTime }
    val result = mutableListOf<String>()
    wrappedFileList.forEach { result.add(it.path) }
    return result
  }

  fun sizeOrderedList(): List<String> {
    val wrappedFileList = mutableListOf<WrappedFile>()
    documentList.forEach { wrappedFileList.add(WrappedFile(File(it))) }
    wrappedFileList.sortBy { it.size }
    val result = mutableListOf<String>()
    wrappedFileList.forEach { result.add(it.path) }
    return result
  }

  fun getFullSize(): ULong {
    var size = 0UL
    val wrappedFileList = mutableListOf<WrappedFile>()
    documentList.forEach { wrappedFileList.add(WrappedFile(File(it))) }
    wrappedFileList.forEach { size += it.size.toUInt() }
    return size
  }
}