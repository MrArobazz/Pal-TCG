package com.example.paltcg;

import android.content.Intent;
import android.os.Bundle;

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

        PC_RecyclerViewAdapter adapter = new PC_RecyclerViewAdapter(this,user.getCardsIds());

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}