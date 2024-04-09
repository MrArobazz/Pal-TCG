package com.example.paltcg;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.example.paltcg.dataclasses.User;

import java.util.ArrayList;


public class End_Fight_Activity extends AppCompatActivity {

    VideoView video;
    TextView textView_winOrLose;

    LinearLayout cards;
    ImageView card1, card2, card3, card4, card5;
    RatingBar ratingBar;


    User user;

    String TAG = "blabla";
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
        int result = 0;
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
                break;
            case 1 :
                uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.run_away);
                break;
            case 2 :
                uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.win);
                break;
            default :
                uri = null;
        }

        video = findViewById(R.id.videoView_endFight);
        if (uri == null)
            video.setVisibility(View.GONE);
        else video.setVideoURI(uri);

        textView_winOrLose = findViewById(R.id.textView_winOrLose);
        ArrayList<Integer> cardsToDisplay = new ArrayList<>();
        if (result < 2) {
            textView_winOrLose.setText(R.string.loosedPoke);
            if (oldCards != null) {
                cardsToDisplay = new ArrayList<>(oldCards);
                cardsToDisplay.removeAll(user.getCardsIds());
                Log.i(TAG, "onCreate: nb a afficher " + cardsToDisplay.size());
            }
        }
        else {
            textView_winOrLose.setText(R.string.wonPoke);
            if (oldCards != null) {
                cardsToDisplay = new ArrayList<>(user.getCardsIds());
                cardsToDisplay.removeAll(oldCards);
                Log.i(TAG, "onCreate: nb a afficher " + cardsToDisplay.size());
            }
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

        if(user!=null){
            if (result < 2)
                user.addLoosedBattle();
            else user.addWonBattle();
        }

        ratingBar = findViewById(R.id.ratingBar_endfight);
        ratingBar.setMax(5);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() { goHome(null);}
        });
    }

    public void goHome(android.view.View v){
        user.setEvaluation(ratingBar.getRating());
        Intent returnIntent = new Intent();
        returnIntent.putExtra("the_user",user);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
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