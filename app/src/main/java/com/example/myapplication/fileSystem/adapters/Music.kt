package com.example.myapplication.fileSystem.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.myapplication.R
import com.example.myapplication.SettingStorage
import java.io.File

class MusicModel(music: File) {
  val name: String = music.nameWithoutExtension // 歌曲就不放扩展名了
  val nameWithoutExt: String = music.nameWithoutExtension

  init {
    if (!music.isFile) {
      throw RuntimeException("No such Video")
    }
  }
}

class MusicAdapter(context: Context, list: ArrayList<MusicModel>) :
  ArrayAdapter<MusicModel>(context, 0, list) {
  private val settingStorage = SettingStorage(context)
  private val showExtension = settingStorage.get(settingStorage.showExtension)
  override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
    val listView = convertView ?: LayoutInflater.from(context).inflate(
      R.layout.music_card_item, parent, false
    )
    val model = getItem(position) ?: throw RuntimeException()
    val card = listView.findViewById<TextView>(R.id.iconButton)
    card.text = if (showExtension != false) {
      model.name
    } else {
      model.nameWithoutExt
    }
    return listView
  }
}