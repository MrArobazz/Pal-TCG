package com.example.paltcg;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class Arena_1_Activity extends AppCompatActivity {

    ConstraintLayout Arena,Page;
    ImageView carte_active_bot;
    ImageView carte_bot , sprite_bot , sprite_player;
    ProgressBar progressBar_bot,progressBar_player;

    private int pv_bot = 100;
    private int pv_player = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.arena1);

        Arena = findViewById(R.id.constraint_layout_arena);
        Page = findViewById(R.id.constraintLayout_arene1);
        progressBar_bot = findViewById(R.id.progressBar_pv_bot_arene1);
        progressBar_player = findViewById(R.id.progressBar_pv_player_arene1);

        Intent intent = getIntent();
        int background = intent.getIntExtra("background",-1);
        if (background != -1) {
            Arena.setBackgroundResource(background);
        }
        carte_active_bot = (ImageView) findViewById(R.id.imageView_carte_active_bot);
        sprite_bot = (ImageView) findViewById(R.id.imageView_sprite_bot);
        sprite_player = (ImageView) findViewById(R.id.imageView_sprite_player);
        sprite_bot.setImageResource(R.drawable.charmander);
        sprite_player.setImageResource(R.drawable.charmander_back);
        AnimationDrawable animationDrawable = (AnimationDrawable) Page.getBackground();
        animationDrawable.setEnterFadeDuration(10);
        animationDrawable.setExitFadeDuration(5000);
        animationDrawable.start();

        progressBar_player.setMax(100);
        progressBar_player.setMin(0);

        progressBar_bot.setMax(100);
        progressBar_bot.setMin(0);

        progressBar_bot.setProgress(pv_bot);
        progressBar_player.setProgress(pv_player);
    }


    public void attack(android.view.View v){
        pv_bot -= 10;
        pv_player -=10;
        progressBar_bot.setProgress(pv_bot);
        progressBar_player.setProgress(pv_player);
    }



}