package com.example.temiboardgame;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.text.TextPaint;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.robotemi.sdk.Robot;
import com.robotemi.sdk.listeners.OnGoToLocationStatusChangedListener;

import java.util.Random;

public class MainActivity extends AppCompatActivity implements OnGoToLocationStatusChangedListener {

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

        // Robot 리스너 등록 (Temi가 이동을 완료했을 때 알림을 받기 위함)
        try {
            Robot.getInstance().addOnGoToLocationStatusChangedListener(this);
        } catch (Exception e) {
            // 에뮬레이터 등에서 실패 시 무시
        }

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

        // [테스트용] 게임 바로 가기 버튼들
        findViewById(R.id.btnTestTimeGame).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TimeGameActivity.class);
            intent.putExtra("position", 10);
            startActivity(intent);
        });
        findViewById(R.id.btnTestReactionGame).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LightReactionGameActivity.class);
            intent.putExtra("position", 3);
            startActivity(intent);
        });
        findViewById(R.id.btnTestPressureGame).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PressureGameActivity.class);
            intent.putExtra("position", 6);
            startActivity(intent);
        });
        findViewById(R.id.btnTestPockyGame).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PockyGameActivity.class);
            intent.putExtra("position", 4);
            startActivity(intent);
        });
        findViewById(R.id.btnTestHeartRateGame).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HeartRateGameActivity.class);
            intent.putExtra("position", 5);
            startActivity(intent);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            Robot.getInstance().removeOnGoToLocationStatusChangedListener(this);
        } catch (Exception e) {
        }
    }

    /**
     * Temi 이동 상태 변경 리스너
     * COMPLETE 상태(이동 완료)가 되면 비로소 게임 화면을 실행합니다.
     */
    @Override
    public void onGoToLocationStatusChanged(String location, String status, int descriptionId, String description) {
        if (status.equals("complete")) {
            runOnUiThread(() -> {
                // 도착 알림
                Toast.makeText(this, location + " 도착 완료!", Toast.LENGTH_SHORT).show();
                // 도착 후에 게임 실행
                goToTile();
            });
        }
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
                int tempDice = random.nextInt(3) + 1;
                tvDiceValue.setText(String.valueOf(tempDice));

                animationCount[0]++;

                if (animationCount[0] < maxAnimationSteps) {
                    tvDiceValue.setTextSize(150);
                    handler.postDelayed(this, 100);
                } else {
                    int finalDice = random.nextInt(3) + 1;
                    tvDiceValue.setText(String.valueOf(finalDice));
                    tvDiceValue.setTextSize(200);

                    handler.postDelayed(() -> {
                        tvDiceValue.setTextSize(150);
                        processMove(finalDice);
                    }, 1000);
                }
            }
        };
        handler.post(diceAnimation);
    }

    private void processMove(int diceNumber) {
        int newPosition = currentPosition + diceNumber;

        // 12 초과 처리 -> 처음으로
        if (newPosition > 12) {
            currentPosition = 1;
            TemiController.moveToPosition(1);
            updateUI();

            Intent finishIntent = new Intent(MainActivity.this, CongratsActivity.class);
            sendGameState(finishIntent);
            startActivity(finishIntent);
            finish();
            return;
        }

        currentPosition = newPosition;
        updateUI();

        // ----------------------------------------------------
        // [핵심] Temi 이동 가능 여부 체크
        // ----------------------------------------------------
        String targetLocation = TemiController.getLocationNameForPosition(currentPosition);

        // Temi에 해당 위치("1"~"8")가 저장되어 있는지 확인
        if (TemiController.isLocationSaved(targetLocation)) {
            // 저장된 위치 -> 이동 명령 내림 (도착 시 리스너가 goToTile 호출)
            TemiController.moveToPosition(currentPosition);
        } else {
            // 저장되지 않은 위치(9~12번 등) -> 이동 스킵하고 바로 게임 실행
            // 이렇게 해야 1~8번 외의 칸에서도 앱이 멈추지 않음!
            goToTile();
        }
    }

    private void updateUI() {
        tvPosition.setText("현재 칸: " + currentPosition);
    }

    private void goToTile() {
        Intent intent;

        switch (currentPosition) {
            case 3:
                intent = new Intent(MainActivity.this, LightReactionGameActivity.class);
                break;
            case 5:
                intent = new Intent(MainActivity.this, HeartRateGameActivity.class);
                break;
            case 6:
                intent = new Intent(MainActivity.this, PressureGameActivity.class);
                break;
            case 7:
                intent = new Intent(MainActivity.this, PockyGameActivity.class);
                break;
            case 10:
                intent = new Intent(MainActivity.this, TimeGameActivity.class);
                break;
            case 4: // 감옥
                skipTurn = true;
                intent = new Intent(MainActivity.this, IslandActivity.class);
                break;
            case 12: // 마지막 칸
                intent = new Intent(MainActivity.this, TileActivity.class);
                break;
            case 8: // 보너스 이동
            case 11:
                intent = new Intent(MainActivity.this, BonusMoveActivity.class);
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

    private void sendGameState(Intent intent) {
        intent.putExtra("position", currentPosition);
        intent.putExtra("skipTurn", skipTurn);
    }
}
