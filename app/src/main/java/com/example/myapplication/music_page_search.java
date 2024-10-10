package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class music_page_search extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.music_page_search);
        // 设置左箭头的点击事件，返回上一级页面
        ImageView leftArrowImageView = findViewById(R.id.leftArrowImageView);
        leftArrowImageView.setOnClickListener(v -> {
            Intent intent = new Intent(music_page_search.this, music_page.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });
        SearchView searchView = findViewById(R.id.searchMusic); // 确保使用正确的 ID

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // 处理搜索提交
                Toast.makeText(music_page_search.this, "搜索: " + query, Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // 处理搜索文本变化
                // 可以在这里添加过滤逻辑
                return false;
            }
        });
    }
}
