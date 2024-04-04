package com.example.paltcg;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class SignUpActivity extends AppCompatActivity {

    ImageButton profilePic;

    RadioGroup genders;

    EditText usernameEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        ConstraintLayout constraintLayout = findViewById(R.id.mainLayout);
        AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(10);
        animationDrawable.setExitFadeDuration(5000);
        animationDrawable.start();

        genders = findViewById(R.id.radioGroup_gender);
        usernameEditText = findViewById(R.id.editText_username);
    }

    public void goSeeDeckView(android.view.View v) {
        Log.i("TAG", "goSeeDeckView: begin");
        Intent intent = new Intent(this, DecksActivity.class);
        startActivity(intent);
        Log.i("TAG", "goSeeDeckView: end");
    }

    public void chooseProfilePic(android.view.View v) {
        if (profilePic != null) {
            profilePic.setBackground(null);
        }
        profilePic = findViewById(v.getId());
        profilePic.setBackgroundColor(Color.BLACK);
    }

    public void signUp(android.view.View v) {
        if (usernameEditText.getText().toString().isEmpty())
            Toast.makeText(this, "Please enter a username", Toast.LENGTH_SHORT).show();
        else {
            if (genders.getCheckedRadioButtonId() == -1)
                Toast.makeText(this, "Please select gender", Toast.LENGTH_SHORT).show();
            else {
                if (profilePic == null)
                    Toast.makeText(this, "Please select a profile picture", Toast.LENGTH_SHORT).show();
                else {
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("profile_username", usernameEditText.getText().toString());
                    if (genders.getCheckedRadioButtonId() == R.id.radioButton_genderMale)
                        returnIntent.putExtra("profile_gender", 0);
                    else
                        returnIntent.putExtra("profile_gender", 1);
                    returnIntent.putExtra("profile_pic_id", Integer.valueOf(profilePic.getTag().toString()));
                    Log.i("TAG", "signUp: end");
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                }
            }
        }

    }
}