package com.example.temiboardgame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class IslandActivity extends AppCompatActivity {

    private Button btnConfirm;
    private int position;
    private boolean skipTurn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_island);

        btnConfirm = findViewById(R.id.btnConfirm);

        Intent receivedIntent = getIntent();
        position = receivedIntent.getIntExtra("position", 4);
        skipTurn = receivedIntent.getBooleanExtra("skipTurn", true);

        // 버튼 클릭 시에만 메인 화면으로 이동 (자동 전환 없음)
        btnConfirm.setOnClickListener(v -> {
            Intent goMain = new Intent(IslandActivity.this, MainActivity.class);
            goMain.putExtra("position", position);
            goMain.putExtra("skipTurn", skipTurn);
            startActivity(goMain);
            finish();
            overridePendingTransition(0, 0);
        });
    }
}
