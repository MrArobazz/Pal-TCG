package com.example.paltcg;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.paltcg.dataclasses.User;

public class Arena_1_Activity extends AppCompatActivity {

    ConstraintLayout arena;
    ImageView botActiveCard, playerActiveCard;
    WebView botPokemonSprite , playerPokemonSprite;
    ProgressBar progressBar_bot,progressBar_player;

    User player;

    private int pv_bot = 100;
    private int pv_player = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.arena1);

        //Get the compoents
        arena = findViewById(R.id.constraint_layout_arena);

        progressBar_bot = findViewById(R.id.progressBar_pv_bot_arene1);
        progressBar_player = findViewById(R.id.progressBar_pv_player_arene1);

        botActiveCard = findViewById(R.id.imageView_carte_active_bot);

        playerPokemonSprite = findViewById(R.id.webView_sprite_player_arena1);
        botPokemonSprite = findViewById(R.id.webView_sprite_bot_arena1);

        Intent intent = getIntent();
        player = intent.getParcelableExtra("the_user");
        int background = intent.getIntExtra("background",-1);
        if (background != -1) {
            arena.setBackgroundResource(background);
        }

        /*botPokemonSprite.loadUrl("https://projectpokemon.org/images/normal-sprite/charmander.gif");
        playerPokemonSprite.loadUrl("https://projectpokemon.org/images/sprites-models/normal-back/charmander.gif");

        progressBar_player.setMax(100);
        progressBar_player.setMin(0);

        progressBar_bot.setMax(100);
        progressBar_bot.setMin(0);

        progressBar_bot.setProgress(pv_bot);
        progressBar_player.setProgress(pv_player);*/
    }


    public void attack(android.view.View v){
        pv_bot -= 10;
        pv_player -=10;
        progressBar_bot.setProgress(pv_bot);
        progressBar_player.setProgress(pv_player);
    }



}