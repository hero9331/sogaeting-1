package com.example.temiboardgame;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HeartRateGameActivity extends AppCompatActivity {

    // 1. 변수 선언
    private DatabaseReference databaseReference;

    // Player 1
    private TextView tvP1Real, tvP1Saved;
    private Button btnP1Save;

    // Player 2
    private TextView tvP2Real, tvP2Saved;
    private Button btnP2Save;

    // 게임 이동용
    private Button btnEndGame;
    private int position;
    private boolean skipTurn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart_rate_game); // 새로 만든 XML 연결

        Intent receivedIntent = getIntent();
        position = receivedIntent.getIntExtra("position", 0);
        skipTurn = receivedIntent.getBooleanExtra("skipTurn", false);

        // 2. 파이어베이스 초기화 (명시적 URL로 확실하게!)
        // 기존 코드: databaseReference = FirebaseDatabase.getInstance().getReference();
        // 수정 코드:
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

        // 4. 기능 구현 (파이어베이스 데이터 수신)

        // [Player 1] 실시간 BPM 읽기 (Game/Player1/bpm)
        databaseReference.child("Game").child("Player1").child("bpm").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getValue() != null) {
                    String value = String.valueOf(snapshot.getValue());
                    tvP1Real.setText(value);
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
                if (snapshot.exists() && snapshot.getValue() != null) {
                    String value = String.valueOf(snapshot.getValue());
                    tvP2Real.setText(value);
                } else {
                    tvP2Real.setText("0");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("Firebase", "P2 Error: " + error.getMessage());
            }
        });

        // 5. 버튼 기능 (기록 저장)
        btnP1Save.setOnClickListener(v -> {
            String currentBpm = tvP1Real.getText().toString();
            tvP1Saved.setText("기록: " + currentBpm + " BPM");
            Toast.makeText(HeartRateGameActivity.this, "Player 1 저장 완료!", Toast.LENGTH_SHORT).show();
        });

        btnP2Save.setOnClickListener(v -> {
            String currentBpm = tvP2Real.getText().toString();
            tvP2Saved.setText("기록: " + currentBpm + " BPM");
            Toast.makeText(HeartRateGameActivity.this, "Player 2 저장 완료!", Toast.LENGTH_SHORT).show();
        });

        // 게임 종료 및 다음 화면 이동
        btnEndGame.setOnClickListener(v -> {
            Intent goResult = new Intent(HeartRateGameActivity.this, ResultActivity.class);
            goResult.putExtra("position", position);
            goResult.putExtra("skipTurn", skipTurn);
            startActivity(goResult);
            finish();
        });
    }
}
