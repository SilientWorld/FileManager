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

class DocumentModel(document: File) {
  val name: String = document.name
  val nameWithoutExt: String = document.nameWithoutExtension

  init {
    if (!document.isFile) {
      throw RuntimeException("No such document")
    }
  }
}

class DocumentAdapter(context: Context, list: ArrayList<DocumentModel>) :
  ArrayAdapter<DocumentModel>(context, 0, list) {
  private val settingStorage = SettingStorage(context)
  private val showExtension = settingStorage.get(settingStorage.showExtension)
  override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
    val listView = convertView ?: LayoutInflater.from(context).inflate(
      R.layout.document_card_item, parent, false
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