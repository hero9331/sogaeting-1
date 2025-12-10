package com.example.temiboardgame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class EyeGameActivity extends AppCompatActivity {

    private TextView tvGameTitle;
    private TextView tvIng;
    private Button btnEndGame;

    private int position;
    private boolean skipTurn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_play); // 공통 레이아웃 사용

        tvGameTitle = findViewById(R.id.tvGameTitle);
        tvIng = findViewById(R.id.tvIng);
        btnEndGame = findViewById(R.id.btnEndGame);

        Intent receivedIntent = getIntent();
        position = receivedIntent.getIntExtra("position", 0);
        skipTurn = receivedIntent.getBooleanExtra("skipTurn", false);

        tvGameTitle.setText("눈싸움 게임");
        tvIng.setText("눈을 먼저 깜빡이는 사람이 패배! 👀\n(카메라가 눈을 감지합니다...)");

        btnEndGame.setOnClickListener(v -> {
            goToResult();
        });
    }

    private void goToResult() {
        Intent goResult = new Intent(EyeGameActivity.this, ResultActivity.class);
        goResult.putExtra("position", position);
        goResult.putExtra("skipTurn", skipTurn);
        startActivity(goResult);
        finish();
    }
}
