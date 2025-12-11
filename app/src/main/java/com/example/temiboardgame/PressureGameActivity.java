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

public class PressureGameActivity extends AppCompatActivity {

    private TextView tvGameTitle;
    private TextView tvIng;
    private Button btnEndGame;

    private int position;
    private boolean skipTurn;

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

        tvGameTitle.setText("압력 맞추기 👇");
        tvIng.setText("아두이노 센서를 꾹 눌러보세요!\n(연결 대기 중...)");

        // Firebase 초기화 (명시적 URL)
        mDatabase = FirebaseDatabase.getInstance("https://temiboardgame-60750-default-rtdb.firebaseio.com")
                .getReference();

        // 아두이노 압력 센서값 수신 (pressure_sensor/adc_raw)
        mDatabase.child("pressure_sensor").child("adc_raw").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Object val = snapshot.getValue();
                if (val != null) {
                    String valStr = val.toString();

                    // 화면에 실시간 값 표시
                    tvIng.setText("현재 압력(ADC): " + valStr + "\n꾹 눌러서 목표에 도달하세요!");

                    // (옵션) 나중에 여기에 목표 도달 로직 추가 가능
                    // int adcValue = Integer.parseInt(valStr);
                    // if (adcValue > 800) { ... }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // 에러 무시 (로그는 찍지 않음)
            }
        });

        btnEndGame.setOnClickListener(v -> {
            goToResult();
        });
    }

    private void goToResult() {
        Intent goResult = new Intent(PressureGameActivity.this, ResultActivity.class);
        goResult.putExtra("position", position);
        goResult.putExtra("skipTurn", skipTurn);
        startActivity(goResult);
        finish();
    }
}
