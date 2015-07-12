package com.example.wordquizgame;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.wordquizgame.db.DatabaseHelper;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;


public class GameActivity extends ActionBarActivity {

    private static final String TAG = "GameActivity";
    private int difficulty;

    private ArrayList<String> fileNameList;
    private ArrayList<String> quizWordsList;
    private ArrayList<String> choiceWords;

    private String answerFileName;
    private int totalGuesses;
    private int score;
    private int numChoices;

    private Random random;
    private Handler handler;

    private TextView questionNumberTextView;
    private ImageView questionImageView;
    private TableLayout buttonTableLayout;
    private TextView answerTextView;

    private Animation shakeAnimation;
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
        dbHelper.close();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        dbHelper = new DatabaseHelper(this);
        db = dbHelper.getWritableDatabase();

        shakeAnimation = AnimationUtils.loadAnimation(this, R.anim.shake);
        shakeAnimation.setRepeatCount(3);

        Intent intent = getIntent();
        difficulty = intent.getIntExtra(MainActivity.DIFF, 0);

        Log.i(TAG, "ค่า diff ที่ส่งมาคือ " + difficulty);

        switch (difficulty) {
            case 0:
                numChoices = 2;
                break;
            case 1:
                numChoices = 4;
                break;
            case 2:
                numChoices = 6;
                break;
        }

        questionNumberTextView = (TextView) findViewById(R.id.questionNumberTextView);
        questionImageView = (ImageView) findViewById(R.id.questionImageView);
        buttonTableLayout = (TableLayout) findViewById(R.id.buttonTableLayout);
        answerTextView = (TextView) findViewById(R.id.answerTextView);

        fileNameList = new ArrayList<String>();
        quizWordsList = new ArrayList<String>();
        choiceWords = new ArrayList<String>();

        random = new Random();
        handler = new Handler();

        getImageFileName();
    }

    private void getImageFileName() {
        String[] categories = {
                "animals", "body", "colors", "numbers", "objects" };

        AssetManager assets = getAssets();

        for (String category : categories) {
            try {
                String[] fileNames = assets.list(category);

                for (String fileName : fileNames) {
                    fileNameList.add(fileName.replace(".png", ""));
                }

            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "Error listing filename in " + category);
            }
        }

        Log.i(TAG, "***** รายชื่อไฟล์ทั้งหมด *****");
        for (String fileName : fileNameList) {
            Log.i(TAG, fileName);
        }

        startQuiz();
    }

    private void startQuiz() {
        totalGuesses = 0;
        score = 0;
        quizWordsList.clear();

        while (quizWordsList.size() < 5) {
            int randomIndex = random.nextInt(fileNameList.size());
            String fileName = fileNameList.get(randomIndex);

            if (quizWordsList.contains(fileName) == false) {
                quizWordsList.add(fileName);
            }
        }

        Log.i(TAG, "***** ชื่อไฟล์สำหรับตั้งโจทย์ที่สุ่มได้ *****");
        for (String fileName : quizWordsList) {
            Log.i(TAG, fileName);
        }

        loadNextQuestion();
    }

    private void loadNextQuestion() {
        answerTextView.setText(null);
        answerFileName = quizWordsList.remove(0);

        String msg = String.format("คำถามข้อที่ %d จากทั้งหมด 5 ข้อ", score + 1);
        questionNumberTextView.setText(msg);

        loadQuestionImage();
        prepareChoiceWords();
    }

    private void loadQuestionImage() {
        String category = answerFileName.substring(
                0, answerFileName.indexOf('-'));
        String filePath = category + "/" + answerFileName + ".png";

        AssetManager assets = getAssets();
        InputStream stream;

        try {
            stream = assets.open(filePath);

            Drawable image = Drawable.createFromStream(stream, filePath);
            questionImageView.setImageDrawable(image);

        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Error loading file: " + filePath);
        }
    }

    private void prepareChoiceWords() {
        choiceWords.clear();

        while (choiceWords.size() < numChoices) {
            int randomIndex = random.nextInt(fileNameList.size());
            String randomWord = getWord(fileNameList.get(randomIndex));

            if (choiceWords.contains(randomWord) == false &&
                    randomWord.equals(getWord(answerFileName)) == false) {
                choiceWords.add(randomWord);
            }
        }

        int randomIndex = random.nextInt(numChoices);
        choiceWords.set(randomIndex, getWord(answerFileName));

        Log.i(TAG, "***** คำศัพท์ตัวเลือกที่สุ่มได้ *****");
        for (String word : choiceWords) {
            Log.i(TAG, word);
        }

        createChoiceButtons();
    }

    private void createChoiceButtons() {
        for (int row = 0; row < buttonTableLayout.getChildCount(); row++) {
            TableRow tr = (TableRow) buttonTableLayout.getChildAt(row);
            tr.removeAllViews();
        }

        LayoutInflater inflater = (LayoutInflater)
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        for (int row = 0; row < numChoices / 2; row++) {
            TableRow tr = (TableRow) buttonTableLayout.getChildAt(row);

            for (int column = 0; column < 2; column++) {
                Button guessButton = (Button) inflater.inflate(
                        R.layout.guess_button, tr, false);

                guessButton.setText(choiceWords.get((row * 2) + column));
                guessButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        submitGuess((Button) v);
                    }
                });

                tr.addView(guessButton);
            }
        }
    }

    private void submitGuess(Button guessButton) {
        String guessWord = guessButton.getText().toString();
        String answer = getWord(answerFileName);
        totalGuesses++;

        // ตอบถูก
        if (guessWord.equals(answer)) {
            disableAllButtons();

            MediaPlayer mp = MediaPlayer.create(this, R.raw.applause);
            mp.setVolume(0.5f, 0.5f);
            mp.start();

            score++;

            String msg = answer + " ถูกต้องนะคร้าบบบ";
            answerTextView.setText(msg);
            answerTextView.setTextColor(
                    getResources().getColor(R.color.correct_answer));

            // เล่นครบ 5 ข้อแล้ว (จบเกม)
            if (score == 5) {

                saveScore();

                String msgResult = String.format(
                        "จำนวนครั้งที่ทาย: %d\nเปอร์เซ็นต์ความถูกต้อง: %.1f",
                        totalGuesses, 100 * 5 / (double) totalGuesses);


                new AlertDialog.Builder(this)
                        .setTitle("สรุปผล")
                        .setMessage(msgResult)
                        .setCancelable(false)
                        .setPositiveButton("เริ่มเกมใหม่", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startQuiz();
                            }
                        }).setNegativeButton("กลับหน้าหลัก", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).show();
            }
            // ตอบถูก แต่เกมยังไม่จบ (ยังไม่ครบ 5 ข้อ)
            else {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadNextQuestion();
                    }
                }, 2000);
            }
        }
        // ตอบผิด
        else {
            guessButton.setEnabled(false);

            questionImageView.startAnimation(shakeAnimation);
            guessButton.startAnimation(shakeAnimation);

            MediaPlayer mp = MediaPlayer.create(this, R.raw.fail3);
            mp.start();

            String msg = "ผิดครับ ลองใหม่นะครับ";
            answerTextView.setText(msg);
            answerTextView.setTextColor(
                    getResources().getColor(R.color.wrong_answer));
        }
    }

    private void saveScore() {
        double score = (100 * 5) / (double) totalGuesses;
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COL_SCORE, score);
        cv.put(DatabaseHelper.COL_DIFFICULTY, difficulty);

        long result = db.insert(DatabaseHelper.TABLE_NAME, null, cv);

        if (result == -1) {
            Log.e(TAG, "Error inserting data into database");
        }
    }

    private void disableAllButtons() {
        for (int row = 0; row < buttonTableLayout.getChildCount(); row++) {
            TableRow tr = (TableRow) buttonTableLayout.getChildAt(row);
            for (int column = 0; column < tr.getChildCount(); column++) {
                tr.getChildAt(column).setEnabled(false);
            }
        }
    }

    private String getWord(String fileName) {
        String word = fileName.substring(fileName.indexOf('-') + 1);
        return word;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Music.play(this, R.raw.game);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Music.stop();
    }
}














