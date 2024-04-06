package com.example.paltcg;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class ArenaChoiceActivity extends AppCompatActivity {

    ImageButton imageButton_arene1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.arena_choice);


        ConstraintLayout constraintLayout = findViewById(R.id.constraintLayout_arene1);
        AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2500);
        animationDrawable.setExitFadeDuration(5000);
        animationDrawable.start();

        imageButton_arene1 = findViewById(R.id.imageButton_arene1);

    }


    public void goArene4(android.view.View v){
        Intent intent = new Intent(this,Arena_1_Activity.class);
        intent.putExtra("background", R.drawable.arene4);
        startActivity(intent);
    }
    public void goArene3(android.view.View v){
        Intent intent = new Intent(this, Arena_3_Activity.class);
        //intent.putExtra("background", R.drawable.arene3);
        startActivity(intent);
    }
    public void goArene2(android.view.View v){
        Intent intent = new Intent(this,Arena_2_Activity.class);
        intent.putExtra("background", R.drawable.arene2);
        startActivity(intent);
    }
    public void goArene1(android.view.View v){
        Intent intent = new Intent(this,Arena_1_Activity.class);
        intent.putExtra("background", R.drawable.arene1);
        startActivity(intent);
    }
}