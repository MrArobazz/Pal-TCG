package com.example.paltcg;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.lang.reflect.Constructor;

public class MainActivity extends AppCompatActivity {

    MediaPlayer mainTheme;
    public void debug(android.view.View v) {
        Log.i("", "debug: clicked");
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);

        ConstraintLayout constraintLayout = findViewById(R.id.mainLayout);
        AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(10);
        animationDrawable.setExitFadeDuration(5000);
        animationDrawable.start();

        playMusic();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopMusic();
    }

    @Override
    protected void onResume() {
        super.onResume();
        playMusic();
    }

    public void goSignUpView(android.view.View v) {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    private void playMusic() {
        if (mainTheme == null) {
            mainTheme = MediaPlayer.create(this, R.raw.title_theme);
            mainTheme.start();
        }
    }

    private void stopMusic() {
        mainTheme.release();
        mainTheme = null;
    }
}