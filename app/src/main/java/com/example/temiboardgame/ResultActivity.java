package com.example.temiboardgame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class ResultActivity extends AppCompatActivity {

    private Button btnSuccess;
    private Button btnFail;

    private int position;
    private int lapCount;
    private boolean skipTurn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        btnSuccess = findViewById(R.id.btnSuccess);
        btnFail = findViewById(R.id.btnFail);

        Intent receivedIntent = getIntent();
        position = receivedIntent.getIntExtra("position", 1);
        lapCount = receivedIntent.getIntExtra("lapCount", 0);
        skipTurn = receivedIntent.getBooleanExtra("skipTurn", false);

        // ✅ 성공 → 다음 주사위 굴리기 화면(MainActivity)
        btnSuccess.setOnClickListener(v -> {
            Intent goDice = new Intent(ResultActivity.this, MainActivity.class);
            goDice.putExtra("position", position);
            goDice.putExtra("lapCount", lapCount);
            goDice.putExtra("skipTurn", skipTurn);
            startActivity(goDice);
            finish();
        });

        // ✅ 실패 → 같은 칸 다시(TileActivity)
        btnFail.setOnClickListener(v -> {
            Intent goTile = new Intent(ResultActivity.this, TileActivity.class);
            goTile.putExtra("position", position);
            goTile.putExtra("lapCount", lapCount);
            goTile.putExtra("skipTurn", skipTurn);
            startActivity(goTile);
            finish();
        });
    }
}
