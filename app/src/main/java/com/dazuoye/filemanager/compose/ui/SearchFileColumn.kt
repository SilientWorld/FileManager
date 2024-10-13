package com.dazuoye.filemanager.compose.ui

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.dazuoye.filemanager.R
import com.dazuoye.filemanager.fileSystem.WrappedFile
import com.dazuoye.filemanager.fileSystem.WrappedFile.Type
import com.dazuoye.filemanager.fileSystem.searchFile
import com.dazuoye.filemanager.main_page
import com.dazuoye.filemanager.utils.AlertHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class SearchFileColumn(
  val context: Context,
  private val searchTypeName: String,
  private val searchRegex: Regex?
) {
  private val fileList = mutableStateListOf<WrappedFile>()
  private val searchText = mutableStateOf("")

  @Composable
  fun Draw() {
    var list by remember { mutableStateOf<List<String>>(emptyList()) }
    var isOkay by remember { mutableStateOf(false) }
    var sortByTime by remember { mutableStateOf(true) }

    LaunchedEffect(isOkay, list,sortByTime) {
      isOkay = false
      fileList.clear()
      val wfList = list.map { WrappedFile(File(it)) }
      if (sortByTime) {
        fileList.addAll(wfList.sortedBy { it.lastModifiedTime })
      } else {
        fileList.addAll(wfList.sortedBy { it.size })
      }
      isOkay = true
    }

    Column(
      modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding()
        .navigationBarsPadding()
        .background(Color(context.getColor(R.color.WhiteSmoke)))
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(
          onClick = {
            val intent = Intent(
              context,
              main_page::class.java
            )
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            context.startActivity(intent)
          }
        ) {
          Image(
            imageVector = ImageVector.vectorResource(R.drawable.ic_left_arrow), "back"
          )
        }

        Text(
          text = context.getString(R.string.search_result, searchTypeName),
          fontSize = 28.sp,
          modifier = Modifier
            .padding(start = 10.dp)
            .padding(vertical = 5.dp),
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.weight(1f))

        IconButton(
          onClick = {
            sortByTime = !sortByTime
            CoroutineScope(Dispatchers.Main).launch {
              Toast.makeText(
                context, context.getString(
                  if (sortByTime) {
                    R.string.sort_by_time
                  } else {
                    R.string.sort_by_size
                  }
                ), Toast.LENGTH_SHORT
              ).show()
            }
          },
        ) {
          Image(
            imageVector = ImageVector.vectorResource(
              if (sortByTime) {
                R.drawable.baseline_access_time_24
              } else {
                R.drawable.baseline_storage_24
              }
            ), "sortMethod"
          )
        }

      }
      if (!isOkay) {
        Column(
          modifier = Modifier.fillMaxSize(),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center
        ) {
          Text(
            text = context.getString(R.string.loading),
            fontSize = 34.sp
          )
        }
      } else {
        DrawColumns(fileList,
          searchText = searchText.value,
          onSearch = { name, searchWhat ->
            searchText.value = searchWhat
            searchFile(name, searchRegex) {
              list = it
              CoroutineScope(Dispatchers.Main).launch {
                if (list.isEmpty()) {
                  Toast.makeText(
                    context,
                    context.getString(R.string.search_no_result), Toast.LENGTH_SHORT
                  ).show()
                } else {
                  Toast.makeText(
                    context,
                    context.getString(R.string.search_some_result, list.size), Toast.LENGTH_SHORT
                  ).show()
                }
                isOkay = !isOkay
              }
            }
          }
        ) {
          val file = File(it)
          if (file.isFile) {
            val uri = FileProvider.getUriForFile(
              context,
              context.packageName + ".provider",
              file
            )
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(uri, WrappedFile(file).mime)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            context.startActivity(intent)
          }
        }
      }
    }
  }

  @Composable
  private fun DrawColumns(
    fileList: List<WrappedFile>,
    update: (() -> Unit)? = null,
    searchText: String = "",
    onSearch: ((String, String) -> Unit)? = null,
    onItemClick: ((String) -> Unit)? = null
  ) {
    LazyColumn(
      modifier = Modifier.padding(vertical = 5.dp)
    ) {
      // 最顶上那个
      item {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          var searchInput by remember { mutableStateOf(searchText) }

          TextField(
            value = searchInput,
            maxLines = 1,
            onValueChange = { searchInput = it },
            modifier = Modifier
              .padding(horizontal = 15.dp, vertical = 10.dp)
              .fillParentMaxWidth(0.9f),
            colors = TextFieldDefaults.colors(
              focusedContainerColor = Color(0xFFFFFAFA),
              unfocusedContainerColor = Color.White,
              focusedIndicatorColor = Color(0xFF03A9F4),
              unfocusedIndicatorColor = Color.Transparent
            ),
            textStyle = TextStyle(fontSize = 18.sp),
            trailingIcon = {
              Image(
                ImageVector.vectorResource(R.drawable.ic_search),
                context.getString(R.string.search),
                modifier = Modifier
                  .clickable {
                    if (searchInput.isNotEmpty()) {
                      if (searchInput == "." || searchInput == "..") {
                        Toast
                          .makeText(
                            context,
                            context.getString(R.string.error_search_input_illegal),
                            Toast.LENGTH_SHORT
                          )
                          .show()
                      }
                      onSearch?.invoke(searchInput, searchInput)
                    } else {
                      Toast
                        .makeText(
                          context,
                          context.getString(R.string.error_need_search_input), Toast.LENGTH_SHORT
                        )
                        .show()
                    }
                  }
              )
            }
          )
        }
      }

      // 下面的内容
      items(fileList) { file ->
        FileSingleView(
          file,
          onItemClick = onItemClick
        )
      }
    }
  }

  @Composable
  private fun FileSingleView(
    file: WrappedFile,
    onItemClick: ((String) -> Unit)? = null
  ) {

    Row(
      modifier = Modifier
        .fillMaxWidth()
        .border(
          width = Dp.Hairline,
          color = Color.Gray,
          shape = RectangleShape
        )
        .padding(vertical = 3.dp)
        .clickable {
          onItemClick?.invoke(file.path)
        },
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Image(
        ImageVector.vectorResource(
          when (file.mime.split('/').first()) {
            "dir" -> R.drawable.type_directory
            "image" -> R.drawable.type_image
            "video" -> R.drawable.type_video
            "audio" -> R.drawable.type_audio
            else -> R.drawable.type_file
          }
        ), file.mime,
        modifier = Modifier
          .padding(horizontal = 8.dp)
      )
      Column(
        modifier = Modifier
          .padding(horizontal = 15.dp)
          .fillMaxWidth(0.8f)
      ) {
        Text(
          text = file.name,
          fontSize = 24.sp,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
        Text(
          text = file.getModifiedTimeString(context),
          fontSize = 15.sp,
          color = Color.Gray,
          maxLines = 1
        )
      }

      Spacer(Modifier.weight(1f))

      IconButton(
        onClick = {
          if (file.type == Type.FILE) { // 普通文件
            AlertHelper.showOnlyInfoNewAlert(context,
              onInfo = {
                AlertHelper.showFileInfoAlert(context, file.path)
              }
            )
          }
        },
        modifier = Modifier.padding(horizontal = 10.dp)
      ) {
        Image(
          ImageVector.vectorResource(R.drawable.outline_info_24), "info"
        )
      }
    }

  }
}