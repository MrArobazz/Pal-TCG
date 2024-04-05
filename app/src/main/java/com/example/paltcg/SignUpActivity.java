package com.example.paltcg;

import android.app.Activity;
import android.content.Intent;
import android.content.res.TypedArray;
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

import com.example.paltcg.dataclasses.User;

import java.util.Random;
import java.util.SortedSet;
import java.util.TreeSet;

public class SignUpActivity extends AppCompatActivity {

    User user = new User();
    ImageButton profilePic;

    RadioGroup genders;

    EditText usernameEditText;

    TypedArray pokemonCardsIds;
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

    public void generateDeck(android.view.View v) {
        Log.i("TAG", "generateDeck: begin");

        Random random = new Random();
        user = new User();

        SortedSet<Integer> randomCardsIds = new TreeSet<>();
        pokemonCardsIds = getResources().obtainTypedArray(R.array.pokemon_cards_ids);

        while (randomCardsIds.size() != 15) {
            int randomId = random.nextInt(pokemonCardsIds.length());
            randomCardsIds.add(pokemonCardsIds.getResourceId(randomId, 0));
        }
        pokemonCardsIds.recycle();

        user.addNewCards(randomCardsIds);

        Intent intent = new Intent(this, DecksActivity.class);
        intent.putExtra("the_user",user);
        startActivity(intent);
        Log.i("TAG", "generateDeck: end");
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
                if (user.getNbCards() == 0)
                    Toast.makeText(this, "You need to generate a deck", Toast.LENGTH_SHORT).show();
                else {
                    user.setUsername(usernameEditText.getText().toString());
                    user.setProfilePicId(Integer.parseInt(profilePic.getTag().toString())-1);
                    user.setGender(genders.getCheckedRadioButtonId() != R.id.radioButton_genderMale);

                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("the_user",user);
                    Log.i("TAG", "signUp: end");
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                }
            }
        }

    }
}