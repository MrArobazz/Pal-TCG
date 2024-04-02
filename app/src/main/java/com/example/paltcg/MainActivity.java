package com.example.paltcg;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class MainActivity extends AppCompatActivity {

    MediaPlayer mainTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);

        //TODO: check si compte existant ou non
        findViewById(R.id.imageView_profilePic).setVisibility(View.GONE);
        TextView text = (TextView)findViewById(R.id.textView_accountUsername);
        text.setText(R.string.need_to_create_a_account);
        findViewById(R.id.textView_completion).setVisibility(View.GONE);

        ConstraintLayout constraintLayout = findViewById(R.id.mainLayout);
        AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(10);
        animationDrawable.setExitFadeDuration(5000);
        animationDrawable.start();

        playMusic();
    }

    @Override
    protected void onResume() {
        super.onResume();
        playMusic();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopMusic();
    }

    public void goSignUpView(android.view.View v) {
        Intent intent = new Intent(this, SignUpActivity.class);
        intent.putExtra("music_time", mainTheme.getCurrentPosition());
        startActivity(intent);
    }

    private void playMusic() {
        if (mainTheme == null)
            mainTheme = MediaPlayer.create(this, R.raw.title_theme);
        mainTheme.start();
    }

    private void stopMusic() {
        mainTheme.release();
        mainTheme = null;
    }

    public void leaveApp(android.view.View v) {
        finish();
        System.exit(0);
    }
}