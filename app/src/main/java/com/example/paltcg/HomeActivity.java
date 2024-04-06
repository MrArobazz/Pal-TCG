package com.example.paltcg;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.paltcg.dataclasses.User;

public class HomeActivity extends AppCompatActivity {

    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        Intent intent = getIntent();
        user = intent.getParcelableExtra("the_user");
        assert user != null;
        Log.i("TAG", "onCreate: " + user.getUsername());

        ConstraintLayout constraintLayout = findViewById(R.id.constraintLayout_arene1);
        ImageButton battleButton = findViewById(R.id.imageButton_battleButton);

        AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2500);
        animationDrawable.setExitFadeDuration(5000);
        animationDrawable.start();

        AnimationDrawable animationDrawable_2 = (AnimationDrawable) battleButton.getBackground();
        animationDrawable_2.setEnterFadeDuration(10);
        animationDrawable_2.setExitFadeDuration(5000);
        animationDrawable_2.start();
    }

    public void goArene(android.view.View v){
        Intent intent = new Intent(this,ArenaChoiceActivity.class);
        startActivity(intent);
    }

    public void seeDeck(View v) {
        Intent intent = new Intent(this, DecksActivity.class);
        intent.putExtra("the_user",user);
        startActivity(intent);
    }
}