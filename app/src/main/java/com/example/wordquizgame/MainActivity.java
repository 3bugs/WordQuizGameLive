package com.example.wordquizgame;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.example.wordquizgame.db.DatabaseHelper;


public class MainActivity extends ActionBarActivity {

    private static final String TAG = "MainActivity";
    public static final String NAME_KEY = "name";
    public static final String AGE_KEY = "age";
    public static final String DIFF = "diff";

    private Button btnPlayGame, btnHighScore;
    private String[] diffLabel = {"ง่าย", "ปานกลาง", "ยาก"};

    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private SimpleCursorAdapter adapter;

    private ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        dbHelper = new DatabaseHelper(this);
        db = dbHelper.getWritableDatabase();

        Log.i(TAG, "onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list = (ListView) findViewById(R.id.listView);

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

                        Intent intent = new Intent(MainActivity.this, GameActivity.class);
                        intent.putExtra(DIFF, which);
                        startActivity(intent);
                    }
                });

                dialog.show();
            }
        });

        btnHighScore = (Button) findViewById(R.id.highScoreButton);
        btnHighScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readScore();
            }
        });
    }

    private void readScore() {
        String[] queryColumns = {
                DatabaseHelper.COL_ID, DatabaseHelper.COL_SCORE, DatabaseHelper.COL_DIFFICULTY };
        String[] showColumns = {
                DatabaseHelper.COL_SCORE, DatabaseHelper.COL_DIFFICULTY };

        int[] views = { android.R.id.text1, android.R.id.text2 };

        Cursor cursor = db.query(DatabaseHelper.TABLE_NAME, queryColumns, null, null, null, null,
                null);

        adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, cursor,
                showColumns, views, 0);

        list.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");

        Music.play(this, R.raw.main);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");

        Music.stop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "onRestart");
    }
}
















