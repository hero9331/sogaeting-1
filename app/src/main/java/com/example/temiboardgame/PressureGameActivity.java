package com.example.temiboardgame;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PressureGameActivity extends AppCompatActivity {

    private TextView tvGameTitle;
    private TextView tvIng;
    private Button btnEndGame;

    private int position;
    private boolean skipTurn;

    // Firebase
    private DatabaseReference mDatabase;

    // 게임 로직
    private long successStartTime = 0;
    private boolean isInRange = false;
    private boolean isSuccess = false;
    private int currentPressure = 0; // 현재 압력값

    // 타이머 핸들러 (지속적인 시간 체크용)
    private Handler timerHandler = new Handler();
    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            if (isSuccess || !isInRange)
                return;

            long duration = System.currentTimeMillis() - successStartTime;
            double seconds = duration / 1000.0;

            // UI 실시간 업데이트 (값 + 시간)
            tvIng.setText(String.format("현재 압력: %d\n🔥 유지 중: %.1f초...", currentPressure, seconds));
            tvIng.setTextColor(Color.parseColor("#FF9800")); // 주황색

            if (duration >= 3000) { // 3초 달성
                handleSuccess();
            } else {
                // 0.1초 뒤에 다시 체크
                timerHandler.postDelayed(this, 100);
            }
        }
    };

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

        tvGameTitle.setText("악수 압력 맞추기 🤝");

        // 초기화 버튼 연결
        Button btnReset = findViewById(R.id.btnResetGame);
        if (btnReset != null) {
            btnReset.setOnClickListener(v -> {
                Intent intent = new Intent(PressureGameActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("RESET_GAME", true);
                startActivity(intent);
                finish();
            });
        }

        tvIng.setText("둘이 악수하여 압력을\n90 ~ 100 사이로\n3초간 유지하세요!");

        btnEndGame.setText("포기하기 (실패)");
        btnEndGame.setBackgroundColor(Color.GRAY);

        // Firebase 초기화
        try {
            mDatabase = FirebaseDatabase.getInstance("https://temiboardgame-60750-default-rtdb.firebaseio.com")
                    .getReference();
        } catch (Exception e) {
        }

        // 아두이노 센서값 수신
        if (mDatabase != null) {
            mDatabase.child("pressure_sensor").child("adc_raw").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (isSuccess)
                        return;

                    Object val = snapshot.getValue();
                    if (val != null) {
                        try {
                            int pressure = Integer.parseInt(val.toString());
                            checkPressure(pressure);
                        } catch (NumberFormatException e) {
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                }
            });
        }

        btnEndGame.setOnClickListener(v -> goToResult(false));
    }

    private void checkPressure(int pressure) {
        currentPressure = pressure; // 최신 값 저장

        // 목표 범위: 90 ~ 100
        if (pressure >= 90 && pressure <= 100) {
            if (!isInRange) {
                // 막 진입함 -> 타이머 시작
                isInRange = true;
                successStartTime = System.currentTimeMillis();
                timerHandler.post(timerRunnable); // 타이머 루프 시작
            }
            // (이미 루프가 돌고 있으면 currentPressure만 업데이트됨)
        } else {
            // 범위 벗어남 -> 리셋 및 타이머 중지
            isInRange = false;
            timerHandler.removeCallbacks(timerRunnable); // 타이머 중지

            if (pressure < 90) {
                tvIng.setText("현재 압력: " + pressure + "\n(더 세게 꽉 잡으세요! 💪)");
            } else {
                tvIng.setText("현재 압력: " + pressure + "\n(너무 세요! 살살... 😌)");
            }
            tvIng.setTextColor(Color.BLACK);
        }
    }

    private void handleSuccess() {
        isSuccess = true;
        timerHandler.removeCallbacks(timerRunnable);

        tvIng.setText("성공! 3초 유지 완료! 🎉");
        tvIng.setTextColor(Color.parseColor("#4CAF50")); // 초록색
        btnEndGame.setEnabled(false);

        new Handler().postDelayed(() -> goToResult(true), 1500);
    }

    private void goToResult(boolean isSuccessResult) {
        timerHandler.removeCallbacks(timerRunnable);

        Intent goResult = new Intent(PressureGameActivity.this, ResultActivity.class);
        goResult.putExtra("position", position);
        goResult.putExtra("skipTurn", skipTurn);
        goResult.putExtra("autoResult", isSuccessResult);
        startActivity(goResult);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 메모리 누수 방지
        timerHandler.removeCallbacks(timerRunnable);
    }
}
