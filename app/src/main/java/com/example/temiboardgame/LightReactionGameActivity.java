package com.example.temiboardgame;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LightReactionGameActivity extends AppCompatActivity {

    private TextView tvGameTitle;
    private TextView tvIng;
    private Button btnEndGame;
    private View layoutScreen;

    private int position;
    private boolean skipTurn;

    private long startTime;
    private boolean isLightOn = false;
    private Handler handler = new Handler();

    private DatabaseReference mDatabase;
    private boolean isGameEnded = false; // 중복 실행 방지
    private String lastSwitchValue = null; // 마지막 스위치 값 저장 (변화 감지용)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_play);

        tvGameTitle = findViewById(R.id.tvGameTitle);
        tvIng = findViewById(R.id.tvIng);
        btnEndGame = findViewById(R.id.btnEndGame);
        layoutScreen = findViewById(R.id.progressBar).getRootView();

        Intent receivedIntent = getIntent();
        position = receivedIntent.getIntExtra("position", 0);
        skipTurn = receivedIntent.getBooleanExtra("skipTurn", false);

        // Firebase 초기화 (명시적 URL 지정 - 연결 확실하게!)
        mDatabase = FirebaseDatabase.getInstance("https://temiboardgame-60750-default-rtdb.firebaseio.com")
                .getReference();

        tvGameTitle.setText("불빛 반응 속도 ⚡");

        // 초기화 버튼 연결
        Button btnReset = findViewById(R.id.btnResetGame);
        if (btnReset != null) {
            btnReset.setOnClickListener(v -> {
                Intent intent = new Intent(LightReactionGameActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("RESET_GAME", true);
                startActivity(intent);
                finish();
            });
        }
        tvIng.setText("버튼이 빨갛게 변하면\n버튼을 누르세요!");
        btnEndGame.setText("준비하세요...");
        btnEndGame.setEnabled(false);

        // 게임 시작 전 상태 초기화 (대기 상태 = 1)
        mDatabase.child("gameState").setValue(1);

        // 2~5초 랜덤 딜레이 후 불빛 켜기
        int randomDelay = (int) (Math.random() * 3000) + 2000;
        handler.postDelayed(this::turnOnLight, randomDelay);

        // 1. 화면 버튼 클릭 리스너
        btnEndGame.setOnClickListener(v -> handleButtonPress());

        // 아두이노 버튼(Firebase) 리스너 (경로: sensor_data/switch_state)
        mDatabase.child("sensor_data").child("switch_state").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Object myText = snapshot.getValue(); // Object로 모든 타입 수용
                if (myText != null) {
                    String valStr = myText.toString();

                    // 최초 실행 시 값 저장만 하고 무시
                    if (lastSwitchValue == null) {
                        lastSwitchValue = valStr;
                        return;
                    }

                    // 이전 값과 다르면(변화 발생 시) 버튼 눌림으로 처리
                    if (!valStr.equals(lastSwitchValue)) {
                        lastSwitchValue = valStr;
                        // 0 또는 1로 변했을 때 모두 처리
                        if (valStr.equals("0") || valStr.equals("0.0") || valStr.equals("1") || valStr.equals("1.0")) {
                            handleButtonPress();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w("LightReaction", "Failed to read value.", error.toException());
            }
        });
    }

    private void handleButtonPress() {
        if (isGameEnded)
            return; // 이미 끝났으면 무시

        boolean isSuccess = false;

        if (isLightOn) {
            // 불 켜진 후 누름 -> 속도 측정
            isGameEnded = true;
            long reactionTime = System.currentTimeMillis() - startTime;

            if (reactionTime <= 1000) {
                // 성공 (1000ms 이하)
                isSuccess = true;
                tvIng.setText("반응 속도: " + reactionTime + "ms\n(1000ms 이내 성공!)");
                btnEndGame.setText("성공! 🎉");
                btnEndGame.setBackgroundColor(Color.BLUE);
            } else {
                // 실패 (너무 느림)
                isSuccess = false;
                tvIng.setText("속도: " + reactionTime + "ms... 너무 느려요! 🐢\n(1000ms 안에 눌러야 해요)");
                btnEndGame.setText("실패... 🐢");
                btnEndGame.setBackgroundColor(Color.GRAY);
            }

            btnEndGame.setEnabled(false);

            // 상태 복구
            mDatabase.child("gameState").setValue(1);

            // 결과 화면 이동 (성공 여부 전달)
            final boolean finalResult = isSuccess;
            handler.postDelayed(() -> goToResult(finalResult), 1500);

        } else {
            // 실패 (너무 빨리 누름)
            isGameEnded = true;
            handler.removeCallbacksAndMessages(null); // 타이머 취소

            tvIng.setText("너무 빨랐어요! 땡! ❌\n(불이 켜지면 누르세요)");
            btnEndGame.setText("실패... ⚡");
            btnEndGame.setBackgroundColor(Color.GRAY);
            btnEndGame.setEnabled(false);

            mDatabase.child("gameState").setValue(1);

            // 실패 전달
            handler.postDelayed(() -> goToResult(false), 1500);
        }
    }

    private void turnOnLight() {
        if (isFinishing() || isGameEnded)
            return;

        isLightOn = true;
        startTime = System.currentTimeMillis();

        // 배경이나 버튼 색상을 붉게 변경하여 신호 줌
        btnEndGame.setBackgroundColor(Color.RED);
        btnEndGame.setText("지금 눌러!! 🚨");
        btnEndGame.setEnabled(true);

        mDatabase.child("gameState").setValue(1);
    }

    private void goToResult(boolean isSuccess) {
        Intent goResult = new Intent(LightReactionGameActivity.this, ResultActivity.class);
        goResult.putExtra("position", position);
        goResult.putExtra("skipTurn", skipTurn);

        // 자동 결과 판정 전달
        goResult.putExtra("autoResult", isSuccess);

        startActivity(goResult);
        finish();
    }
}
