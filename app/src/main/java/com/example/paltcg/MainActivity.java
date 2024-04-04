package com.example.paltcg;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class MainActivity extends AppCompatActivity {

    MediaPlayer mainTheme;
    ActivityResultLauncher<Intent> signUpActivityResultLauncher;
    Boolean accountExists = false;

    Integer profilePicId;
    String profileUsername;
    Integer gender; //0 for male, 1 for female


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);

        //TODO: check si sauvegarde existante ou non
        if (!accountExists) {
            findViewById(R.id.imageView_profilePic).setVisibility(View.GONE);
            TextView text = findViewById(R.id.textView_accountUsername);
            text.setText(R.string.need_to_create_a_account);
            findViewById(R.id.textView_completion).setVisibility(View.GONE);

            signUpActivityResultLauncher = registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        Log.i("TAG", "onActivityResult: received");
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Log.i("TAG", "onActivityResult: received result ok");
                            Intent resultDatas = result.getData();
                            if (resultDatas != null) {

                                Log.i("TAG", "onActivityResult: received something");
                                profilePicId = resultDatas.getIntExtra("profile_pic_id", 0);
                                profileUsername = resultDatas.getStringExtra("profile_username");
                                gender = resultDatas.getIntExtra("profile_gender", 0);
                                Log.i("TAG", "onActivityResult: get those infos : " + profilePicId + " " + profileUsername + " " + gender);

                                ImageView profilePic = findViewById(R.id.imageView_profilePic);
                                profilePic.setVisibility(View.VISIBLE);
                                profilePic.setImageResource(getResources()
                                        .obtainTypedArray(R.array.profils_pics_ids)
                                        .getResourceId(profilePicId-1,0));

                                TextView text1 = findViewById(R.id.textView_accountUsername);
                                text1.setText(profileUsername);
                                findViewById(R.id.textView_completion).setVisibility(View.VISIBLE);
                            }
                        }
                    }
            );
        }



        ConstraintLayout constraintLayout = findViewById(R.id.mainLayout);
        AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(10);
        animationDrawable.setExitFadeDuration(5000);
        animationDrawable.start();

        playMusic();
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

    public void goSignUpView(android.view.View v) {
        Intent intent = new Intent(this, SignUpActivity.class);
        signUpActivityResultLauncher.launch(intent);
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

    public void leaveApp(android.view.View v) {
        finish();
        System.exit(0);
    }
}