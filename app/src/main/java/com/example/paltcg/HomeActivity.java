package com.example.paltcg;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toolbar;

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

    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);



        Intent intent = getIntent();
        user = intent.getParcelableExtra("the_user");
        if (user != null) Log.i("TAG", "onCreate: " + user.getUsername());



        decksActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                this::handleActivityResult
        );

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() { leave(null);}
        });




        ConstraintLayout constraintLayout = findViewById(R.id.mainLayout);
        AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2500);
        animationDrawable.setExitFadeDuration(5000);
        animationDrawable.start();



        /*toolbar= (Toolbar) findViewById(R.id.toolbar_home);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId()==R.id.profil_button){
                   profile();
                }
                return true;
            }
        });*/


    }

    public void leave(View v) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("the_user",user);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
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
    public void profile(){
        Intent intent = new Intent(this,ProfileActivity.class);
        if(user!=null){
            intent.putExtra("the_user",user);
        }
        startActivity(intent);
    }




}