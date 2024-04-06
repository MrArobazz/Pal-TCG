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

    User user;

    TypedArray profilePicsIds;


    // Widgets

    ImageView profilePic;
    TextView accountUsername;
    TextView completion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);

        // Widgets
        profilePic = findViewById(R.id.imageView_profilePic);
        accountUsername = findViewById(R.id.textView_accountUsername);
        completion = findViewById(R.id.textView_completion);

        //TODO: check si sauvegarde existante ou non
        if (user == null) {
            profilePic.setVisibility(View.GONE);
            accountUsername.setText(R.string.need_to_create_a_account);
            completion.setVisibility(View.GONE);

            signUpActivityResultLauncher = registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        Log.i("TAG", "onActivityResult: received");
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Log.i("TAG", "onActivityResult: received result ok");
                            Intent resultDatas = result.getData();
                            if (resultDatas != null) {
                                Log.i("TAG", "onActivityResult: received something");
                                user = resultDatas.getParcelableExtra("the_user");
                                assert user != null;
                                Log.i("TAG", "onActivityResult: get those infos : " + user.getUsername() + " " + user.getProfilePicId() + " " + user.getGender());

                                ImageView profilePic = findViewById(R.id.imageView_profilePic);
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
                        }
                    }
            );
        }

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

    private void deleteAccount(View view) {
        if (user != null) {

            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle(R.string.ask_to_delete_title);
            alert.setMessage(R.string.ask_to_delete);
            alert.setPositiveButton(android.R.string.yes, (dialog, which) -> {
                user = null;
                profilePic.setImageDrawable(null);
                profilePic.setVisibility(View.GONE);
                accountUsername.setText(R.string.need_to_create_a_account);
                completion.setText("");
                completion.setVisibility(View.GONE);
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
            startActivity(intent);
        }
    }

    public void adminSignUp(View v) {
        user = new User();
        user.setUsername("Admin");
        user.setGender(false);
        user.setProfilePicId(R.drawable.pic5);
        SortedSet<Integer> cards = new TreeSet<>();
        cards.add(R.drawable.arcaninebaseset23);
        cards.add(R.drawable.bulbasaurbaseset44);
        cards.add(R.drawable.dugtriobaseset19);
        cards.add(R.drawable.diglettbaseset47);
        cards.add(R.drawable.kadabrabaseset32);
        cards.add(R.drawable.machopbaseset52);
        user.addNewCards(cards);
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra("the_user",user);
        startActivity(intent);
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
}