package com.example.temiboardgame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color; // 추가됨
import android.graphics.LinearGradient; // 추가됨
import android.graphics.Shader; // 추가됨
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class GameFinishActivity extends AppCompatActivity {

    private int position;
    private int lapCount;
    private boolean skipTurn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish); // XML 파일 이름이 맞는지 꼭 확인하세요!

        Button btnRestart = findViewById(R.id.btnRestart);

        // 1. 데이터 받기
        Intent receivedIntent = getIntent();
        if (receivedIntent != null) {
            position = receivedIntent.getIntExtra("position", 1);
            lapCount = receivedIntent.getIntExtra("lapCount", 0);
            skipTurn = receivedIntent.getBooleanExtra("skipTurn", false);
        }


        // 4. 버튼 클릭 리스너
        btnRestart.setOnClickListener(v -> {
            Intent restart = new Intent(GameFinishActivity.this, MainActivity.class);
            // 모든 상태 초기화해서 보내기
            restart.putExtra("position", 1);
            restart.putExtra("lapCount", 0);
            restart.putExtra("skipTurn", false);

            // 이전 스택을 지우고 새로 시작 (뒤로가기 방지)
            restart.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(restart);
            finish();
        });
    }
}