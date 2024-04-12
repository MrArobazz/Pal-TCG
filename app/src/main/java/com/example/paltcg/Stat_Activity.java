package com.example.paltcg;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.paltcg.dataclasses.User;

public class Stat_Activity extends AppCompatActivity {

    User user;

    ProgressBar wonPoke, loosedPoke, wonBattles, loosedBattles, fleeBattles;
    RatingBar evaluation;
    TextView my_Poke, victories, defeats, flee, pokeWon, pokeLoose, totalPoke;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stat);

        Intent intent = getIntent();
        user = intent.getParcelableExtra("the_user");

        fleeBattles = findViewById(R.id.progressBar_flee);
        fleeBattles.setMax(100);
        wonPoke = findViewById(R.id.progressBar_catchedPoke);
        wonPoke.setMax(100);
        loosedPoke = findViewById(R.id.progressBar_lostpoke);
        loosedPoke.setMax(200);
        loosedBattles = findViewById(R.id.progressBar_Defeats);
        loosedBattles.setMax(100);
        wonBattles = findViewById(R.id.progressBar_Victories);
        wonBattles.setMax(100);
        evaluation = findViewById(R.id.ratingBar_evaluation);
        evaluation.setNumStars(5);
        my_Poke = findViewById(R.id.textView_mypoke);
        victories = findViewById(R.id.textView_victories);
        defeats = findViewById(R.id.textView_defeats);
        flee = findViewById(R.id.textView_flee);
        pokeWon = findViewById(R.id.textView_catchedpoke);
        pokeLoose = findViewById(R.id.textView_lostpoke);
        totalPoke = findViewById(R.id.textView_totalpoke);

        if (user == null) {
            Toast.makeText(this, "user null", Toast.LENGTH_LONG).show();
            UserNull();
        } else {
            UserNotNull();
        }


    }


    public void goHome(android.view.View View) {
        finish();
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        if (savedInstanceState.containsKey("the_user")) {
            user = savedInstanceState.getParcelable("the_user");
        } else {
            user = null;
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("the_user", user);
    }

    public void UserNull() {
        wonBattles.setProgress(0);
        loosedBattles.setProgress(0);
        wonPoke.setProgress(0);
        loosedPoke.setProgress(0);
        evaluation.setRating(0);
    }

    public void UserNotNull() {
        wonBattles.setProgress(user.getNbWonBattle());
        loosedBattles.setProgress(user.getLoosedBattle());
        wonPoke.setProgress(user.getWonPoke());
        loosedPoke.setProgress(user.getLoosedPoke());
        evaluation.setRating((float) user.getEvaluation());
        my_Poke.setText(String.valueOf(user.getNbCards()));
        pokeLoose.setText(String.valueOf(user.getLoosedPoke()));
        pokeWon.setText(String.valueOf(user.getWonPoke()));
        victories.setText(String.valueOf(user.getNbWonBattle()));
        defeats.setText(String.valueOf(user.getLoosedBattle()));
        flee.setText(String.valueOf(user.getFleeBattle()));
        fleeBattles.setProgress(user.getFleeBattle());
        totalPoke.setText(R.string.cards_number);

    }


    public void save(android.view.View v) {
        user.saveUser(this);
    }


}