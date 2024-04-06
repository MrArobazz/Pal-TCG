package com.example.paltcg;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.paltcg.dataclasses.User;

public class DecksActivity extends AppCompatActivity {

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decks);

        Intent intent = getIntent();
        user = intent.getParcelableExtra("the_user");

        RecyclerView recyclerView = findViewById(R.id.recyclerView_cards);

        PC_RecyclerViewAdapter adapter = new PC_RecyclerViewAdapter(this, user);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent returnIntent = new Intent();
                for (int id : user.getDeckCardsIds())
                    Log.i("DecksActivity", "activeCard : " + id);
                returnIntent.putExtra("the_user",user);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });
    }
}