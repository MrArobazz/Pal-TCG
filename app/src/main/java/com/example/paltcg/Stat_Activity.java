package com.example.paltcg;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.paltcg.dataclasses.User;

public class Stat_Activity extends AppCompatActivity {

    User user;

    ProgressBar wonPoke, loosedPoke;
    SeekBar wonBattles,loosedBattles;
    RatingBar evaluation;
    EditText my_Poke;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stat);

        Intent intent  = getIntent();
        user = intent.getParcelableExtra("the_user");

        wonPoke = (ProgressBar) findViewById(R.id.progressBar_nb_pokemon_gagne);
        loosedPoke = (ProgressBar) findViewById(R.id.progressBar_nb_pokemon_perdu);
        loosedBattles = (SeekBar) findViewById(R.id.seekBar_nb_defaite);
        wonBattles = (SeekBar) findViewById(R.id.seekBar_nb_victoire);
        evaluation = (RatingBar) findViewById(R.id.ratingBar_evaluation_combat);
        my_Poke = (EditText) findViewById(R.id.editText_nb_pokemon);

        if(user == null){
            Toast.makeText(this,"user null",Toast.LENGTH_LONG).show();
            UserNull();
        }else{
            UserNotNull();
        }




    }


    public void goHome(android.view.View View){
        finish();
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState){
        if(savedInstanceState.containsKey("the_user")){
            user = savedInstanceState.getParcelable("the_user");
        }else{
            user = null;
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("the_user",user);
    }

    public void UserNull(){
        wonBattles.setProgress(0);
        loosedBattles.setProgress(0);
        wonPoke.setProgress(0);
        loosedPoke.setProgress(0);
        evaluation.setRating(0);
        my_Poke.setText(0+"/69");
    }

    public void UserNotNull(){
        wonBattles.setProgress(user.getNbWonBattle());
        loosedBattles.setProgress(user.getLoosedBattle());
        wonPoke.setProgress(user.getWonPoke());
        loosedPoke.setProgress(user.getLoosedPoke());
        evaluation.setRating((float)user.getEvaluation());
        my_Poke.setText(user.getNbCards()+"/69");
    }
}