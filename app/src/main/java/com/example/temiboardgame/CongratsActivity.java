package com.example.temiboardgame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class CongratsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_congrats);

        TextView tvMessage = findViewById(R.id.tvCongratsMessage);

        // XML ID 변경 반영 (btnRestart)
        Button btnRestart = findViewById(R.id.btnRestart);

        tvMessage.setText("마지막칸 도착!\n축하합니다! 🎉");
        tvMessage.setTextSize(80); // 텍스트 크기 대폭 확대 (80sp)
        tvMessage.setTypeface(null, android.graphics.Typeface.BOLD); // 굵게 강조

        btnRestart.setOnClickListener(v -> {
            // 처음부터 다시 시작 (RESET_GAME)
            Intent intent = new Intent(CongratsActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("RESET_GAME", true);
            startActivity(intent);
            finish();
        });
    }
}
