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
        tvIng.setText("화면이 빨갛게 변하면\n버튼을 누르세요!");
        btnEndGame.setText("준비...");
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

                    // "0"이나 0.0 등 모든 형태의 0을 체크
                    if (valStr.equals("0") || valStr.equals("0.0")) {
                        handleButtonPress();
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

        if (isLightOn) {
            // 성공 (반응 속도 측정)
            isGameEnded = true;
            long reactionTime = System.currentTimeMillis() - startTime;
            tvIng.setText("반응 속도: " + reactionTime + "ms");
            btnEndGame.setText("성공! 🎉");
            btnEndGame.setBackgroundColor(Color.BLUE);
            btnEndGame.setEnabled(false);

            // 아두이노 상태 복구 (다음 게임을 위해 1로 원복)
            mDatabase.child("gameState").setValue(1);

            handler.postDelayed(this::goToResult, 1500);
        } else {
            // 실패 (너무 빨리 누름)
            isGameEnded = true;
            handler.removeCallbacksAndMessages(null); // 불 켜지는 타이머 취소
            tvIng.setText("너무 빨랐어요! 땡! ❌");
            btnEndGame.setText("실패...");
            btnEndGame.setEnabled(false);

            // 실패 시에도 상태 복구
            mDatabase.child("gameState").setValue(1);

            handler.postDelayed(this::goToResult, 1500);
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

        // 확실하게 0이 아니도록 설정 (버튼 누름 대기)
        mDatabase.child("gameState").setValue(1);
    }

    private void goToResult() {
        Intent goResult = new Intent(LightReactionGameActivity.this, ResultActivity.class);
        goResult.putExtra("position", position);
        goResult.putExtra("skipTurn", skipTurn);
        startActivity(goResult);
        finish();
    }
}
