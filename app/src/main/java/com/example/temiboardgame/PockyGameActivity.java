package com.example.temiboardgame;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
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
        tvIng.setText("서로 가까이 다가가세요...\n현재 거리: -- cm");

        // Firebase 초기화 (명시적 URL)
        DatabaseReference mDatabase = FirebaseDatabase
                .getInstance("https://temiboardgame-60750-default-rtdb.firebaseio.com").getReference();

        // 거리 센서 값 수신 (sensor_data/distance_cm)
        mDatabase.child("sensor_data").child("distance_cm").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Object val = snapshot.getValue();
                if (val != null) {
                    String valStr = val.toString();
                    tvIng.setText("현재 거리: " + valStr + " cm\n(더 가까이!)");

                    // 거리 값에 따른 반응 (예: 5cm 이하 성공)
                    try {
                        double distance = Double.parseDouble(valStr);
                        if (distance > 0 && distance <= 5.0) {
                            tvIng.append("\n성공! 아주 가까워요! 💕");
                        }
                    } catch (NumberFormatException e) {
                        // 숫자가 아닌 값이 들어올 경우 무시
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // 에러 무시
            }
        });

        btnEndGame.setOnClickListener(v -> {
            goToResult();
        });
    }

    private void goToResult() {
        Intent goResult = new Intent(PockyGameActivity.this, ResultActivity.class);
        goResult.putExtra("position", position);
        goResult.putExtra("skipTurn", skipTurn);
        startActivity(goResult);
        finish();
    }
}
