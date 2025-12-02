package com.example.temiboardgame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class CongratsActivity extends AppCompatActivity {

    private int position;
    private int lapCount;
    private boolean skipTurn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_congrats);

        TextView tvMessage = findViewById(R.id.tvCongratsMessage);
        Button btnGoTile = findViewById(R.id.btnGoTile);

        Intent receivedIntent = getIntent();
        position = receivedIntent.getIntExtra("position", 12);
        lapCount = receivedIntent.getIntExtra("lapCount", 0);
        skipTurn = receivedIntent.getBooleanExtra("skipTurn", false);

        tvMessage.setText("🎉 12번 칸 도착! 축하합니다! 🎉");

        btnGoTile.setOnClickListener(v -> {
            Intent goTile = new Intent(CongratsActivity.this, TileActivity.class);
            goTile.putExtra("position", position);
            goTile.putExtra("lapCount", lapCount);
            goTile.putExtra("skipTurn", skipTurn);
            startActivity(goTile);
            finish();
        });
    }
}
