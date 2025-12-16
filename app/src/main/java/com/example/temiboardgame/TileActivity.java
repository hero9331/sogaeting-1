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
        skipTurn = receivedIntent.getBooleanExtra("skipTurn", false);

        String title = TileInfoProvider.getTitle(position);
        String desc = TileInfoProvider.getDescription(position);

        tvTileTitle.setText(title);
        tvTileDescription.setText(desc);

        // 버튼 텍스트 변경
        btnGoResult.setText("다음으로 👉");

        btnGoResult.setOnClickListener(v -> {
            // TileActivity는 설명을 보여주는 용도.
            // 여기서 '게임 시작' 버튼을 누르면 해당 게임 Activity로 이동하거나,
            // 게임이 없는 칸(시작점 등)인 경우 바로 Result(다음 턴)로 이동.

            Intent intent;
            switch (position) {
                case 2: // 눈싸움 심박수
                    intent = new Intent(TileActivity.this, EyeGameActivity.class);
                    break;
                case 3: // 불빛 반응
                case 12: // 불빛 반응
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

                // 특수 칸이나 게임이 없는 칸(1번 등)은 바로 결과 화면(턴 종료)으로 이동
                default:
                    intent = new Intent(TileActivity.this, ResultActivity.class);
                    break;
            }

            intent.putExtra("position", position);
            intent.putExtra("skipTurn", skipTurn);
            startActivity(intent);
            finish();
        });
    }
}