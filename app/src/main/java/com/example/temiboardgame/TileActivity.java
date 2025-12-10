package com.example.temiboardgame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class TileActivity extends AppCompatActivity {

    private TextView tvTileTitle;
    private TextView tvTileDescription;
    private Button btnGoResult;

    private int position;
    // private int lapCount;
    private boolean skipTurn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tile);

        tvTileTitle = findViewById(R.id.tvTileTitle);
        tvTileDescription = findViewById(R.id.tvTileDescription);
        btnGoResult = findViewById(R.id.btnGoResult);

        // MainActivity에서 보낸 게임 상태 받기
        Intent receivedIntent = getIntent();
        position = receivedIntent.getIntExtra("position", 0);
        // lapCount = receivedIntent.getIntExtra("lapCount", 0);
        skipTurn = receivedIntent.getBooleanExtra("skipTurn", false);

        String title = TileInfoProvider.getTitle(position);
        String desc = TileInfoProvider.getDescription(position);

        tvTileTitle.setText(title);
        tvTileDescription.setText(desc);

        // 버튼 텍스트 변경
        btnGoResult.setText("게임 시작 🎮");

        btnGoResult.setOnClickListener(v -> {
            // 게임 화면(GamePlayActivity)으로 이동
            Intent goGame = new Intent(TileActivity.this, GamePlayActivity.class);
            goGame.putExtra("position", position);
            goGame.putExtra("skipTurn", skipTurn);
            startActivity(goGame);
            finish();
        });
    }
}