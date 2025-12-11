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
        setContentView(R.layout.activity_game_play);

        tvGameTitle = findViewById(R.id.tvGameTitle);
        tvIng = findViewById(R.id.tvIng);
        btnEndGame = findViewById(R.id.btnEndGame);

        Intent receivedIntent = getIntent();
        position = receivedIntent.getIntExtra("position", 0);
        skipTurn = receivedIntent.getBooleanExtra("skipTurn", false);

        tvGameTitle.setText("눈싸움 게임 👀");
        tvIng.setText("카메라가 얼굴을 찾고 있습니다...\n(준비되면 '눈 감지 테스트' 버튼 클릭)");

        btnEndGame.setText("눈 감음 감지 (Test)");

        initCamera();

        btnEndGame.setOnClickListener(v -> {
            onEyesClosedDetected();
        });
    }

    // 1. build.gradle에 ML Kit (Face Detection) 추가 필요

    private void initCamera() {
        // TODO: 여기서 카메라를 켜고 얼굴 탐지를 시작하는 코드를 작성하세요.
    }

    private void onEyesClosedDetected() {
        tvIng.setText("눈 감음 감지됨! 😵\n당신이 졌습니다!");
        btnEndGame.setText("결과 확인");
        btnEndGame.setOnClickListener(v -> goToResult());
    }

    private void goToResult() {
        Intent goResult = new Intent(EyeGameActivity.this, ResultActivity.class);
        goResult.putExtra("position", position);
        goResult.putExtra("skipTurn", skipTurn);
        startActivity(goResult);
        finish();
    }
}
