package com.example.temiboardgame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ResultActivity extends AppCompatActivity {

    private Button btnSuccess;
    private Button btnFail;
    private Button btnRetry; // 새로 추가된 재도전 버튼
    private TextView tvAutoResult;

    private int position;
    private boolean skipTurn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        btnSuccess = findViewById(R.id.btnSuccess);
        btnFail = findViewById(R.id.btnFail);
        btnRetry = findViewById(R.id.btnRetry);
        tvAutoResult = findViewById(R.id.tvAutoResult);

        // 초기화 버튼 연결
        Button btnReset = findViewById(R.id.btnResetGame);
        if (btnReset != null) {
            btnReset.setOnClickListener(v -> {
                Intent intent = new Intent(ResultActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("RESET_GAME", true);
                startActivity(intent);
                finish();
            });
        }

        Intent receivedIntent = getIntent();
        position = receivedIntent.getIntExtra("position", 1);
        skipTurn = receivedIntent.getBooleanExtra("skipTurn", false);

        boolean hasAutoResult = receivedIntent.hasExtra("autoResult");

        if (hasAutoResult) {
            boolean isSuccess = receivedIntent.getBooleanExtra("autoResult", false);
            handleAutoResult(isSuccess);
        } else {
            setupManualButtons();
        }
    }

    private void handleAutoResult(boolean isSuccess) {
        // 모든 버튼 숨기기
        btnSuccess.setVisibility(View.GONE);
        btnFail.setVisibility(View.GONE);
        btnRetry.setVisibility(View.GONE);

        tvAutoResult.setVisibility(View.VISIBLE);

        if (isSuccess) {
            tvAutoResult.setText("미션 성공! 🎉");
            tvAutoResult.setTextColor(Color.parseColor("#4CAF50")); // 초록색
            // 성공 -> 3초 후 Main(다음 턴) 자동 이동
            new Handler().postDelayed(this::goToMain, 3000);
        } else {
            tvAutoResult.setText("미션 실패... 😢");
            tvAutoResult.setTextColor(Color.parseColor("#F44336")); // 빨간색

            // 실패 -> "다시 도전하기" 버튼 표시
            btnRetry.setVisibility(View.VISIBLE);

            // 클릭 시 게임 재시작
            btnRetry.setOnClickListener(v -> retryGame());
        }
    }

    private void setupManualButtons() {
        btnSuccess.setOnClickListener(v -> goToMain());
        // 수동 실패도 재도전으로
        btnFail.setOnClickListener(v -> retryGame());
    }

    private void goToMain() {
        if (isFinishing())
            return;

        Intent goDice = new Intent(ResultActivity.this, MainActivity.class);
        goDice.putExtra("position", position);
        goDice.putExtra("skipTurn", skipTurn);
        startActivity(goDice);
        finish();
    }

    // 실패 시 해당 게임 즉시 재시작 (TileActivity 안 거침)
    private void retryGame() {
        if (isFinishing())
            return;

        Intent intent;
        switch (position) {
            case 2: // 눈싸움 심박수
                intent = new Intent(ResultActivity.this, EyeGameActivity.class);
                break;
            case 3: // 불빛 반응
            case 12:
                intent = new Intent(ResultActivity.this, LightReactionGameActivity.class);
                break;
            case 6: // 압력
                intent = new Intent(ResultActivity.this, PressureGameActivity.class);
                break;
            case 7: // 시간
            case 10:
                intent = new Intent(ResultActivity.this, TimeGameActivity.class);
                break;
            case 9: // 인간 빼빼로
                intent = new Intent(ResultActivity.this, PockyGameActivity.class);
                break;

            default:
                intent = new Intent(ResultActivity.this, MainActivity.class);
                break;
        }

        intent.putExtra("position", position);
        intent.putExtra("skipTurn", skipTurn);
        startActivity(intent);
        finish();
    }
}