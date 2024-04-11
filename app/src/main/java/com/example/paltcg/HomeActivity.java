package com.example.paltcg;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.paltcg.dataclasses.User;

public class HomeActivity extends AppCompatActivity {

    ActivityResultLauncher<Intent> activityResultLauncher;

    User user;

    androidx.appcompat.widget.Toolbar toolbar;

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        user = savedInstanceState.getParcelable("the_user");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);



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




        ConstraintLayout constraintLayout = findViewById(R.id.mainLayout);
        AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2500);
        animationDrawable.setExitFadeDuration(5000);
        animationDrawable.start();



        toolbar= findViewById(R.id.toolbar_home);
        toolbar.setOnMenuItemClickListener(new androidx.appcompat.widget.Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId()==R.id.profil_button){
                    profile();
                }else{
                    if(item.getItemId()==R.id.save){
                        Toast.makeText(HomeActivity.this,user.getEvaluation()+" nb etoiles",Toast.LENGTH_LONG).show();
                        partage();
                    }
                }
                return true;
            }
        });

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("the_user",user);
    }

    public void leave(View v) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("the_user",user);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    private void handleActivityResult(ActivityResult result) {
        if (result.getResultCode() == Activity.RESULT_OK) {
            Intent resultDatas = result.getData();
            Log.i("Returned value", "received result ok.");
            if (resultDatas != null) {
                user = resultDatas.getParcelableExtra("the_user");
                Log.i("HOMEACTIVITY", "handleActivityResult: returned from combat, rating :" + user.getUsername());
                Log.i("Returned value", "received user.");
            }
        }
    }

    public void goArene(android.view.View v){
        if (user.getDeckCardsIds().size() == 5)
            goNextActivity(new Intent(this, ArenaChoiceActivity.class));
        else {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle(R.string.not_enough_active_cards_title);
            alert.setMessage(R.string.not_enough_active_cards);
            alert.setPositiveButton(android.R.string.yes, (dialog, which) -> {
                seeDeck(v);
            });

            alert.show();
        }
    }

    public void seeDeck(View v) {
        goNextActivity(new Intent(this, DecksActivity.class));
    }

    public void goNextActivity(Intent intent) {
        intent.putExtra("the_user", user);
        activityResultLauncher.launch(intent);
    }
    public void profile(){
        Intent intent = new Intent(this,ProfileActivity.class);
        intent.putExtra("the_user",user);
        startActivity(intent);
    }

    public void stats(android.view.View View){
        Intent intent = new Intent(this,Stat_Activity.class);
        intent.putExtra("the_user",user);
        startActivity(intent);
    }

    public void partage() {

        String uriText =
                "mailto:tom.caillaud@etu.univ-poitiers.fr" +
                        "?subject=" + Uri.encode("TCG_POKEMON") +
                        "&body=" + Uri.encode(user.getStats());

        Uri uri = Uri.parse(uriText);

        Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
        sendIntent.setData(uri);
        startActivity(Intent.createChooser(sendIntent, "Send email"));
    }

}