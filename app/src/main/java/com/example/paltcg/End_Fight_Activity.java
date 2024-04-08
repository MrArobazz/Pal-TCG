package com.example.paltcg;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.RatingBar;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class End_Fight_Activity extends AppCompatActivity {

    VideoView video;

    RatingBar ratingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.end_fight);

        Intent intent =getIntent();
        boolean result = false;
        if(intent != null){
            result = intent.getBooleanExtra("result",false);
        }
        Uri uri;
        if(!result){
            uri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.win);
        }else{
            uri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.game_over);

        }

        video = (VideoView) findViewById(R.id.videoView_endFight);
        video.setVideoURI(uri);

        ratingBar = (RatingBar) findViewById(R.id.ratingBar_endfight);
        ratingBar.setMax(5);

    }

    public void goHome(android.view.View v){
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStart(){
        super.onStart();
        video.start();
    }

    @Override
    protected void onResume(){
        super.onResume();
        video.start();
    }

    @Override
    protected void onPause(){
        super.onPause();
        video.suspend();
    }
}