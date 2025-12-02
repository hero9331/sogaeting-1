package com.example.temiboardgame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color; // 추가됨
import android.graphics.LinearGradient; // 추가됨
import android.graphics.Shader; // 추가됨
import android.os.Bundle;
import android.text.TextPaint; // 추가됨
import android.widget.Button;
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private TextView tvDiceValue;
    private TextView tvPosition;
    private Button btnRollDice;

    // 게임 상태
    private int currentPosition = 1;   // 현재 칸
    private int lapCount = 0;          // 몇 바퀴 돌았는지
    private boolean skipTurn = false;  // 감옥(4번 칸) 때문에 한 턴 쉬어야 하면 true

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvDiceValue = findViewById(R.id.tvDiceValue);
        tvPosition = findViewById(R.id.tvPosition);
        btnRollDice = findViewById(R.id.btnRollDice);

        // ==========================================
        // ▼ [추가된 부분] 텍스트 그라데이션 효과 적용 ▼
        // ==========================================
        // tvDiceValue에 그라데이션 적용 (위: #ff9088 -> 아래: #ff211b)
        TextPaint paint = tvDiceValue.getPaint();
        Shader textShader = new LinearGradient(
                0, 0, 0, tvDiceValue.getTextSize(),
                new int[]{
                        Color.parseColor("#ff9088"), // 위쪽 색
                        Color.parseColor("#ff211b")  // 아래쪽 색
                },
                null, Shader.TileMode.CLAMP);
        tvDiceValue.getPaint().setShader(textShader);
        // ==========================================


        // ResultActivity / TileActivity 등에서 돌아올 때 상태 받기
        Intent receivedIntent = getIntent();
        if (receivedIntent != null) {
            currentPosition = receivedIntent.getIntExtra("position", 1);
            lapCount = receivedIntent.getIntExtra("lapCount", 0);
            skipTurn = receivedIntent.getBooleanExtra("skipTurn", false);
        }

        updateUI();

        btnRollDice.setOnClickListener(v -> rollDiceAndMove());
    }

    private void rollDiceAndMove() {

        // ✅ 감옥(4번 칸)에서 한 턴 쉬는 상태라면
        if (skipTurn) {
            // 이번 턴은 주사위 안 굴리고 바로 칸 설명 화면으로
            skipTurn = false; // 다음 턴부터는 정상 진행
            goToTile();
            return;
        }

        // ✅ 주사위 굴리기 (1~6)
        Random random = new Random();
        int diceNumber = random.nextInt(6) + 1;
        tvDiceValue.setText(String.valueOf(diceNumber));

        int oldPosition = currentPosition;
        currentPosition += diceNumber;

        // ✅ 12칸 보드 → 12 다음은 다시 1로 돌아가도록 순환 + 바퀴 수 계산
        if (currentPosition > 12) {
            // 몇 칸을 넘겼는지로 바퀴 수 계산(사실 주사위 최대 6이라 1바퀴씩만 증가)
            int lapsPassed = (currentPosition - 1) / 12;
            lapCount += lapsPassed;
            currentPosition = ((currentPosition - 1) % 12) + 1;
        }

        TemiController.moveToPosition(currentPosition);

        updateUI();

        // ✅ 3바퀴 돌았으면 게임 종료 화면으로 이동
        if (lapCount >= 3) {
            Intent finishIntent = new Intent(MainActivity.this, GameFinishActivity.class);
            sendGameState(finishIntent);
            startActivity(finishIntent);
            finish();
            return;
        }

        // ✅ 4번 칸(무인도/감옥) 도착 → 다음 턴 쉬기 세팅
        if (currentPosition == 4) {
            skipTurn = true;
        }

        // ✅ 12번 칸 도착 → 축하 화면으로 이동
        if (currentPosition == 12) {
            Intent congratsIntent = new Intent(MainActivity.this, CongratsActivity.class);
            sendGameState(congratsIntent);
            startActivity(congratsIntent);
            finish();
            return;
        }

        // 그 외에는 일반 칸 → TileActivity로 이동
        goToTile();
    }

    private void goToTile() {
        Intent intent = new Intent(MainActivity.this, TileActivity.class);
        sendGameState(intent);
        startActivity(intent);
        finish();
    }

    // 상태를 Intent에 넣어주는 공용 함수
    private void sendGameState(Intent intent) {
        intent.putExtra("position", currentPosition);
        intent.putExtra("lapCount", lapCount);
        intent.putExtra("skipTurn", skipTurn);
    }

    private void updateUI() {
        tvPosition.setText("현재 칸: " + currentPosition + " / " + lapCount + "바퀴");
    }
}