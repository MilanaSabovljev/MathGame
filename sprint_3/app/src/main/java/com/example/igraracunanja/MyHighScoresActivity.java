package com.example.igraracunanja;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class MyHighScoresActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_high_scores);

        databaseHelper = new DatabaseHelper();
        loadGlobalHighScores();
    }

    private void loadGlobalHighScores() {
        databaseHelper.getBestResultsByUser(sortedResults -> {
            LinearLayout container = findViewById(R.id.highScoresContainer);
            container.removeAllViews();

            if (sortedResults.isEmpty()) {
                TextView noScores = new TextView(this);
                noScores.setText("No global high scores available.");
                noScores.setTextColor(Color.WHITE);
                noScores.setTextSize(18);
                noScores.setGravity(android.view.Gravity.CENTER);
                container.addView(noScores);
            } else {
                int rank = 1;
                for (String result : sortedResults) {
                    // Dodavanje bedža iznad svakog rezultata
                    if (rank <= 5) {
                        ImageView badge = new ImageView(this);
                        badge.setImageResource(getBadgeResource(rank));
                        badge.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT));
                        badge.setAdjustViewBounds(true);
                        badge.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                        badge.setPadding(0, 16, 0, 16);
                        container.addView(badge);
                    }

                    // Dodavanje rezultata
                    TextView resultText = new TextView(this);
                    resultText.setText(rank + ". " + result);
                    resultText.setTextColor(Color.WHITE);
                    resultText.setTextSize(18);
                    resultText.setPadding(0, 8, 0, 16);
                    container.addView(resultText);

                    rank++;
                }
            }
        });
    }

    private int getBadgeResource(int rank) {
        switch (rank) {
            case 1:
                return R.drawable.diamond_1;
            case 2:
                return R.drawable.ruby_2;
            case 3:
                return R.drawable.emerald_3;
            case 4:
                return R.drawable.sapphire_4;
            case 5:
                return R.drawable.amethyst_5;
            default:
                return 0; // Nema bedža za ostale
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent musicIntent = new Intent(this, MusicService.class);
        startService(musicIntent); // Pokretanje muzike
    }


}
