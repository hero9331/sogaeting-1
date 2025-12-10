package com.example.temiboardgame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.text.TextPaint;
import android.widget.Button;
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private TextView tvDiceValue;
    private TextView tvPosition;
    private Button btnRollDice;

    // 게임 상태
    private int currentPosition = 1;
    private boolean skipTurn = false;  // 감옥(4번 칸) → 한 턴 쉬기

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvDiceValue = findViewById(R.id.tvDiceValue);
        tvPosition = findViewById(R.id.tvPosition);
        btnRollDice = findViewById(R.id.btnRollDice);

        // 텍스트 그라데이션
        TextPaint paint = tvDiceValue.getPaint();
        Shader textShader = new LinearGradient(
                0, 0, 0, tvDiceValue.getTextSize(),
                new int[]{
                        Color.parseColor("#ff9088"),
                        Color.parseColor("#ff211b")
                },
                null, Shader.TileMode.CLAMP);
        tvDiceValue.getPaint().setShader(textShader);

        // 이전 Activity에서 돌아왔을 때 상태 받기
        Intent receivedIntent = getIntent();
        if (receivedIntent != null) {
            currentPosition = receivedIntent.getIntExtra("position", 1);
            skipTurn = receivedIntent.getBooleanExtra("skipTurn", false);
        }

        updateUI();

        btnRollDice.setOnClickListener(v -> rollDiceAndMove());
    }

    private void rollDiceAndMove() {

        // 감옥 턴 스킵
        if (skipTurn) {
            skipTurn = false;
            goToTile();
            return;
        }

        // 주사위 (1~3)
        Random random = new Random();
        int diceNumber = random.nextInt(3) + 1;
        tvDiceValue.setText(String.valueOf(diceNumber));

        int newPosition = currentPosition + diceNumber;

        // ===========================================================
        // 🔥 규칙 1: 주사위 기준으로 12 초과 → 게임 종료
        // ===========================================================
        if (newPosition > 12) {

            currentPosition = 1;  // 시작 칸으로 이동
            TemiController.moveToPosition(1);
            updateUI();

            Intent finishIntent = new Intent(MainActivity.this, CongratsActivity.class);
            sendGameState(finishIntent);
            startActivity(finishIntent);
            finish();
            return;
        }

        // ===========================================================
        // 🔥 규칙 2: 12 이하인 경우에만 위치 업데이트
        // ===========================================================
        currentPosition = newPosition;

        // Temi 이동
        TemiController.moveToPosition(currentPosition);
        updateUI();

        // ===========================================================
        // 🔥 특수칸 처리 (순수 이동만 적용)
        // ===========================================================

        // ① 감옥 (4번)
        if (currentPosition == 4) {
            skipTurn = true;
            goToTile();
            return;
        }

        // ② 앞으로 1칸 이동 (5, 8)
        if (currentPosition == 5 || currentPosition == 8) {

            currentPosition += 1;
            if (currentPosition > 12) currentPosition = 1;

            TemiController.moveToPosition(currentPosition);
            updateUI();

            goToTile();
            return;
        }

        // ③ 앞으로 2칸 이동 (11)
        if (currentPosition == 11) {

            currentPosition += 2;

            // 특수칸 이동은 종료 조건 아님 → 1로 순환
            if (currentPosition > 12) currentPosition = 1;

            TemiController.moveToPosition(currentPosition);
            updateUI();

            goToTile();
            return;
        }

        // 일반 칸 → TileActivity 이동
        goToTile();
    }

    private void goToTile() {
        Intent intent = new Intent(MainActivity.this, TileActivity.class);
        sendGameState(intent);
        startActivity(intent);
        finish();
    }

    private void sendGameState(Intent intent) {
        intent.putExtra("position", currentPosition);
        intent.putExtra("skipTurn", skipTurn);
    }

    private void updateUI() {
        tvPosition.setText("현재 칸: " + currentPosition);
    }
}
