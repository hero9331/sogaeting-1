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
            Intent intent;
            switch (position) {
                case 1: // 눈싸움
                    intent = new Intent(TileActivity.this, EyeGameActivity.class);
                    break;
                case 2: // 심박수
                    intent = new Intent(TileActivity.this, HeartRateGameActivity.class);
                    break;
                case 3: // 불빛 반응
                    intent = new Intent(TileActivity.this, LightReactionGameActivity.class);
                    break;
                case 6: // 압력
                    intent = new Intent(TileActivity.this, PressureGameActivity.class);
                    break;
                case 7: // 시간
                case 10: // 시간
                    intent = new Intent(TileActivity.this, TimeGameActivity.class);
                    break;
                case 9: // 인간 빼빼로
                    intent = new Intent(TileActivity.this, PockyGameActivity.class);
                    break;
                case 12: // 물 양
                    intent = new Intent(TileActivity.this, WaterGameActivity.class);
                    break;
                default: // 그 외 (혹시 모를 예외, 기본 GamePlayActivity)
                    intent = new Intent(TileActivity.this, GamePlayActivity.class);
                    break;
            }

            intent.putExtra("position", position);
            intent.putExtra("skipTurn", skipTurn);
            startActivity(intent);
            finish();
        });
    }
}