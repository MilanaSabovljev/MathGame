package com.example.igraracunanja;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;


import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class PlayGameActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private int currentLevel = 1; // Počinje sa nivoom 1
    private int score = 0;
    private int questionCount = 0;
    private final int maxQuestions = 10;
    private TextView questionText, scoreText, timerText, levelText, timeSpentText;
    private Button btnOption1, btnOption2, btnOption3, btnOption4;
    private int correctAnswer;
    private CountDownTimer timer;
    private double totalTimeSpent = 0.0; // Ukupno vreme kroz sve nivoe
    private long questionStartTime;
    private final char[] operators = {'+', '-', '*', '/'}; // Operatori za sve nivoe
    private int operatorIndex = 0; // Indeks za naizmenično biranje operatora u Level 2 i Level 3
    private int wrongAnswerCount = 0; // Brojač netačnih odgovora
    private final int maxWrongAnswers = 20; // Maksimalno dozvoljenih netačnih odgovora
    private TextView wrongAnswersText;
    private LocalDatabaseHelper localDatabaseHelper;
    private DatabaseHelper databaseHelper;
    private String username; // Dodato za praćenje korisničkog imena





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_game);

        // U onCreate metodi:
        wrongAnswersText = findViewById(R.id.wrongAnswersText);
        updateWrongAnswersText();

        // Inicijalizacija lokalne baze
        localDatabaseHelper = new LocalDatabaseHelper(this);

        // Inicijalizacija Firebase baze za globalne rezultate
        databaseHelper = new DatabaseHelper();

        username = getIntent().getStringExtra("USERNAME");
        if (username == null) {
            username = "Unknown"; // Fallback ako korisničko ime nije prosleđeno
        }


        // Prekidanje globalne muzike
        Intent stopMusic = new Intent(this, MusicService.class);
        stopService(stopMusic);

        // Pokretanje lokalne muzike
        mediaPlayer = MediaPlayer.create(this, R.raw.sound);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

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
            if (currentLevel < 4) {
                startPauseBetweenLevels(); // Dodajemo pauzu pre prelaska na sledeći nivo
            } else {
                endGame(); // Završava igru ako je nivo 4
            }
        }
    }

    private void startPauseBetweenLevels() {
        questionText.setText("Level " + currentLevel + " completed!");
        btnOption1.setVisibility(Button.INVISIBLE);
        btnOption2.setVisibility(Button.INVISIBLE);
        btnOption3.setVisibility(Button.INVISIBLE);
        btnOption4.setVisibility(Button.INVISIBLE);

        Toast.makeText(this, "Get ready for the next level!", Toast.LENGTH_SHORT).show();

        new CountDownTimer(10000, 1000) { // Pauza traje 10 sekundi
            @Override
            public void onTick(long millisUntilFinished) {
                timerText.setText("Next level starts in: " + millisUntilFinished / 1000 + "s");
            }

            @Override
            public void onFinish() {
                currentLevel++;
                questionCount = 0;
                updateLevelText();

                // Vraćanje dugmadi i učitavanje sledećeg pitanja
                btnOption1.setVisibility(Button.VISIBLE);
                btnOption2.setVisibility(Button.VISIBLE);
                btnOption3.setVisibility(Button.VISIBLE);
                btnOption4.setVisibility(Button.VISIBLE);

                loadNextQuestion();
            }
        }.start();
    }

    private void updateWrongAnswersText() {
        wrongAnswersText.setText("Wrong: " + wrongAnswerCount);
    }



    private void generateQuestion() {
        switch (currentLevel) {
            case 1:
                generateLevel1Question();
                break;
            case 2:
                generateLevel2Question();
                break;
            case 3:
                generateLevel3Question();
                break;
            case 4:
                generateLevel4Question(); // Dodata podrška za nivo 4
                break;
            default:
                throw new IllegalStateException("Unexpected level: " + currentLevel);
        }
    }

    // Generisanje pitanja za nivo 1
    private void generateLevel1Question() {
        Random random = new Random();
        int num1, num2;
        char operator = random.nextBoolean() ? '+' : '-';

        do {
            num1 = random.nextInt(10); // Jednocifren broj (0-9)
            num2 = random.nextInt(10); // Jednocifren broj (0-9)
            if (operator == '-' && num1 < num2) {
                int temp = num1;
                num1 = num2;
                num2 = temp;
            }
            correctAnswer = calculateResult(num1, num2, operator);
        } while (correctAnswer < 0 || correctAnswer > 9);

        displayQuestion(num1, num2, operator);
    }

    // Generisanje pitanja za nivo 2
    private void generateLevel2Question() {
        Random random = new Random();
        int num1, num2;
        char operator = operators[operatorIndex];
        operatorIndex = (operatorIndex + 1) % operators.length;

        do {
            if (operator == '/') {
                num2 = random.nextInt(4) + 1; // Delilac: 1, 2, 3, 4
                correctAnswer = random.nextInt(10); // Rezultat: 0–9
                num1 = num2 * correctAnswer; // Deljenik = delilac * rezultat
            } else {
                num1 = random.nextInt(10);
                num2 = random.nextInt(10);
                if (operator == '-' && num1 < num2) {
                    int temp = num1;
                    num1 = num2;
                    num2 = temp;
                }
                correctAnswer = calculateResult(num1, num2, operator);
            }
        } while ((operator == '/' && num1 > 9) || correctAnswer > 9);

        displayQuestion(num1, num2, operator);
    }

    // Generisanje pitanja za nivo 3
    private void generateLevel3Question() {
        Random random = new Random();
        int num1, num2;
        char operator = operators[operatorIndex];
        operatorIndex = (operatorIndex + 1) % operators.length;

        do {
            if (operator == '/') {
                num2 = random.nextInt(24) + 1; // Delilac: 1–24
                correctAnswer = random.nextInt(50); // Rezultat: 0–49
                num1 = num2 * correctAnswer; // Deljenik = delilac * rezultat
            } else {
                num1 = random.nextInt(50) + 1;
                num2 = random.nextInt(50) + 1;
                if (operator == '-' && num1 < num2) {
                    int temp = num1;
                    num1 = num2;
                    num2 = temp;
                }
                correctAnswer = calculateResult(num1, num2, operator);
            }
        } while ((operator == '/' && num1 > 49) || correctAnswer > 99);

        displayQuestion(num1, num2, operator);
    }

    // Nivo 4 (kako si ga ti napisao)
    private void generateLevel4Question() {
        Random random = new Random();
        int num1, num2, num3;
        char operator1, operator2;

        while (true) { // Ponavljamo dok ne generišemo validno pitanje
            // Generisanje brojeva između 0 i 9
            num1 = random.nextInt(10); // Prvi broj
            num2 = random.nextInt(10); // Drugi broj
            num3 = random.nextInt(9) + 1; // Treći broj (delilac: 1 do 9)

            // Biranje operatora
            operator1 = random.nextBoolean() ? '+' : '-'; // Prvi operator (samo + ili -)
            operator2 = operators[random.nextInt(operators.length)]; // Drugi operator (+, -, *, /)

            // Provera za negativan rezultat u zagradi
            if (operator1 == '-' && num1 < num2) {
                int temp = num1;
                num1 = num2;
                num2 = temp; // Zamenjujemo mesta brojevima kako bi rezultat bio nenegativan
            }

            // Računanje rezultata iz zagrade
            int bracketResult = calculateResult(num1, num2, operator1);

            // Validacija za deljenje
            if (operator2 == '/') {
                // Provera za deljenje: rezultat iz zagrade mora biti veći od delioca i deljiv bez ostatka
                if (bracketResult < num3 || bracketResult % num3 != 0) {
                    System.out.println("Nevalidno pitanje: Rezultat zagrade " + bracketResult + " nije deljiv sa " + num3);
                    continue; // Ponovno generisanje
                }
            }

            // Konačan rezultat
            correctAnswer = calculateResult(bracketResult, num3, operator2);

            // Ako je rezultat validan, izlazimo iz petlje
            if (correctAnswer >= 0 && correctAnswer <= 25) {
                // Kreiranje pitanja
                String question = "(" + num1 + " " + operator1 + " " + num2 + ") " + operator2 + " " + num3;
                questionText.setText(question);
                break;
            }
        }

        // Generisanje opcija za odgovor
        ArrayList<Integer> options = new ArrayList<>();
        options.add(correctAnswer);
        while (options.size() < 4) {
            int wrongAnswer = new Random().nextInt(26); // Pogrešan odgovor u granicama (0-25)
            if (!options.contains(wrongAnswer)) {
                options.add(wrongAnswer);
            }
        }

        // Mešanje opcija
        Collections.shuffle(options);

        // Postavljanje opcija na dugmad
        btnOption1.setText(String.valueOf(options.get(0)));
        btnOption2.setText(String.valueOf(options.get(1)));
        btnOption3.setText(String.valueOf(options.get(2)));
        btnOption4.setText(String.valueOf(options.get(3)));

        // Dodavanje klikova za proveru odgovora
        btnOption1.setOnClickListener(v -> checkAnswer(Integer.parseInt(btnOption1.getText().toString())));
        btnOption2.setOnClickListener(v -> checkAnswer(Integer.parseInt(btnOption2.getText().toString())));
        btnOption3.setOnClickListener(v -> checkAnswer(Integer.parseInt(btnOption3.getText().toString())));
        btnOption4.setOnClickListener(v -> checkAnswer(Integer.parseInt(btnOption4.getText().toString())));

        // Postavljanje početnog vremena za praćenje potrošenog vremena
        questionStartTime = System.currentTimeMillis();
    }





    private void displayQuestion(int num1, int num2, char operator) {
        questionText.setText(num1 + " " + operator + " " + num2);

        Random random = new Random();
        ArrayList<Integer> options = new ArrayList<>();
        options.add(correctAnswer);
        while (options.size() < 4) {
            int wrongAnswer = random.nextInt(currentLevel == 3 ? 100 : 10);
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

    private int calculateResult(int num1, int num2, char operator) {
        switch (operator) {
            case '+':
                return num1 + num2;
            case '-':
                return num1 - num2;
            case '*':
                return num1 * num2;
            case '/':
                return num1 / num2;
            default:
                return 0;
        }
    }

    private void startTimer() {
        int timerDuration = currentLevel == 4 ? 6000 : (currentLevel == 3 ? 5000 : 4000);
        timer = new CountDownTimer(timerDuration, 100) {
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

    private void endGameWithFailure() {
        if (timer != null) {
            timer.cancel();
        }
        Toast.makeText(this, "Game Over! Too many wrong answers.", Toast.LENGTH_LONG).show();
        score = 0; // Postavi skor na 0
        finish();
    }


    private void checkAnswer(int selectedAnswer) {
        if (selectedAnswer == correctAnswer) {
            score += currentLevel;
        } else {
            wrongAnswerCount++;
            score -= 1;
            updateWrongAnswersText();
            if (wrongAnswerCount >= maxWrongAnswers) {
                endGameWithFailure();
                return; // Prekini dalje izvršavanje
            }
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

        // Prikaži poruku korisniku
        Toast.makeText(this, "Game Over! Final score: " + score, Toast.LENGTH_LONG).show();

        // Zaokruži vreme na 2 decimale
        double roundedTimeSpent = Math.round(totalTimeSpent * 100.0) / 100.0;

        // Čuvanje rezultata u lokalnu bazu
        Log.d("PlayGameActivity", "Saving game result locally.");
        localDatabaseHelper.addGameResult(username, score, roundedTimeSpent, wrongAnswerCount);

        // Čuvanje rezultata u globalnu bazu (Firestore)
        Log.d("PlayGameActivity", "Saving game result globally.");
        databaseHelper.addGameResult(username, score, roundedTimeSpent, wrongAnswerCount);


        // Vraćanje u glavni meni
        Intent intent = new Intent(PlayGameActivity.this, MainActivity.class);
        intent.putExtra("USERNAME", username);
        startActivity(intent);
        finish();

        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
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