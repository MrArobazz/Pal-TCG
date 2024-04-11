package com.example.paltcg;

import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.os.Environment;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RatingBar;
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

    ProgressBar wonPoke, loosedPoke,wonBattles,loosedBattles;
    RatingBar evaluation;
    EditText my_Poke;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stat);

        Intent intent  = getIntent();
        user = intent.getParcelableExtra("the_user");

        //wonPoke = (ProgressBar) findViewById(R.id.progressBar_nb_pokemon_gagne);
        //loosedPoke = (ProgressBar) findViewById(R.id.progressBar_nb_pokemon_perdu);
        loosedBattles = (ProgressBar) findViewById(R.id.progressBar_Defeats);
        wonBattles = (ProgressBar) findViewById(R.id.progressBar_Victories);
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
        //wonPoke.setProgress(0);
        //loosedPoke.setProgress(0);
        evaluation.setRating(0);
        my_Poke.setText(0+" / 69");
    }

    public void UserNotNull(){
        wonBattles.setProgress(user.getNbWonBattle());
        loosedBattles.setProgress(user.getLoosedBattle());
        //wonPoke.setProgress(user.getWonPoke());
        //loosedPoke.setProgress(user.getLoosedPoke());
        evaluation.setRating((float)user.getEvaluation());
        my_Poke.setText(user.getNbCards()+" / 69");
    }


    public void save(android.view.View v){
        if(!createSave((user))){
            Toast.makeText(this,"Save Failed",Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this,"Save Success",Toast.LENGTH_LONG).show();
        }
    }

    private boolean createSave(User user){

        try{
            File chemin = this.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
            File file = new File(chemin,user.getUsername()+sdf.format(new Date())+"__test.txt");
            file.createNewFile();
            write(user,file);
        } catch (IOException e) {
            return false;
        }
        return true;


    }

    private boolean write(User user , File file) {
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write( "Pseudo : "+  user.getUsername());
            fileWriter.write(" : "+  user.getUsername());
            fileWriter.write(R.string.loosedPoke + " : "+  user.getUsername());
            fileWriter.write(R.string.Pokemons + " : "+  user.getUsername());

            fileWriter.close();
        } catch (IOException e) {
            return false;
        }
        return true;
    }
}