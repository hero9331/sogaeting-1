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
import android.widget.Toast;
import android.app.AlertDialog;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private TextView tvDiceValue;
    private TextView tvPosition;
    private Button btnRollDice;

    // 게임 상태
    private int currentPosition = 1;
    private boolean skipTurn = false; // 감옥(4번 칸) → 한 턴 쉬기

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
                new int[] {
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

        // [테스트용] 시간 맞추기 게임 바로 가기
        findViewById(R.id.btnTestTimeGame).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TimeGameActivity.class);
            intent.putExtra("position", 10); // 임의의 칸 번호
            startActivity(intent);
        });

        // [테스트용] 반응 속도 게임 바로 가기
        findViewById(R.id.btnTestReactionGame).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LightReactionGameActivity.class);
            intent.putExtra("position", 3); // 임의의 칸 번호
            startActivity(intent);
        });

        // [테스트용] 압력 측정 게임 바로 가기
        findViewById(R.id.btnTestPressureGame).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PressureGameActivity.class);
            intent.putExtra("position", 6);
            startActivity(intent);
        });

        // [테스트용] 빼빼로 게임 바로 가기
        findViewById(R.id.btnTestPockyGame).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PockyGameActivity.class);
            intent.putExtra("position", 4);
            startActivity(intent);
        });

        // [테스트용] 심박수 게임 바로 가기
        findViewById(R.id.btnTestHeartRateGame).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HeartRateGameActivity.class);
            intent.putExtra("position", 5);
            startActivity(intent);
        });
    }

    private void rollDiceAndMove() {
        // 중복 클릭 방지
        btnRollDice.setEnabled(false);

        // 주사위 굴리는 효과 (애니메이션)
        final int[] animationCount = { 0 };
        final int maxAnimationSteps = 15; // 숫자가 바뀌는 횟수

        android.os.Handler handler = new android.os.Handler(android.os.Looper.getMainLooper());

        Runnable diceAnimation = new Runnable() {
            @Override
            public void run() {
                Random random = new Random();
                // 애니메이션 중 보여줄 임시 숫자 (1~3)
                int tempDice = random.nextInt(3) + 1;
                tvDiceValue.setText(String.valueOf(tempDice));

                animationCount[0]++;

                if (animationCount[0] < maxAnimationSteps) {
                    // 아직 애니메이션 중 -> 100ms 뒤에 다시 실행
                    tvDiceValue.setTextSize(150); // 기본 크기
                    handler.postDelayed(this, 100);
                } else {
                    // 애니메이션 종료 -> 최종 숫자 확정 및 강조 효과!
                    int finalDice = random.nextInt(3) + 1;
                    tvDiceValue.setText(String.valueOf(finalDice));

                    // 💥 팍! 커지는 효과
                    tvDiceValue.setTextSize(200);

                    // 최종 숫자를 확인하도록 잠시 대기 후 이동 로직 실행
                    handler.postDelayed(() -> {
                        // 크기 원상 복구 및 이동
                        tvDiceValue.setTextSize(150);
                        processMove(finalDice);
                    }, 1000); // 1초 대기
                }
            }
        };

        // 애니메이션 시작
        handler.post(diceAnimation);
    }

    private void processMove(int diceNumber) {
        int newPosition = currentPosition + diceNumber;

        // ===========================================================
        // 🔥 규칙 1: 주사위 기준으로 12 초과 → 게임 종료
        // ===========================================================
        if (newPosition > 12) {

            currentPosition = 1; // 시작 칸으로 이동
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
            goToIsland();
            return;
        }

        // ② 앞으로 1칸 이동 (5, 8, 11)
        if (currentPosition == 5 || currentPosition == 8 || currentPosition == 11) {

            // 먼저 해당 칸(5, 8, 11)으로 이동했다는 것을 보여줌
            TemiController.moveToPosition(currentPosition);
            updateUI();

            // 별도의 보너스 화면으로 이동 (Activity 전환)
            Intent intent = new Intent(MainActivity.this, BonusMoveActivity.class);
            intent.putExtra("position", currentPosition);
            startActivity(intent);
            finish();
            return;
        }

        /*
         * // ③ 앞으로 2칸 이동 (11) - 삭제됨
         * ...
         */

        // 일반 칸 → TileActivity 이동
        goToTile();
    }

    // 보너스 이동 처리 함수 분리
    private void moveExtraOneStep() {
        currentPosition += 1;
        if (currentPosition > 12)
            currentPosition = 1;

        TemiController.moveToPosition(currentPosition);
        updateUI();

        goToTile();
    }

    private void goToTile() {
        // 칸별 게임 실행 로직
        Intent intent;

        // 4번(감옥), 5/8/11번(이동)은 이미 processMove에서 처리했으므로 여기서는 게임 위주로 배치
        switch (currentPosition) {
            case 3:
                // [3번 칸] 반응 속도 게임
                intent = new Intent(MainActivity.this, LightReactionGameActivity.class);
                break;
            case 5:
                // [5번 칸] 심박수 게임 (보너스로 이동한 칸이라도 게임 실행)
                intent = new Intent(MainActivity.this, HeartRateGameActivity.class);
                break;
            case 6:
                // [6번 칸] 압력 측정 게임
                intent = new Intent(MainActivity.this, PressureGameActivity.class);
                break;
            case 7:
                // [7번 칸] 빼빼로 게임
                intent = new Intent(MainActivity.this, PockyGameActivity.class);
                break;
            case 10:
                // [10번 칸] 시간 맞추기 게임
                intent = new Intent(MainActivity.this, TimeGameActivity.class);
                break;
            default:
                // [그 외] 일반 설명 칸
                intent = new Intent(MainActivity.this, TileActivity.class);
                break;
        }

        sendGameState(intent);
        startActivity(intent);
        finish();
    }

    private void goToIsland() {
        Intent intent = new Intent(MainActivity.this, IslandActivity.class);
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
