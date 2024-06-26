package com.example.paltcg;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.paltcg.dataclasses.User;

public class ArenaChoiceActivity extends AppCompatActivity{

    ActivityResultLauncher<Intent> activityResultLauncher;

    User user;

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        user = savedInstanceState.getParcelable("the_user");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.arena_choice);

        Intent intent = getIntent();
        user = intent.getParcelableExtra("the_user");
        if (user != null) Log.i("TAG", "onCreate: " + user.getUsername());

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                this::handleActivityResult
        );

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() { leave(null);}
        });

        ConstraintLayout constraintLayout = findViewById(R.id.constraintLayout_arene1);
        AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2500);
        animationDrawable.setExitFadeDuration(5000);
        animationDrawable.start();

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("the_user",user);
    }

    public void leave(View v) {
        Intent returnIntent = new Intent();
        Log.i("TAG", "leave: " + user.getUsername());
        returnIntent.putExtra("the_user",user);
        Log.i("TAG", "leave: " + user.getEvaluation());
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    private void handleActivityResult(ActivityResult result) {
        if (result.getResultCode() == Activity.RESULT_OK) {
            Intent resultDatas = result.getData();
            if (resultDatas != null) {
                user = resultDatas.getParcelableExtra("the_user");
            }
            leave(null);
        }
    }

    public void goArene1(android.view.View v){
        Intent intent = new Intent(this,Arena_1_Activity.class);
        intent.putExtra("background", R.drawable.battlefiled_1);
        goNextActivity(intent);
    }

    public void goArene2(android.view.View v){
        Intent intent = new Intent(this,Arena_2_Activity.class);
        intent.putExtra("background", R.drawable.battlefield_2);
        goNextActivity(intent);
    }

    public void goArene3(android.view.View v){
        Intent intent = new Intent(this, Arena_3_Activity.class);
        intent.putExtra("background", R.drawable.battlefield_3);
        goNextActivity(intent);
    }

    public void goNextActivity(Intent intent) {
        intent.putExtra("the_user", user);
        activityResultLauncher.launch(intent);
    }
}