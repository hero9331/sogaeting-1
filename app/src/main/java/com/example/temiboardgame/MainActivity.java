package com.example.temiboardgame;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.text.TextPaint;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.robotemi.sdk.Robot;
import com.robotemi.sdk.TtsRequest;
// import com.robotemi.sdk.listeners.OnAsrListener; // 제거 (구버전 호환)
import com.robotemi.sdk.listeners.OnGoToLocationStatusChangedListener;
import com.robotemi.sdk.SttLanguage;
import androidx.annotation.NonNull;

import java.util.Random;

public class MainActivity extends AppCompatActivity implements Robot.AsrListener, OnGoToLocationStatusChangedListener {

    private TextView tvDiceValue;
    private TextView tvPosition;
    private Button btnRollDice;
    private LinearLayout llMapContainer;

    // 게임 상태
    private int currentPosition = 1;
    private boolean skipTurn = false;

    // 초기화 중인지 체크
    private boolean isResetting = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvDiceValue = findViewById(R.id.tvDiceValue);
        tvPosition = findViewById(R.id.tvPosition);
        btnRollDice = findViewById(R.id.btnRollDice);
        llMapContainer = findViewById(R.id.llMapContainer);

        initMiniMap();

        TextPaint paint = tvDiceValue.getPaint();
        Shader textShader = new LinearGradient(
                0, 0, 0, tvDiceValue.getTextSize(),
                new int[] { Color.parseColor("#ff9088"), Color.parseColor("#ff211b") },
                null, Shader.TileMode.CLAMP);
        tvDiceValue.getPaint().setShader(textShader);

        Intent receivedIntent = getIntent();
        processIntent(receivedIntent);

        updateUI();

        btnRollDice.setOnClickListener(v -> rollDiceAndMove());

        setupTestButtons();

        // [Temi SDK 설정]
        Robot robot = Robot.getInstance();
        robot.addAsrListener(this);
        robot.addOnGoToLocationStatusChangedListener(this);

        // 키오스크 모드 요청 (앱 고정)
        robot.requestToBeKioskApp();

        // 타이틀 바 숨기기 (몰입감)
        robot.hideTopBar(true);
    }

    // --- [음성 인식] ---
    @Override
    public void onAsrResult(@NonNull String asrResult, @NonNull SttLanguage sttLanguage) {
        String text = asrResult.trim();

        // "주사위", "굴려" 등 인식
        if (text.contains("주사위") || text.contains("굴려") || text.contains("출발") || text.contains("GO")) {
            runOnUiThread(() -> {
                if (btnRollDice.isEnabled()) {
                    Toast.makeText(this, "🗣️ 음성 명령 확인: " + text, Toast.LENGTH_SHORT).show();
                    rollDiceAndMove();
                }
            });
            Robot.getInstance().finishConversation();
        }
    }

    // --- [이동 상태 리스너] ---
    @Override
    public void onGoToLocationStatusChanged(String location, String status, int descriptionId, String description) {
        if (status.equals("complete")) {
            runOnUiThread(() -> {
                // 도착 알림
                Toast.makeText(this, location + "번 칸 도착!", Toast.LENGTH_SHORT).show();

                if (isResetting) {
                    isResetting = false;
                    return;
                }

                // 해당 칸의 게임 시작
                goToTile();
            });
        }
    }

    private void initMiniMap() {
        if (llMapContainer == null)
            return;
        llMapContainer.removeAllViews();

        for (int i = 1; i <= 13; i++) {
            TextView tv = new TextView(this);
            tv.setText(String.valueOf(i));
            tv.setGravity(Gravity.CENTER);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            tv.setTextColor(Color.GRAY);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(100, 100);
            params.setMargins(8, 0, 8, 0);
            tv.setLayoutParams(params);
            tv.setBackgroundResource(R.drawable.bg_white_round);
            llMapContainer.addView(tv);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        processIntent(intent);
    }

    private void processIntent(Intent intent) {
        if (intent != null) {
            if (intent.getBooleanExtra("RESET_GAME", false)) {
                resetGame();
                return;
            }
            if (intent.getBooleanExtra("BONUS_MOVE", false)) {
                if (intent.hasExtra("position"))
                    currentPosition = intent.getIntExtra("position", 1);
                processMove(1);
                return;
            }
            if (intent.hasExtra("position"))
                currentPosition = intent.getIntExtra("position", 1);
            skipTurn = intent.getBooleanExtra("skipTurn", false);
            updateUI();
        }
    }

    private void resetGame() {
        currentPosition = 1;
        skipTurn = false;
        isResetting = true;
        updateUI();

        // 로봇 초기 위치로 이동
        TemiController.moveToPosition(1);

        Toast.makeText(this, "게임이 초기화되었습니다 🔄", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Robot.getInstance().removeAsrListener(this);
        Robot.getInstance().removeOnGoToLocationStatusChangedListener(this);
    }

    private void rollDiceAndMove() {
        if (skipTurn) {
            Toast.makeText(this, "무인도(감옥)에 있어 이번 턴을 쉽니다 ㅠㅠ", Toast.LENGTH_SHORT).show();
            skipTurn = false;
            btnRollDice.setEnabled(true);
            return;
        }
        btnRollDice.setEnabled(false);

        // Temi 음성 안내 (활성화)
        Robot.getInstance().speak(TtsRequest.create("주사위를 굴립니다!", false));

        final int[] animationCount = { 0 };
        final int maxAnimationSteps = 15;
        android.os.Handler handler = new android.os.Handler(android.os.Looper.getMainLooper());

        tvDiceValue.setTextColor(Color.parseColor("#ff211b"));

        Runnable diceAnimation = new Runnable() {
            @Override
            public void run() {
                Random random = new Random();
                int tempDice = random.nextInt(3) + 1;
                tvDiceValue.setText(String.valueOf(tempDice));

                // 틱! 사운드
                tvDiceValue.playSoundEffect(android.view.SoundEffectConstants.CLICK);

                animationCount[0]++;

                if (animationCount[0] < maxAnimationSteps) {
                    tvDiceValue.setTextSize(150);
                    handler.postDelayed(this, 100);
                } else {
                    int finalDice = random.nextInt(3) + 1;
                    tvDiceValue.setText(String.valueOf(finalDice));
                    tvDiceValue.setTextSize(200);
                    // 띵! 사운드
                    tvDiceValue.playSoundEffect(android.view.SoundEffectConstants.NAVIGATION_DOWN);

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
        if (newPosition >= 13)
            newPosition = 13;

        currentPosition = newPosition;
        updateUI();

        if (currentPosition == 4)
            skipTurn = true;

        // 실제 로봇 이동 명령
        String targetLocation = TemiController.getLocationNameForPosition(currentPosition);

        if (TemiController.isLocationSaved(targetLocation)) {
            TemiController.moveToPosition(currentPosition);
        } else {
            // 저장되지 않은 위치 테스트용 (즉시 이동)
            goToTile();
        }
    }

    private void updateUI() {
        if (currentPosition >= 13) {
            tvPosition.setText("현재 칸: 13 (도착!)");
        } else {
            tvPosition.setText("현재 칸: " + currentPosition);
        }
        updateMiniMap();
    }

    private void updateMiniMap() {
        if (llMapContainer == null)
            return;

        for (int i = 0; i < llMapContainer.getChildCount(); i++) {
            TextView tv = (TextView) llMapContainer.getChildAt(i);
            int mapNum = i + 1;

            if (mapNum == currentPosition) {
                tv.setTextColor(Color.WHITE);
                tv.setBackgroundColor(Color.parseColor("#ff211b"));
                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                tv.setText("📍\n" + mapNum);
            } else {
                tv.setTextColor(Color.BLACK);
                tv.setBackgroundColor(Color.WHITE);
                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                tv.setText(String.valueOf(mapNum));
            }
        }

        final android.widget.HorizontalScrollView sv = findViewById(R.id.svMap);
        if (sv != null) {
            sv.post(() -> {
                int targetX = llMapContainer.getChildAt(currentPosition - 1).getLeft();
                sv.smoothScrollTo(targetX - 400, 0);
            });
        }
    }

    private void goToTile() {
        Intent intent = null;
        switch (currentPosition) {
            case 1:
                return;
            case 2:
                intent = new Intent(MainActivity.this, EyeGameActivity.class);
                break;
            case 3:
                intent = new Intent(MainActivity.this, LightReactionGameActivity.class);
                break;
            case 4:
                intent = new Intent(MainActivity.this, IslandActivity.class);
                break;
            case 5:
                intent = new Intent(MainActivity.this, BonusMoveActivity.class);
                break;
            case 6:
                intent = new Intent(MainActivity.this, PressureGameActivity.class);
                break;
            case 7:
                intent = new Intent(MainActivity.this, TimeGameActivity.class);
                break;
            case 8:
                intent = new Intent(MainActivity.this, BonusMoveActivity.class);
                break;
            case 9:
                intent = new Intent(MainActivity.this, PockyGameActivity.class);
                break;
            case 10:
                intent = new Intent(MainActivity.this, TimeGameActivity.class);
                break;
            case 11:
                intent = new Intent(MainActivity.this, BonusMoveActivity.class);
                break;
            case 12:
                intent = new Intent(MainActivity.this, LightReactionGameActivity.class);
                break;
            case 13:
                intent = new Intent(MainActivity.this, CongratsActivity.class);
                break;
            default:
                Toast.makeText(this, "쉬어가는 칸입니다 🌿", Toast.LENGTH_SHORT).show();
                return;
        }

        if (intent != null) {
            sendGameState(intent);
            startActivity(intent);
        }
    }

    private void sendGameState(Intent intent) {
        intent.putExtra("position", currentPosition);
        intent.putExtra("skipTurn", skipTurn);
    }

    private void setupTestButtons() {
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
            intent.putExtra("position", 9);
            startActivity(intent);
        });
        findViewById(R.id.btnTestHeartRateGame).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, EyeGameActivity.class);
            intent.putExtra("position", 2);
            startActivity(intent);
        });
        findViewById(R.id.btnResetGame).setOnClickListener(v -> {
            resetGame();
        });
    }
}
