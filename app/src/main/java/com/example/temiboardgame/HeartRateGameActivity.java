package com.example.temiboardgame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class HeartRateGameActivity extends AppCompatActivity {

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

        tvGameTitle.setText("심박수 올리기");
        tvIng.setText("심박수를 측정하고 있습니다... ❤️\n(심박 센서 값 수신 중)");

        btnEndGame.setOnClickListener(v -> {
            goToResult();
        });
    }

    private void goToResult() {
        Intent goResult = new Intent(HeartRateGameActivity.this, ResultActivity.class);
        goResult.putExtra("position", position);
        goResult.putExtra("skipTurn", skipTurn);
        startActivity(goResult);
        finish();
    }
}
