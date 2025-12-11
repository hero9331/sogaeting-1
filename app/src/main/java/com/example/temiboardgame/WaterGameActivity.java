package com.example.temiboardgame;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class WaterGameActivity extends AppCompatActivity {

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

        tvGameTitle.setText("물 양 맞추기 💧");
        tvIng.setText("물을 조심스럽게 따라주세요...\n현재 수위: 0%");

        // TODO: 아두이노나 외부 센서에서 수위 값을 받아오는 통신 코드를 여기에 작성하세요.

        btnEndGame.setOnClickListener(v -> {
            goToResult();
        });
    }

    // [센서 연동 포인트] 외부에서 수위값(%)이 들어오면 이 함수를 호출해주세요.
    public void onWaterLevelReceived(int percentage) {
        tvIng.setText("현재 수위: " + percentage + "%");

        if (percentage >= 100) {
            tvIng.setText("넘쳤습니다! 실패! 🌊");
        } else if (percentage > 90) {
            tvIng.setText("아주 좋아요! 90% 달성! 👍");
        }
    }

    private void goToResult() {
        Intent goResult = new Intent(WaterGameActivity.this, ResultActivity.class);
        goResult.putExtra("position", position);
        goResult.putExtra("skipTurn", skipTurn);
        startActivity(goResult);
        finish();
    }
}
