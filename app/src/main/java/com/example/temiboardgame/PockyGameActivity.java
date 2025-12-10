package com.example.temiboardgame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class PockyGameActivity extends AppCompatActivity {

    private TextView tvGameTitle;
    private TextView tvIng;
    private Button btnEndGame;

    private int position;
    private boolean skipTurn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_play);

        tvGameTitle = findViewById(R.id.tvGameTitle);
        tvIng = findViewById(R.id.tvIng);
        btnEndGame = findViewById(R.id.btnEndGame);

        Intent receivedIntent = getIntent();
        position = receivedIntent.getIntExtra("position", 0);
        skipTurn = receivedIntent.getBooleanExtra("skipTurn", false);

        tvGameTitle.setText("인간 빼빼로");
        tvIng.setText("서로 가까이 다가가세요... 📏\n(초음파 거리 측정 중)");

        btnEndGame.setOnClickListener(v -> {
            goToResult();
        });
    }

    private void goToResult() {
        Intent goResult = new Intent(PockyGameActivity.this, ResultActivity.class);
        goResult.putExtra("position", position);
        goResult.putExtra("skipTurn", skipTurn);
        startActivity(goResult);
        finish();
    }
}
