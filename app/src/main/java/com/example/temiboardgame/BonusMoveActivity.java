package com.example.temiboardgame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class BonusMoveActivity extends AppCompatActivity {

    private Button btnMove;
    private int startPosition; // 5, 8, 11 등 시작 위치

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bonus_move);

        btnMove = findViewById(R.id.btnMove);

        Intent receivedIntent = getIntent();
        startPosition = receivedIntent.getIntExtra("position", 1);

        btnMove.setOnClickListener(v -> {
            // 다시 메인으로 돌아가서 실제 이동 처리하도록 함
            // 메인에서 onActivityResult나 플래그로 처리할 수도 있지만,
            // 여기서는 MainActivity를 새로 시작하면서 상태를 넘겨주는 방식으로 함
            // (기존 방식 유지: MainActivity가 켜지면서 위치 업데이트)

            int nextPosition = startPosition + 1;
            if (nextPosition > 12)
                nextPosition = 1;

            Intent goMain = new Intent(BonusMoveActivity.this, MainActivity.class);
            goMain.putExtra("position", nextPosition);
            goMain.putExtra("skipTurn", false); // 보너스 칸은 스킵 없음
            goMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(goMain);
            finish();
        });
    }
}
