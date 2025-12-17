package com.example.temiboardgame;

import androidx.appcompat.app.AppCompatActivity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TimeGameActivity extends AppCompatActivity {

    private TextView tvGameTitle;
    private TextView tvIng;
    private Button btnEndGame;

    private int position;
    private boolean skipTurn;

    // 게임 로직 변수
    private long startTime;
    private boolean isRunning = false;
    private android.os.Handler handler = new android.os.Handler();

    // Firebase
    private DatabaseReference mDatabase;

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

        // Firebase 초기화
        try {
            mDatabase = FirebaseDatabase.getInstance("https://temiboardgame-60750-default-rtdb.firebaseio.com")
                    .getReference();
        } catch (Exception e) {
            Toast.makeText(this, "Firebase Init Error", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 초기화 버튼 연결
        Button btnReset = findViewById(R.id.btnResetGame);
        if (btnReset != null) {
            btnReset.setOnClickListener(v -> {
                Intent intent = new Intent(TimeGameActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("RESET_GAME", true);
                startActivity(intent);
                finish();
            });
        }

        tvGameTitle.setText("시간 맞추기 ⏱️");
        tvIng.setText("버튼을 눌러 3.00초에 맞춰보세요!\n(정확히 3초에 가까울수록 승리!)");
        btnEndGame.setText("시작하기");

        btnEndGame.setOnClickListener(v -> {
            if (!isRunning) {
                // 게임 시작
                startTimer();
            } else {
                // 게임 정지 (멈춰!)
                stopTimerAndFinish();
            }
        });

        // Firebase 리스너 등록 (경로: sensor_data/switch_state)
        mDatabase.child("sensor_data").child("switch_state").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Object myText = snapshot.getValue();
                if (myText != null) {
                    String valStr = myText.toString();
                    // "0"이나 "0.0", "1", "1.0" 등 값이 바뀌면 정지 신호로 간주
                    if (isRunning && (valStr.equals("0") || valStr.equals("0.0") || valStr.equals("1")
                            || valStr.equals("1.0"))) {
                        stopTimerAndFinish();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w("TimeGame", "Failed to read value.", error.toException());
            }
        });
    }

    private void startTimer() {
        isRunning = true;
        startTime = System.currentTimeMillis();

        btnEndGame.setText("멈춰! 🛑");
        tvIng.setText("시간이 흐르고 있습니다...\n??? 초");
    }

    private void stopTimerAndFinish() {
        if (!isRunning)
            return;

        isRunning = false;
        long endTime = System.currentTimeMillis();

        // 1. 네트워크 지연 보정: 0.5초(500ms) 차감
        long durationRaw = endTime - startTime;
        long durationCompensated = durationRaw - 500;

        if (durationCompensated < 0)
            durationCompensated = 0;

        double elapsedSeconds = durationCompensated / 1000.0;

        // 2. 성공 여부 판정 (오차범위 +- 1초 -> 2.0초 ~ 4.0초 사이)
        boolean isSuccess = (elapsedSeconds >= 2.0 && elapsedSeconds <= 4.0);

        tvIng.setText(String.format("측정 종료!\n기록: %.2f초\n(보정 적용됨)", elapsedSeconds));
        btnEndGame.setEnabled(false); // 중복 클릭 방지

        // 3. 결과 다이얼로그 띄우기
        showResultDialog(isSuccess, elapsedSeconds);
    }

    private void showResultDialog(boolean isSuccess, double time) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_time_game_result);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialog.setCancelable(false);

        TextView tvIcon = dialog.findViewById(R.id.tvResultIcon);
        TextView tvTitle = dialog.findViewById(R.id.tvResultTitle);
        TextView tvTime = dialog.findViewById(R.id.tvResultTime);
        Button btnConfirm = dialog.findViewById(R.id.btnConfirm);

        if (isSuccess) {
            tvIcon.setText("🎉");
            tvTitle.setText("성공!");
            tvTitle.setTextColor(Color.parseColor("#4CAF50")); // 초록색
            tvTime.setText(String.format("완벽해요! %.2f초", time));
        } else {
            tvIcon.setText("😢");
            tvTitle.setText("실패...");
            tvTitle.setTextColor(Color.parseColor("#F44336")); // 빨간색
            tvTime.setText(String.format("아쉬워요.. %.2f초\n(목표: 2.0 ~ 4.0초)", time));
        }

        btnConfirm.setOnClickListener(v -> {
            dialog.dismiss();
            // 결과 화면으로 성공 여부 전달
            goToResult(isSuccess);
        });

        handler.postDelayed(() -> {
            if (!isFinishing()) {
                dialog.show();
            }
        }, 500);
    }

    private void goToResult(boolean isSuccess) {
        Intent goResult = new Intent(TimeGameActivity.this, ResultActivity.class);
        goResult.putExtra("position", position);
        goResult.putExtra("skipTurn", skipTurn);

        // 자동 결과 판정 (true: 성공, false: 실패)
        goResult.putExtra("autoResult", isSuccess);

        startActivity(goResult);
        finish();
    }
}
