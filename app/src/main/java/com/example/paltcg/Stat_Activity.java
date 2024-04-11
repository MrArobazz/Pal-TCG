package com.example.paltcg;

import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.paltcg.dataclasses.User;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Locale;

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

        fleeBattles = (ProgressBar) findViewById(R.id.progressBar_flee);
        fleeBattles.setMax(100);
        wonPoke = (ProgressBar) findViewById(R.id.progressBar_catchedPoke);
        wonPoke.setMax(100);
        loosedPoke = (ProgressBar) findViewById(R.id.progressBar_lostpoke);
        loosedPoke.setMax(200);
        loosedBattles = (ProgressBar) findViewById(R.id.progressBar_Defeats);
        loosedBattles.setMax(100);
        wonBattles = (ProgressBar) findViewById(R.id.progressBar_Victories);
        wonBattles.setMax(100);
        evaluation = (RatingBar) findViewById(R.id.ratingBar_evaluation);
        evaluation.setNumStars(5);
        my_Poke = (TextView) findViewById(R.id.textView_mypoke);
        victories = (TextView) findViewById(R.id.textView_victories);
        defeats = (TextView) findViewById(R.id.textView_defeats);
        flee = (TextView) findViewById(R.id.textView_flee);
        pokeWon = (TextView) findViewById(R.id.textView_catchedpoke);
        pokeLoose = (TextView) findViewById(R.id.textView_lostpoke);
        totalPoke = (TextView) findViewById(R.id.textView_totalpoke);

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
        my_Poke.setText("" + user.getNbCards());
        pokeLoose.setText("" + user.getLoosedPoke());
        pokeWon.setText("" + user.getWonPoke());
        victories.setText("" + user.getNbWonBattle());
        defeats.setText("" + user.getLoosedBattle());
        flee.setText("" + user.getFleeBattle());
        fleeBattles.setProgress(user.getFleeBattle());
        totalPoke.setText("69");

    }


    public void save(android.view.View v) {
        if (!createSave((user))) {
            Toast.makeText(this, "Save Failed", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Save Success", Toast.LENGTH_LONG).show();
        }
    }

    private boolean createSave(User user) {

        try {
            File chemin = this.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
            File file = new File(chemin, user.getUsername() + sdf.format(new Date()) + "__test.txt");
            file.createNewFile();
            write(user, file);
        } catch (IOException e) {
            return false;
        }
        return true;


    }

    private boolean write(User user, File file) {
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(user.getStats());
            fileWriter.close();
        } catch (IOException e) {
            return false;
        }
        return true;
    }


}