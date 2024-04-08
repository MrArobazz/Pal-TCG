package com.example.paltcg;

import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.paltcg.dataclasses.Pokemon;
import com.example.paltcg.dataclasses.User;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class Arena_1_Activity extends AppCompatActivity {
    Random random;

    ConstraintLayout arena;
    ImageView botActiveCard, playerActiveCard;
    WebView botPokemonSprite , playerPokemonSprite;
    ProgressBar progressBar_bot,progressBar_player;
    TextView player_hp, bot_hp;


    Spinner pokemonsChoice;
    ArrayAdapter<String> adapter;

    ListView attacksChoice;

    User player;
    ArrayList<Pokemon> playerPokemons = new ArrayList<>();

    ArrayList<Pokemon> botPokemons = new ArrayList<>();


    TypedArray pokemonCardsIds;
    ArrayList<Integer> player_pokemonCards = new ArrayList<>();
    ArrayList<Integer> bot_pokemonCards = new ArrayList<>();

    boolean pokemon_alive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.arena1);
        findViewById(R.id.linearLayout_player_choice).setVisibility(View.GONE);
        random = new Random();

        //Get the compoents
        arena = findViewById(R.id.constraint_layout_arena);

        progressBar_bot = findViewById(R.id.progressBar_pv_bot_arene1);
        progressBar_player = findViewById(R.id.progressBar_pv_player_arene1);

        botActiveCard = findViewById(R.id.imageView_carte_active_bot);

        playerPokemonSprite = findViewById(R.id.webView_sprite_player_arena1);
        botPokemonSprite = findViewById(R.id.webView_sprite_bot_arena1);

        attacksChoice = findViewById(R.id.listView_attackChoice);

        player_hp = findViewById(R.id.textView_pv_player_arena1);
        bot_hp = findViewById(R.id.textView_pv_bot);

        playerActiveCard = findViewById(R.id.imageView_carte_active_player);
        botActiveCard = findViewById(R.id.imageView_carte_active_bot);

        // Get the player and his cards
        Intent intent = getIntent();
        player = intent.getParcelableExtra("the_user");
        int background = intent.getIntExtra("background",-1);
        if (background != -1) {
            arena.setBackgroundResource(background);
        }

        // Find pokemons of the player cards
        pokemonCardsIds = getResources().obtainTypedArray(R.array.pokemon_cards_ids);
        String[] pokemonNames = getResources().getStringArray(R.array.pokemon_names);
        for (int cardid : player.getDeckCardsIds()) {
            for (int i = 0 ; i < pokemonCardsIds.length(); i++) {
                if (player.getCardId(cardid) == pokemonCardsIds.getResourceId(i,-1)) {
                    Pokemon pokemon = new Pokemon(pokemonNames[i],i+1);
                    player_pokemonCards.add(player.getCardId(cardid));
                    playerPokemons.add(pokemon);
                }
            }
        }

        // Generate the deck of the bot and the pokemons
        ArrayList<Integer> randomCardsIds = new ArrayList<>();
        while (randomCardsIds.size() != 5) {
            int randomId = random.nextInt(pokemonCardsIds.length());
            Log.i("TAG", "onCreate: " + randomId);
            Integer card = pokemonCardsIds.getResourceId(randomId, 0);
            if (!randomCardsIds.contains(card)) {
                randomCardsIds.add(card);
                Log.i("TAG", "onCreate: " + card);
                bot_pokemonCards.add(card);
                Pokemon pokemon = new Pokemon(pokemonNames[randomId], randomId+1);
                botPokemons.add(pokemon);
            }
        }
        pokemonCardsIds.recycle();

        // Add the pokemons to the choice list
        ArrayList<String> playerSpinnerArray = new ArrayList<String>();
        playerSpinnerArray.add(getResources().getString(R.string.choose_a_pokemon));
        // WAIT DEGUEULASSE
        for (Pokemon pokemon : playerPokemons) {
            while (pokemon.isNotReady()) {}
            playerSpinnerArray.add(pokemon.getName());
        }

        adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, playerSpinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pokemonsChoice = (Spinner) findViewById(R.id.spinner_poke_choice);
        pokemonsChoice.setAdapter(adapter);
        for (Pokemon pokemon : botPokemons) {
            while (pokemon.isNotReady()) {}
        }
        // FIN WAIT DEGUEULASSE

        pokemonsChoice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.i("TAG", "onItemSelected: test");
                if (adapter.getCount() != 5 && position != 0) {
                    Log.i("TAG", "onItemSelected: test2");
                    adapter.remove(getResources().getString(R.string.choose_a_pokemon));
                    pokemonsChoice.setSelection(position-1);
                    beginBattleWith(position-1);
                    pokemonsChoice.setVisibility(View.GONE);
                    pokemon_alive = true;
                }
                else if (adapter.getCount() <= 5) {
                    Log.i("TAG", "onItemSelected: test2");
                    if (!pokemon_alive) {
                        Log.i("TAG", "onItemSelected: pokemon dead");
                        choosePokemon(position);
                        pokemonsChoice.setVisibility(View.GONE);
                        pokemon_alive = true;
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    void beginBattleWith(int position) {
        Pokemon player_pokemon = playerPokemons.get(position);
        Pokemon bot_pokemon = botPokemons.get(0);

        for (Pokemon bot : botPokemons) {
            Log.i("TAG", "beginBattleWith: " + bot.getName());
        }

        progressBar_player.setMax(player_pokemon.getPv());
        progressBar_bot.setMax(bot_pokemon.getPv());

        progressBar_player.setProgress(player_pokemon.getPv());
        progressBar_bot.setProgress(bot_pokemon.getPv());

        String pv_display = player_pokemon.getPv() + "/" + player_pokemon.getPv();
        player_hp.setText(pv_display);

        pv_display = bot_pokemon.getPv() + "/" + bot_pokemon.getPv();
        bot_hp.setText(pv_display);

        playerPokemonSprite.loadUrl(
                "https://projectpokemon.org/images/sprites-models/normal-back/"
                        + player_pokemon.getName().toLowerCase() + ".gif");
        botPokemonSprite.loadUrl("https://projectpokemon.org/images/normal-sprite/" + bot_pokemon.getName().toLowerCase() + ".gif");

        playerActiveCard.setImageResource(player_pokemonCards.get(position));
        botActiveCard.setImageResource(bot_pokemonCards.get(0));

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1, player_pokemon.getAttackNames());

        attacksChoice.setAdapter(adapter);
        attacksChoice.setVisibility(View.GONE);
        findViewById(R.id.linearLayout_player_choice).setVisibility(View.VISIBLE);
    }

    public void displayAttackChoice(View v) {
        Log.i("TAG", "displayAttackChoice: pokemon gone attack visible");
        pokemonsChoice.setVisibility(View.GONE);
        attacksChoice.setVisibility(View.VISIBLE);
    }

    public void displayPokemonsChoice(View v) {
        Log.i("TAG", "displayPokemonsChoice: attack gone pokemon visible");
        attacksChoice.setVisibility(View.GONE);
        pokemonsChoice.setVisibility(View.VISIBLE);
    }

    public void run_away(View v) {
        Log.i("TAG", "run_away: attack gone pokemon gone");
        attacksChoice.setVisibility(View.GONE);
        pokemonsChoice.setVisibility(View.GONE);
    }

    /*public void goBilan() {
        boolean to_send = false;
        Intent intent = new Intent(this, End_Fight_Activity.class);
        if (pv_player == 0) {
            to_send = true;
        }
        intent.putExtra("result", to_send);
        startActivity(intent);
    }*/
    void choosePokemon(int position) {
    }

}