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

public class PockyGameActivity extends AppCompatActivity {

    private TextView tvGameTitle;
    private TextView tvIng;
    private Button btnEndGame;

    private int position;
    private boolean skipTurn;
    private DatabaseReference mDatabase;

    // 게임 로직
    private long successStartTime = 0;
    private boolean isInRange = false;
    private boolean isSuccess = false;
    private double currentDistance = 0.0; // 현재 거리 값

    // 타이머 핸들러
    private Handler timerHandler = new Handler();
    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            if (isSuccess || !isInRange)
                return;

            long duration = System.currentTimeMillis() - successStartTime;
            double seconds = duration / 1000.0;

            // UI 실시간 업데이트 (값 + 시간)
            tvIng.setText(String.format("현재 거리: %.1f cm\n💕 유지 중: %.1f초...", currentDistance, seconds));
            tvIng.setTextColor(Color.parseColor("#E91E63")); // 핑크색

            if (duration >= 3000) { // 3초 달성
                handleSuccess();
            } else {
                timerHandler.postDelayed(this, 100); // 0.1초 후 재실행
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

        tvGameTitle.setText("인간 빼빼로 📏");

        // 초기화 버튼 연결
        Button btnReset = findViewById(R.id.btnResetGame);
        if (btnReset != null) {
            btnReset.setOnClickListener(v -> {
                Intent intent = new Intent(PockyGameActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("RESET_GAME", true);
                startActivity(intent);
                finish();
            });
        }

        tvIng.setText("서로 가까이 붙어 10cm 이내를\n3초간 유지하세요!");

        btnEndGame.setText("포기하기 (실패)");
        btnEndGame.setBackgroundColor(Color.GRAY);

        try {
            mDatabase = FirebaseDatabase
                    .getInstance("https://temiboardgame-60750-default-rtdb.firebaseio.com").getReference();
        } catch (Exception e) {
        }

        if (mDatabase != null) {
            mDatabase.child("sensor_data").child("distance_cm").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (isSuccess)
                        return;

                    Object val = snapshot.getValue();
                    if (val != null) {
                        try {
                            double distance = Double.parseDouble(val.toString());
                            checkDistance(distance);
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

    private void checkDistance(double distance) {
        currentDistance = distance; // 최신 값 저장

        // 목표 범위: 0 < distance <= 10.0
        if (distance > 0 && distance <= 10.0) {
            if (!isInRange) {
                // 막 진입함
                isInRange = true;
                successStartTime = System.currentTimeMillis();
                timerHandler.post(timerRunnable);
            }
            // (이미 루프 돌고 있음)
        } else {
            // 범위 벗어남
            isInRange = false;
            timerHandler.removeCallbacks(timerRunnable);

            tvIng.setText(String.format("현재 거리: %.1f cm\n(더 가까이 붙으세요! 10cm 이내)", distance));
            tvIng.setTextColor(Color.BLACK);
        }
    }

    private void handleSuccess() {
        isSuccess = true;
        timerHandler.removeCallbacks(timerRunnable);

        tvIng.setText("성공! 3초 유지 완료! 💑");
        tvIng.setTextColor(Color.parseColor("#4CAF50")); // 초록색
        btnEndGame.setEnabled(false);

        new Handler().postDelayed(() -> goToResult(true), 1500);
    }

    private void goToResult(boolean isSuccessResult) {
        timerHandler.removeCallbacks(timerRunnable);

        Intent goResult = new Intent(PockyGameActivity.this, ResultActivity.class);
        goResult.putExtra("position", position);
        goResult.putExtra("skipTurn", skipTurn);
        goResult.putExtra("autoResult", isSuccessResult);
        startActivity(goResult);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timerHandler.removeCallbacks(timerRunnable);
    }
}
