package com.example.paltcg;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.paltcg.dataclasses.User;

import java.util.ArrayList;


public class End_Fight_Activity extends AppCompatActivity {

    VideoView video;
    TextView textView_winOrLose;

    LinearLayout cards;
    ImageView card1, card2, card3, card4, card5;
    RatingBar ratingBar;

    int won_card = 0 , loosed_card = 0;


    User user;

    String TAG = "blabla";

    boolean rated = false;

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        user = savedInstanceState.getParcelable("the_user");
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.end_fight);

        Log.i(TAG, "onCreate: debut");
        cards = findViewById(R.id.linearLayout_cards);
        card1 = findViewById(R.id.imageView_card1);
        card2 = findViewById(R.id.imageView_card2);
        card3 = findViewById(R.id.imageView_card3);
        card4 = findViewById(R.id.imageView_card4);
        card5 = findViewById(R.id.imageView_card5);

        Intent intent = getIntent();
        int result;
        result = intent.getIntExtra("result",0);
        ArrayList<Integer> oldCards = intent.getIntegerArrayListExtra("old_list");
        if (oldCards != null)
            Log.i(TAG, "onCreate: ancien deck " + oldCards.size());
        user = intent.getParcelableExtra("the_user");
        if (user != null) Log.i(TAG, "onCreate: nouveau deck " + user.getCardsIds());


        Uri uri;
        switch (result) {
            case 0 :
                uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.game_over);
                user.addLoosedBattle();
                break;
            case 1 :
                uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.run_away);
                user.addFleeBattle();
                break;
            case 2 :
                uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.win);
                user.addWonBattle();
                break;
            default :
                uri = null;
        }

        video = findViewById(R.id.videoView_endFight);
        if (uri == null)
            video.setVisibility(View.GONE);
        else video.setVideoURI(uri);

        textView_winOrLose = findViewById(R.id.textView_winOrLose);
        Log.i(TAG, "onCreate: " + textView_winOrLose);
        ArrayList<Integer> cardsToDisplay = new ArrayList<>();
        if (result < 2) {
            textView_winOrLose.setText(R.string.loosedPoke);
            if (oldCards != null) {
                cardsToDisplay = new ArrayList<>(oldCards);
                cardsToDisplay.removeAll(user.getCardsIds());
                Log.i(TAG, "onCreate: nb a afficher " + cardsToDisplay.size());
                user.addLoosedPoke(cardsToDisplay.size());
                loosed_card = cardsToDisplay.size();
            }
        }
        else {
            Log.i(TAG, "onCreate: " + R.string.wonPoke);
            textView_winOrLose.setText(R.string.wonPoke);
            if (oldCards != null) {
                cardsToDisplay = new ArrayList<>(user.getCardsIds());
                cardsToDisplay.removeAll(oldCards);
                Log.i(TAG, "onCreate: nb a afficher " + cardsToDisplay.size());
                user.addWonPoke(cardsToDisplay.size());
                won_card = cardsToDisplay.size();
            }
        }

        calculateExp(result);
        if(user.getExp() > (user.getLevel() * 20)) {
            user.setLevel();
            Toast.makeText(this,"Level Up : "+user.getLevel(),Toast.LENGTH_LONG).show();
        }

        int cardCount = cardsToDisplay.size();

        for (int i = 0; i < cardCount; i++) {
            if (i < 5) {  // Check if index is within card limit
                ((ImageView)cards.getChildAt(i)).setImageResource(cardsToDisplay.get(i));
            }
        }

        if (cardCount < 5) {
            for (int i = cardCount; i < 5; i++) {
                cards.getChildAt(i).setVisibility(View.GONE);
            }
        }

        ratingBar = findViewById(R.id.ratingBar_endfight);
        ratingBar.setMax(5);

        ratingBar.setOnTouchListener((v, event) -> {
            rated = true;
            return ratingBar.onTouchEvent(event);
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() { goHome(null);}
        });

    }
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("the_user", user);
    }


    public void goHome(android.view.View v){
        Log.i(TAG, "goHome: " + rated);
        if (!rated)
            Toast.makeText(this,getString(R.string.need_to_rate), Toast.LENGTH_SHORT).show();
        else {
            user.setEvaluation(ratingBar.getRating());
            Intent returnIntent = new Intent();
            returnIntent.putExtra("the_user", user);
            Log.i(TAG, "goHome: " + user.getEvaluation());
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        }
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


    private void calculateExp(int resultat){
        switch(resultat){
            case 0 : user.setExp((user.getLoosedBattle())/(user.getLevel() + user.getNbWonBattle() + loosed_card));
                    break;
            case 1 : user.setExp((user.getFleeBattle() + user.getNbBattles())/(user.getLevel() + user.getLoosedBattle() + loosed_card));
                    break;
            case 2 : user.setExp((user.getNbWonBattle() + won_card + user.getNbBattles())/user.getLevel());
                    break;
        }
    }


}