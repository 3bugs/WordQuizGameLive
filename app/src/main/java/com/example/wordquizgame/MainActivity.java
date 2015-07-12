package com.example.wordquizgame;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {

    private static final String TAG = "MainActivity";
    public static final String NAME_KEY = "name";
    public static final String AGE_KEY = "age";

    private Button btnPlayGame, btnHighScore;
    private String[] diffLabel = {"ง่าย", "ปานกลาง", "ยาก"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnPlayGame = (Button) findViewById(R.id.playGameButton);
        btnPlayGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setTitle("เลือกระดับความยาก");

                dialog.setItems(diffLabel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i(TAG, "ผู้ใช้เลือก " + which);
                    }
                });

                dialog.show();

                //dialog.setMessage("hello");
/*
                dialog.setPositiveButton("ง่าย", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialog.setNegativeButton("ยาก", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
*/


/*
                Log.i(TAG, "ปุ่มเล่นเกมถูกคลิก");
                Intent intent = new Intent(MainActivity.this, GameActivity.class);
                intent.putExtra(NAME_KEY, "Promlert");
                intent.putExtra(AGE_KEY, 40);
                startActivity(intent);
*/
            }
        });

        btnHighScore = (Button) findViewById(R.id.highScoreButton);
        btnHighScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "ปุ่มคะแนนสูงสุดถูกคลิก");
            }
        });

    }


}
