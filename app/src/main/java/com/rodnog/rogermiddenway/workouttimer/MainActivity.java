package com.rodnog.rogermiddenway.workouttimer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Time;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {


    private TextView lastTimeTextView;
    private Chronometer timerTextView;
    private EditText workoutTypeTextEdit;

    boolean isRunning;

    private long pauseTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialising views and pauseTime variable
        lastTimeTextView = findViewById(R.id.lastTimeTextView);
        timerTextView = findViewById(R.id.timerTextView);
        workoutTypeTextEdit = findViewById(R.id.workoutTypeEditText);

        pauseTime = 0;

        // Getting last workout time and type from shared prefs
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        long workoutTime = sharedPref.getLong("WORKOUT_TIME", 0);
        String workoutType = sharedPref.getString("WORKOUT_TYPE", "");

        // The text field is hidden if there is no previous workout
        if(workoutTime == 0) {
            lastTimeTextView.setVisibility(View.INVISIBLE);
        }
        else {
            lastTimeTextView.setVisibility(View.VISIBLE);

            long second = (workoutTime / 1000) % 60;
            long minute = (workoutTime / (1000 * 60)) % 60;

            String time = String.format("%02d:%02d",  minute, second);

            lastTimeTextView.setText("You spent " + time + " on " + workoutType + " last time.");
        }

        // Handling orientation changes, we store the pauseTime and isRunning variables, and the
        // current base time of the chronometer
        if(savedInstanceState != null) {
            pauseTime = savedInstanceState.getLong("PAUSE_TIME");
            isRunning = savedInstanceState.getBoolean("IS_RUNNING");
            long baseTime = savedInstanceState.getLong("BASE_TIME");

            // If the pauseTime variable has a value, set it in order to give the chronometer the
            // correct value
            if(pauseTime != 0) {
                timerTextView.setBase(SystemClock.elapsedRealtime() - pauseTime);
                timerTextView.stop();
            }
            // If the timer is running, set the value based on baseTime variable and start the chronometer
            if(isRunning) {
                isRunning = true;
                timerTextView.setBase(SystemClock.elapsedRealtime() - (SystemClock.elapsedRealtime() - baseTime));
                timerTextView.start();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("PAUSE_TIME", pauseTime);
        outState.putLong("BASE_TIME", timerTextView.getBase());
        outState.putBoolean("IS_RUNNING", isRunning);

    }

    public void startTimer(View view) {
        if(workoutTypeTextEdit.getText().toString().isEmpty()){
            Toast.makeText(this, "Please enter a workout type", Toast.LENGTH_LONG).show();
        }
        else {
            isRunning = true;
            timerTextView.setBase(SystemClock.elapsedRealtime() - pauseTime);
            timerTextView.start();
        }
    }
    public void pauseTimer(View view) {
        if(isRunning) {
            isRunning = false;
            pauseTime = SystemClock.elapsedRealtime() - timerTextView.getBase();
            timerTextView.stop();
        }
    }
    public void stopTimer(View view) {
        if(workoutTypeTextEdit.getText().toString().isEmpty()){
            Toast.makeText(this, "Please enter a workout type", Toast.LENGTH_LONG).show();
        }
        else {
            isRunning = false;
            pauseTime = 0;

            long totalTime = SystemClock.elapsedRealtime() - timerTextView.getBase();

            if (totalTime > 0) {
                SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putLong("WORKOUT_TIME", totalTime);
                editor.putString("WORKOUT_TYPE", workoutTypeTextEdit.getText().toString());
                editor.apply();
            }
            timerTextView.setBase(SystemClock.elapsedRealtime());
            timerTextView.stop();
        }
    }
}
