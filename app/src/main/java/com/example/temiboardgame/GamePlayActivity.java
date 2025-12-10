package com.example.temiboardgame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class GamePlayActivity extends AppCompatActivity {

    private TextView tvGameTitle;
    private TextView tvIng; // 진행 메시지 뷰 추가
    private Button btnEndGame;

    private int position;
    private boolean skipTurn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_play);

        tvGameTitle = findViewById(R.id.tvGameTitle);
        tvIng = findViewById(R.id.tvIng); // 연결
        btnEndGame = findViewById(R.id.btnEndGame);

        // 데이터 받기
        Intent receivedIntent = getIntent();
        position = receivedIntent.getIntExtra("position", 0);
        skipTurn = receivedIntent.getBooleanExtra("skipTurn", false);

        // 게임 제목 설정
        String title = TileInfoProvider.getTitle(position);
        tvGameTitle.setText(title);

        // 게임 진행 메시지 설정 (데모)
        String ingMsg = TileInfoProvider.getIngMessage(position);
        tvIng.setText(ingMsg);

        // 게임 종료 버튼 -> 결과 선택 화면으로
        btnEndGame.setOnClickListener(v -> {
            Intent goResult = new Intent(GamePlayActivity.this, ResultActivity.class);
            goResult.putExtra("position", position);
            goResult.putExtra("skipTurn", skipTurn);
            startActivity(goResult);
            finish();
        });
    }
}
