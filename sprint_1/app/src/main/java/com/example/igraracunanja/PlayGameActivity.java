package com.example.igraracunanja;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class PlayGameActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer; // MediaPlayer za muziku tokom igre
    private int currentLevel = 1; // Počinje sa nivoom 1
    private int score = 0;
    private int questionCount = 0;
    private final int maxQuestions = 10;
    private TextView questionText, scoreText, timerText, levelText, timeSpentText;
    private Button btnOption1, btnOption2, btnOption3, btnOption4;
    private int correctAnswer;
    private CountDownTimer timer;
    private double totalTimeSpent = 0.0; // Ukupno vreme kroz sve nivoe
    private long questionStartTime; // Vreme kada je pitanje počelo
    private int operatorIndex = 0; // Indeks operatora za naizmenično biranje u Level 2

    private final char[] operators = {'+', '-', '*', '/'}; // Operatori za Level 2

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_game);

        // Prekidanje globalne muzike
        Intent stopMusic = new Intent(this, MusicService.class);
        stopService(stopMusic);

        // Pokretanje lokalne muzike
        mediaPlayer = MediaPlayer.create(this, R.raw.sound); // Koristi audio fajl iz raw foldera
        mediaPlayer.setLooping(true); // Postavi da se muzika ponavlja
        mediaPlayer.start(); // Pokreni muziku

        questionText = findViewById(R.id.questionText);
        scoreText = findViewById(R.id.scoreText);
        timerText = findViewById(R.id.timerText);
        levelText = findViewById(R.id.levelText);
        timeSpentText = findViewById(R.id.timeSpentText);
        btnOption1 = findViewById(R.id.btnOption1);
        btnOption2 = findViewById(R.id.btnOption2);
        btnOption3 = findViewById(R.id.btnOption3);
        btnOption4 = findViewById(R.id.btnOption4);

        updateLevelText();
        loadNextQuestion();
    }

    private void updateLevelText() {
        levelText.setText("Level " + currentLevel);
    }

    private void loadNextQuestion() {
        if (questionCount < maxQuestions) {
            generateQuestion();
            startTimer();
        } else {
            if (currentLevel == 1) {
                currentLevel = 2;
                questionCount = 0;
                Toast.makeText(this, "Level 2: Now with multiplication and division!", Toast.LENGTH_SHORT).show();
                updateLevelText();
                loadNextQuestion();
            } else {
                endGame();
            }
        }
    }

    private void generateQuestion() {
        Random random = new Random();
        int num1, num2;
        char operator;

        if (currentLevel == 1) {
            operator = random.nextBoolean() ? '+' : '-'; // Level 1: samo + i -
        } else {
            operator = operators[operatorIndex]; // Naizmenično biramo operatore u Level 2
            operatorIndex = (operatorIndex + 1) % operators.length; // Pomeri indeks za sledeći operator
        }

        do {
            num1 = random.nextInt(10); // Jednocifren broj (0-9)
            num2 = random.nextInt(9) + 1; // Jednocifren broj (1-9), izbegavamo 0 za deljenje

            if (operator == '-') {
                if (num1 < num2) {
                    int temp = num1;
                    num1 = num2;
                    num2 = temp;
                }
            } else if (operator == '/') {
                // Osiguravamo da num1 bude deljiv sa num2 i da rezultat bude jednocifren
                num1 = random.nextInt(9) + 1; // Jednocifren broj (1-9)
                num2 = random.nextInt(9) + 1; // Jednocifren broj (1-9)
                while (num1 % num2 != 0 || num1 / num2 > 9) {
                    num1 = random.nextInt(9) + 1;
                    num2 = random.nextInt(9) + 1;
                }
            }

            switch (operator) {
                case '+':
                    correctAnswer = num1 + num2;
                    break;
                case '-':
                    correctAnswer = num1 - num2;
                    break;
                case '*':
                    correctAnswer = num1 * num2;
                    break;
                case '/':
                    correctAnswer = num1 / num2;
                    break;
                default:
                    correctAnswer = 0;
            }
        } while (correctAnswer < 0 || correctAnswer > 9); // Rezultat mora biti jednocifren

        questionText.setText(num1 + " " + operator + " " + num2);

        ArrayList<Integer> options = new ArrayList<>();
        options.add(correctAnswer);
        while (options.size() < 4) {
            int wrongAnswer = random.nextInt(10);
            if (!options.contains(wrongAnswer)) {
                options.add(wrongAnswer);
            }
        }

        Collections.shuffle(options);

        btnOption1.setText(String.valueOf(options.get(0)));
        btnOption2.setText(String.valueOf(options.get(1)));
        btnOption3.setText(String.valueOf(options.get(2)));
        btnOption4.setText(String.valueOf(options.get(3)));

        btnOption1.setOnClickListener(v -> checkAnswer(Integer.parseInt(btnOption1.getText().toString())));
        btnOption2.setOnClickListener(v -> checkAnswer(Integer.parseInt(btnOption2.getText().toString())));
        btnOption3.setOnClickListener(v -> checkAnswer(Integer.parseInt(btnOption3.getText().toString())));
        btnOption4.setOnClickListener(v -> checkAnswer(Integer.parseInt(btnOption4.getText().toString())));

        questionStartTime = System.currentTimeMillis();
    }

    private void startTimer() {
        timer = new CountDownTimer(4000, 100) { // 4 sekunde, ažuriranje svakih 100 ms
            @Override
            public void onTick(long millisUntilFinished) {
                timerText.setText(String.format("Time left: %.1f", millisUntilFinished / 1000.0) + "s");
            }

            @Override
            public void onFinish() {
                questionCount++;
                calculateTimeSpent();
                loadNextQuestion();
            }
        }.start();
    }

    private void checkAnswer(int selectedAnswer) {
        if (selectedAnswer == correctAnswer) {
            score += currentLevel == 1 ? 1 : 2;
        }
        questionCount++;
        scoreText.setText("Score: " + score);

        if (timer != null) {
            timer.cancel();
        }

        calculateTimeSpent();
        loadNextQuestion();
    }

    private void calculateTimeSpent() {
        double timeSpentOnQuestion = (System.currentTimeMillis() - questionStartTime) / 1000.0;
        totalTimeSpent += timeSpentOnQuestion;
        timeSpentText.setText(String.format("Time spent: %.1f s", totalTimeSpent));
    }

    private void endGame() {
        if (timer != null) {
            timer.cancel();
        }
        Toast.makeText(this, "Game Over! Final score: " + score, Toast.LENGTH_LONG).show();

        // Vrati korisnika u MainActivity
        String username = getIntent().getStringExtra("USERNAME");
        Intent intent = new Intent(PlayGameActivity.this, MainActivity.class);
        intent.putExtra("USERNAME", username);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
