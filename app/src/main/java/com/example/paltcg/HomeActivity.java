package com.example.paltcg;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class HomeActivity extends AppCompatActivity {

    private ImageButton battleButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        ConstraintLayout constraintLayout = findViewById(R.id.constraintLayout_arene1);
        battleButton = findViewById(R.id.imageButton_battleButton);

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
}