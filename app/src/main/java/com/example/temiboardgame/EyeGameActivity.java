package com.example.temiboardgame;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EyeGameActivity extends AppCompatActivity {

    // 1. 변수 선언
    private DatabaseReference databaseReference;

    // Player 1
    private TextView tvP1Real, tvP1Saved;
    private Button btnP1Save; // 자동화로 인해 숨김

    // Player 2
    private TextView tvP2Real, tvP2Saved;
    private Button btnP2Save; // 자동화로 인해 숨김

    // 게임 이동용
    private Button btnEndGame;
    private int position;
    private boolean skipTurn;

    // 성공 상태 관리
    private boolean isP1Success = false;
    private boolean isP2Success = false;
    private boolean isGameEnded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 레이아웃은 기존 심박수 게임의 레이아웃을 그대로 사용
        setContentView(R.layout.activity_heart_rate_game);

        Intent receivedIntent = getIntent();
        position = receivedIntent.getIntExtra("position", 0);
        skipTurn = receivedIntent.getBooleanExtra("skipTurn", false);

        // 초기화 버튼 연결
        Button btnReset = findViewById(R.id.btnResetGame);
        if (btnReset != null) {
            btnReset.setOnClickListener(v -> {
                Intent intent = new Intent(EyeGameActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("RESET_GAME", true);
                startActivity(intent);
                finish();
            });
        }

        // 2. 파이어베이스 초기화
        try {
            databaseReference = FirebaseDatabase.getInstance("https://temiboardgame-60750-default-rtdb.firebaseio.com")
                    .getReference();
        } catch (Exception e) {
            Toast.makeText(this, "Firebase Init Error", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 3. XML 연결
        tvP1Real = findViewById(R.id.tv_p1_bpm);
        tvP1Saved = findViewById(R.id.tv_p1_record);
        btnP1Save = findViewById(R.id.btn_p1_save);

        tvP2Real = findViewById(R.id.tv_p2_bpm);
        tvP2Saved = findViewById(R.id.tv_p2_record);
        btnP2Save = findViewById(R.id.btn_p2_save);

        btnEndGame = findViewById(R.id.btnEndGame);

        // 버튼 텍스트 변경 & 수동 저장 버튼 숨김
        btnEndGame.setText("포기하기 (실패)");
        btnEndGame.setBackgroundColor(Color.GRAY);

        btnP1Save.setVisibility(View.GONE);
        btnP2Save.setVisibility(View.GONE);

        tvP1Saved.setText("목표: 심박수 100 이상! 🔥");
        tvP2Saved.setText("목표: 심박수 100 이상! 🔥");

        // 4. 기능 구현 (파이어베이스 데이터 수신)

        // [Player 1] 실시간 BPM 읽기 (Game/Player1/bpm)
        databaseReference.child("Game").child("Player1").child("bpm").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (isGameEnded)
                    return;

                if (snapshot.exists() && snapshot.getValue() != null) {
                    String value = String.valueOf(snapshot.getValue());
                    tvP1Real.setText(value);

                    try {
                        int bpm = Integer.parseInt(value);
                        if (bpm > 100) {
                            handlePlayerSuccess(1, bpm);
                        }
                    } catch (NumberFormatException e) {
                    }
                } else {
                    tvP1Real.setText("0");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("Firebase", "P1 Error: " + error.getMessage());
            }
        });

        // [Player 2] 실시간 BPM 읽기 (Game/Player2/bpm)
        databaseReference.child("Game").child("Player2").child("bpm").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (isGameEnded)
                    return;

                if (snapshot.exists() && snapshot.getValue() != null) {
                    String value = String.valueOf(snapshot.getValue());
                    tvP2Real.setText(value);

                    try {
                        int bpm = Integer.parseInt(value);
                        if (bpm > 100) {
                            handlePlayerSuccess(2, bpm);
                        }
                    } catch (NumberFormatException e) {
                    }
                } else {
                    tvP2Real.setText("0");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("Firebase", "P2 Error: " + error.getMessage());
            }
        });

        // 게임 포기(실패) 버튼
        btnEndGame.setOnClickListener(v -> {
            goToResult(false);
        });
    }

    private synchronized void handlePlayerSuccess(int playerNum, int bpm) {
        if (playerNum == 1 && !isP1Success) {
            isP1Success = true;
            tvP1Saved.setText("성공! (" + bpm + " BPM) 🎉");
            tvP1Saved.setTextColor(Color.parseColor("#4CAF50")); // 초록색
        } else if (playerNum == 2 && !isP2Success) {
            isP2Success = true;
            tvP2Saved.setText("성공! (" + bpm + " BPM) 🎉");
            tvP2Saved.setTextColor(Color.parseColor("#4CAF50")); // 초록색
        }

        // 두 명 다 성공했는지 체크
        if (isP1Success && isP2Success && !isGameEnded) {
            isGameEnded = true;
            btnEndGame.setText("모두 성공! 축하합니다! 🎉");
            btnEndGame.setBackgroundColor(Color.BLUE);
            btnEndGame.setEnabled(false);

            // 1.5초 후 결과 화면(성공)으로 이동
            new Handler().postDelayed(() -> goToResult(true), 1500);
        }
    }

    private void goToResult(boolean isSuccess) {
        Intent goResult = new Intent(EyeGameActivity.this, ResultActivity.class);
        goResult.putExtra("position", position);
        goResult.putExtra("skipTurn", skipTurn);

        // 자동 결과 전달
        goResult.putExtra("autoResult", isSuccess);

        startActivity(goResult);
        finish();
    }
}
