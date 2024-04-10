package com.example.paltcg;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.Toast;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.bottomappbar.BottomAppBar;

public class Arena_2_Activity extends AppCompatActivity {

    ConstraintLayout Arena;
    BottomAppBar bottomAppBar;
    ProgressBar progressBar_bot,progressBar_player;
    WebView sprite_bot , sprite_player;

    private int pv_bot = 100;
    private int pv_player = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.arena2);

        Arena = findViewById(R.id.constraint_layout_arena2);

        progressBar_bot = findViewById(R.id.progressBar_pv_bot_arene2);
        progressBar_player = findViewById(R.id.progressBar_pv_player_arene2);

        sprite_player = (WebView) findViewById(R.id.webView_sprite_player_arena2);
        sprite_bot = (WebView) findViewById(R.id.webView_sprite_bot_arena2);



        sprite_bot.loadUrl("https://projectpokemon.org/images/normal-sprite/squirtle.gif");
        sprite_player.loadUrl("https://projectpokemon.org/images/sprites-models/normal-back/squirtle.gif");

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
        pv_bot -= 1;
        pv_player -=10;
        progressBar_bot.setProgress(pv_bot);
        progressBar_player.setProgress(pv_player);
        if(pv_bot <= 0 || pv_player <= 0){
            goBilan();
        }
    }

    public void goBilan(){
        boolean to_send = false;
        Intent intent = new Intent(this,End_Fight_Activity.class);
        if(pv_player==0) {
            to_send = true;
        }
        intent.putExtra("result",to_send);
        startActivity(intent);

    }

}