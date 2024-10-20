package com.dazuoye.filemanager

import androidx.appcompat.app.AppCompatActivity
import com.dazuoye.filemanager.fileSystem.DeleteHelper.Companion.delete
import java.io.File

open class BaseActivity: AppCompatActivity() {


  override fun onDestroy() {
    super.onDestroy()
    val clipFile = File(this.cacheDir,"clipboard")
    if (clipFile.exists()){
      delete(clipFile.path)
    }
    System.gc()
  }
}