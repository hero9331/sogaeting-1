package com.example.temiboardgame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class BonusMoveActivity extends AppCompatActivity {

    private Button btnMove;
    private int startPosition; // [누락된 변수 복구]

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bonus_move);

        btnMove = findViewById(R.id.btnMove);

        // 현재 위치 받아오기 [누락된 로직 복구]
        startPosition = getIntent().getIntExtra("position", 1);

        // 초기화 버튼 연결
        Button btnReset = findViewById(R.id.btnResetGame);
        if (btnReset != null) {
            btnReset.setOnClickListener(v -> {
                Intent intent = new Intent(BonusMoveActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("RESET_GAME", true);
                startActivity(intent);
                finish();
            });
        }

        btnMove.setOnClickListener(v -> {
            // 메인 액티비티로 돌아가서 "한 칸 더 이동" 로직 수행 요청
            Intent goMain = new Intent(BonusMoveActivity.this, MainActivity.class);
            goMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            goMain.putExtra("BONUS_MOVE", true); // 플래그 전달
            goMain.putExtra("position", startPosition); // [중요] 현재 위치도 같이 전달!
            startActivity(goMain);
            finish();
        });
    }
}
