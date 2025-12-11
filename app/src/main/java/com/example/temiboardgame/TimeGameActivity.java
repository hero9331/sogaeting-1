package com.example.temiboardgame;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

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

        // Firebase 초기화 (명시적 URL 지정 - 연결 확실하게!)
        mDatabase = FirebaseDatabase.getInstance("https://temiboardgame-60750-default-rtdb.firebaseio.com")
                .getReference();
        tvGameTitle.setText("시간 맞추기 ⏱️");
        tvIng.setText("버튼을 눌러 3.00초에 맞춰보세요!\n(시작하려면 버튼 클릭)");
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
                Object myText = snapshot.getValue(); // Object로 받아서 유연하게 처리

                if (myText != null) {
                    String valStr = myText.toString();

                    // "0"이나 "0.0"이면 정지 신호
                    if (isRunning && (valStr.equals("0") || valStr.equals("0.0"))) {
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

        // [테스트를 위해 주석 처리] 시작할 때 강제로 1로 바꾸지 않음 (실시간 변경 테스트용)
        // mDatabase.child("sensor_data").child("switch_state").setValue(1);

        btnEndGame.setText("멈춰! 🛑");
        tvIng.setText("시간이 흐르고 있습니다...\n??? 초");
    }

    private void stopTimerAndFinish() {
        if (!isRunning)
            return; // 이미 멈췄으면 패스

        isRunning = false;
        long endTime = System.currentTimeMillis();

        // 네트워크 지연 보정: 약 400ms 차감
        long duration = (endTime - startTime) - 400;
        if (duration < 0)
            duration = 0;

        double elapsedSeconds = duration / 1000.0;

        tvIng.setText(String.format("기록: %.2f초\n(통신 지연 -0.4초 보정)", elapsedSeconds));
        btnEndGame.setEnabled(false); // 중복 클릭 방지

        // 잠시 후 결과 화면으로 이동
        handler.postDelayed(() -> {
            goToResult();
        }, 1500);
    }

    private void goToResult() {
        Intent goResult = new Intent(TimeGameActivity.this, ResultActivity.class);
        goResult.putExtra("position", position);
        goResult.putExtra("skipTurn", skipTurn);
        startActivity(goResult);
        finish();
    }
}
