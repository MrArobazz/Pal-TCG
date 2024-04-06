package com.example.paltcg;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.bottomappbar.BottomAppBar;

public class Arena_2_Activity extends AppCompatActivity {

    ConstraintLayout Arena;
    CoordinatorLayout Page;
    BottomAppBar bottomAppBar;
    ProgressBar progressBar_bot,progressBar_player;
    VideoView sprite_player;

    private int pv_bot = 100;
    private int pv_player = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.arena2);

        Arena = findViewById(R.id.constraint_layout_arena2);
        Page = findViewById(R.id.coordinatorLayout_arene2);

        progressBar_bot = findViewById(R.id.progressBar_pv_bot_arene2);
        progressBar_player = findViewById(R.id.progressBar_pv_player_arene2);

        AnimationDrawable animationDrawable = (AnimationDrawable) Page.getBackground();
        animationDrawable.setEnterFadeDuration(10);
        animationDrawable.setExitFadeDuration(5000);
        animationDrawable.start();

        Intent intent = getIntent();
        int background = intent.getIntExtra("background",-1);
        if (background != -1) {
            Arena.setBackgroundResource(background);
        }

        bottomAppBar = findViewById(R.id.bottom_app_bar_arene2);
        bottomAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId()==R.id.save){
                    Toast.makeText(Arena_2_Activity.this,"Save",Toast.LENGTH_SHORT).show();
                }
                if(item.getItemId()==R.id.setting){
                    Toast.makeText(Arena_2_Activity.this,"Setting",Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });

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