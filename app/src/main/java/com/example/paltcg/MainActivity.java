package com.example.paltcg;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.paltcg.dataclasses.User;

import java.util.SortedSet;
import java.util.TreeSet;

public class MainActivity extends AppCompatActivity {

    MediaPlayer mainTheme;
    ActivityResultLauncher<Intent> signUpActivityResultLauncher;
    ActivityResultLauncher<Intent> homeActivityResultLauncher;

    User user;

    TypedArray profilePicsIds;


    // Widgets

    ImageView profilePic;
    TextView accountUsername;
    TextView completion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Widgets
        profilePic = findViewById(R.id.imageView_profilePic);
        accountUsername = findViewById(R.id.textView_accountUsername);
        completion = findViewById(R.id.textView_completion);

        //TODO: check si sauvegarde existante ou non
        if (user == null) {
            displayInfos();

            signUpActivityResultLauncher = registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    this::handleActivityResult
            );
        }

        homeActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                this::handleActivityResult
        );

        ImageButton btnPlayOrCreate = findViewById(R.id.imageButton_playOrCreate);
        btnPlayOrCreate.setOnClickListener(this::playOrSignUp);

        ImageButton btnDelete = findViewById(R.id.imageButton_deleteAccount);
        btnDelete.setOnClickListener(this::deleteAccount);

        ConstraintLayout constraintLayout = findViewById(R.id.mainLayout);
        AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(10);
        animationDrawable.setExitFadeDuration(5000);
        animationDrawable.start();

        playMusic();
    }

    private void handleActivityResult(ActivityResult result) {
        Log.i("Returned value", "received result but not ok.");
        if (result.getResultCode() == Activity.RESULT_OK) {
            Intent resultDatas = result.getData();
            Log.i("Returned value", "received result ok.");
            if (resultDatas != null) {
                user = resultDatas.getParcelableExtra("the_user");
                Log.i("Returned value", "received user.");
                displayInfos();
            }
        }
    }

    private void deleteAccount(View view) {
        if (user != null) {

            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle(R.string.ask_to_delete_title);
            alert.setMessage(R.string.ask_to_delete);
            alert.setPositiveButton(android.R.string.yes, (dialog, which) -> {
                user = null;
                displayInfos();
            });

            alert.setNegativeButton(android.R.string.no, (dialog, which) -> dialog.cancel());

            alert.show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        playMusic();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopMusic();
    }

    public void playOrSignUp(android.view.View v) {
        Intent intent;
        if (user == null) {
            intent = new Intent(this, SignUpActivity.class);
            signUpActivityResultLauncher.launch(intent);
        }
        else {
            intent = new Intent(this, HomeActivity.class);
            intent.putExtra("the_user",user);
            homeActivityResultLauncher.launch(intent);
        }
    }

    public void adminSignUp(View v) {
        user = new User();
        user.setUsername("Admin");
        user.setGender(false);
        user.setProfilePicId(5);
        SortedSet<Integer> cards = new TreeSet<>();
        cards.add(R.drawable.arcaninebaseset23);
        cards.add(R.drawable.bulbasaurbaseset44);
        cards.add(R.drawable.dugtriobaseset19);
        cards.add(R.drawable.diglettbaseset47);
        cards.add(R.drawable.kadabrabaseset32);
        cards.add(R.drawable.machopbaseset52);
        user.addNewCards(cards);
        displayInfos();
        playOrSignUp(v);
    }

    private void playMusic() {
        if (mainTheme == null)
            mainTheme = MediaPlayer.create(this, R.raw.title_theme);
        mainTheme.start();
    }

    private void stopMusic() {
        mainTheme.release();
        mainTheme = null;
    }

    public void seeRules(View v) {
        Intent intent = new Intent(this, RulesActivity.class);
        startActivity(intent);
    }
    public void leaveApp(android.view.View v) {
        finish();
        System.exit(0);
    }

    private void displayInfos() {
        if (user != null) {
            profilePic.setVisibility(View.VISIBLE);
            profilePicsIds = getResources().obtainTypedArray(R.array.profils_pics_ids);
            profilePic.setImageResource(
                    profilePicsIds.getResourceId(user.getProfilePicId(), 0)
            );
            profilePicsIds.recycle();

            accountUsername.setText(user.getUsername());
            findViewById(R.id.textView_completion).setVisibility(View.VISIBLE);

            String toDisplay = user.getNbCards() + "/69";
            completion.setText(toDisplay);
        }
        else {
            profilePic.setImageDrawable(null);
            profilePic.setVisibility(View.GONE);
            accountUsername.setText(R.string.need_to_create_a_account);
            completion.setText("");
            completion.setVisibility(View.GONE);
        }
    }
}