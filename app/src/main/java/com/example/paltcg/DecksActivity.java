package com.example.paltcg;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DecksActivity extends AppCompatActivity {

    ArrayList<PokemonCard> pokemonCards = new ArrayList<>();

    TypedArray pokemonCardsImages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("TAG", "onCreate: begin");
        setContentView(R.layout.activity_decks);

        Log.i("TAG", "onCreate: setContentView");

        RecyclerView recyclerView = findViewById(R.id.recyclerView_cards);
        Log.i("TAG", "onCreate: get recyclerview");

        pokemonCardsImages = getResources().obtainTypedArray(R.array.pokemon_cards_ids);
        setUpPokemonCards();
        Log.i("TAG", "onCreate: set up pokemon cards");

        PC_RecyclerViewAdapter adapter = new PC_RecyclerViewAdapter(this,pokemonCards);
        Log.i("TAG", "onCreate: create and get adapter");

        recyclerView.setAdapter(adapter);
        Log.i("TAG", "onCreate: set adapter to recycler view");
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Log.i("TAG", "onCreate: end");
    }

    private void setUpPokemonCards(){
        String[] pokemonNames = getResources().getStringArray(R.array.pokemon_names);

        for (int i = 0; i < pokemonNames.length ; i++) {
            pokemonCards.add(new PokemonCard(pokemonNames[i], pokemonCardsImages.getResourceId(i,0), false));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pokemonCardsImages.recycle();
    }
}