package com.example.paltcg;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.paltcg.dataclasses.User;

public class HomeActivity extends AppCompatActivity {

    ActivityResultLauncher<Intent> decksActivityResultLauncher;
    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        Intent intent = getIntent();
        user = intent.getParcelableExtra("the_user");
        if (user != null) Log.i("TAG", "onCreate: " + user.getUsername());

        ConstraintLayout constraintLayout = findViewById(R.id.constraintLayout_arene1);
        ImageButton battleButton = findViewById(R.id.imageButton_battleButton);

        decksActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                this::handleActivityResult
        );

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("the_user",user);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });


        AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2500);
        animationDrawable.setExitFadeDuration(5000);
        animationDrawable.start();

        AnimationDrawable animationDrawable_2 = (AnimationDrawable) battleButton.getBackground();
        animationDrawable_2.setEnterFadeDuration(10);
        animationDrawable_2.setExitFadeDuration(5000);
        animationDrawable_2.start();
    }

    private void handleActivityResult(ActivityResult result) {
        Log.i("Returned value", "received result but not ok.");
        if (result.getResultCode() == Activity.RESULT_OK) {
            Intent resultDatas = result.getData();
            Log.i("Returned value", "received result ok.");
            if (resultDatas != null) {
                user = resultDatas.getParcelableExtra("the_user");
                Log.i("Returned value", "received user.");
            }
        }
    }

    public void goArene(android.view.View v){
        Intent intent = new Intent(this,ArenaChoiceActivity.class);
        startActivity(intent);
    }

    public void seeDeck(View v) {
        Intent intent = new Intent(this, DecksActivity.class);
        intent.putExtra("the_user",user);
        decksActivityResultLauncher.launch(intent);
    }
}