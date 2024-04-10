package com.example.paltcg;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.paltcg.dataclasses.Pokemon;
import com.example.paltcg.dataclasses.User;

import java.util.ArrayList;
import java.util.Random;

public class Arena_1_Activity extends AppCompatActivity {

    ActivityResultLauncher<Intent> activityResultLauncher;
    Random random;

    ConstraintLayout arena;
    ImageView botActiveCard, playerActiveCard;
    Integer playerActiveCardResId;
    WebView botPokemonSprite , playerPokemonSprite;
    ProgressBar progressBar_bot,progressBar_player;
    TextView player_hp, bot_hp;


    Spinner pokemonsChoice;
    ArrayAdapter<String> adapter;

    ListView attacksChoice;

    User player;
    ArrayList<Pokemon> playerPokemons = new ArrayList<>();
    ArrayList<Pokemon> playerPokemons_original = new ArrayList<>();
    ArrayList<Pokemon> botPokemons = new ArrayList<>();
    Pokemon active_player_pokemon, active_bot_pokemon;


    TypedArray pokemonCardsIds;
    ArrayList<Integer> player_pokemonCards = new ArrayList<>();
    ArrayList<Integer> player_pokemonCardsToRemove = new ArrayList<>(); // si le joueur fuit
    ArrayList<Integer> bot_pokemonCards = new ArrayList<>();

    boolean begin = true;
    boolean change_by_ko = false;
    boolean player_turn = true;
    private int nb_attempts = 0;
    private String TAG = "COMBAT";

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        player = savedInstanceState.getParcelable("player");
        playerPokemons = savedInstanceState.getParcelableArrayList("playerPokemons");
        playerPokemons_original = savedInstanceState.getParcelableArrayList("playerPokemons_original");
        botPokemons = savedInstanceState.getParcelableArrayList("botPokemons");
        active_player_pokemon = savedInstanceState.getParcelable("active_player_pokemon");
        active_bot_pokemon = savedInstanceState.getParcelable("active_bot_pokemon");
        player_pokemonCards = savedInstanceState.getIntegerArrayList("player_pokemonCards");
        player_pokemonCardsToRemove = savedInstanceState.getIntegerArrayList("player_pokemonCardsToRemove");
        bot_pokemonCards = savedInstanceState.getIntegerArrayList("bot_pokemonCards");
        begin = savedInstanceState.getBoolean("begin");
        change_by_ko = savedInstanceState.getBoolean("change_by_ko");
        player_turn = savedInstanceState.getBoolean("player_turn");
        nb_attempts = savedInstanceState.getInt("nb_attempts");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        int background = intent.getIntExtra("background", -1);
        if (background != -1) {
            arena.setBackgroundResource(background);
        }

        // Find pokemons of the player cards
        pokemonCardsIds = getResources().obtainTypedArray(R.array.pokemon_cards_ids);
        String[] pokemonNames = getResources().getStringArray(R.array.pokemon_names);
        for (int cardid : player.getDeckCardsIds()) {
            for (int i = 0; i < pokemonCardsIds.length(); i++) {
                if (player.getCardId(cardid) == pokemonCardsIds.getResourceId(i, -1)) {
                    Pokemon pokemon = new Pokemon(pokemonNames[i], i + 1);
                    pokemon.fetchDatas();

                    player_pokemonCards.add(player.getCardId(cardid));
                    Log.i(TAG, "onCreate: " + pokemon.getName() + " " + player.getCardId(cardid));
                    playerPokemons.add(pokemon);
                    playerPokemons_original.add(pokemon);
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
                Pokemon pokemon = new Pokemon(pokemonNames[randomId], randomId + 1);
                pokemon.fetchDatas();
                botPokemons.add(pokemon);
            }
        }
        pokemonCardsIds.recycle();

        // Add the pokemons to the choice list
        ArrayList<String> playerSpinnerArray = new ArrayList<String>();
        playerSpinnerArray.add(getResources().getString(R.string.choose_a_pokemon));
        for (Pokemon pokemon : playerPokemons) {
            playerSpinnerArray.add(pokemon.getName());
        }

        adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, playerSpinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pokemonsChoice = (Spinner) findViewById(R.id.spinner_poke_choice);
        pokemonsChoice.setAdapter(adapter);
        pokemonsChoice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.i("TAG", "onItemSelected: test with " + position);
                if (position != 0) {
                    Log.i(TAG, "onItemSelected: " + begin);
                    Log.i(TAG, "onItemSelected: " + change_by_ko);

                    if (change_by_ko) {
                        replacePlayerPokemonWith(position - 1);
                        pokemonsChoice.setVisibility(View.GONE);
                        pokemonsChoice.setSelection(0);
                        begin = false;
                    }
                    else {
                        if (begin) {
                            beginBattleWith(position - 1);
                        }
                        else
                            changePokemonWith(position-1);
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() { Toast.makeText(Arena_1_Activity.this, getString(R.string.try_to_press_back_button), Toast.LENGTH_SHORT).show();}
        });

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                this::handleActivityResult
        );

        attacksChoice.setOnItemClickListener(attack_action);

        Log.i(TAG, "onCreate: Début combat");
    }

    private void handleActivityResult(ActivityResult result) {
        if (result.getResultCode() == Activity.RESULT_OK) {
            Intent resultDatas = result.getData();
            if (resultDatas != null) {
                player = resultDatas.getParcelableExtra("the_user");
            }
            Intent returnIntent = new Intent();
            returnIntent.putExtra("the_user",player);
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("player",player);
        outState.putParcelableArrayList("playerPokemons",playerPokemons);
        outState.putParcelableArrayList("playerPokemons_original",playerPokemons_original);
        outState.putParcelableArrayList("botPokemons",botPokemons);
        outState.putParcelable("active_player_pokemon",active_player_pokemon);
        outState.putParcelable("active_bot_pokemon",active_bot_pokemon);
        outState.putIntegerArrayList("player_pokemonCards",player_pokemonCards);
        outState.putIntegerArrayList("player_pokemonCardsToRemove",player_pokemonCardsToRemove);
        outState.putIntegerArrayList("bot_pokemonCards",bot_pokemonCards);
        outState.putBoolean("begin",begin);
        outState.putBoolean("change_by_ko",change_by_ko);
        outState.putBoolean("player_turn",player_turn);
        outState.putInt("nb_attempts",nb_attempts);
    }

    void showToast(Context ctx, String message, int duration) {
        final Toast toast = Toast.makeText(ctx, message,Toast.LENGTH_SHORT);
        toast.show();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                toast.cancel();
            }
        }, duration);
    }

    void setUpProgressBar(ProgressBar progressBar, Pokemon pokemon) {
        progressBar.setMax(pokemon.getMaxPv());
        progressBar.setProgress(pokemon.getPv());
    }

    void setUpTextView(TextView healthBar, Pokemon pokemon) {
        String pv_display = pokemon.getPv() + "/" + pokemon.getMaxPv();
        healthBar.setText(pv_display);
    }

    void setUpSprite(WebView sprite, Pokemon pokemon, boolean isPlayer) {
        String rightName = pokemon.getName();
        if (rightName.equals("Nidoran♂"))
            rightName = rightName.replace("Nidoran♂","Nidoran_m");
        else if (rightName.equals("Farfetch'd"))
            rightName = rightName.replace("Farfetch'd","Farfetchd");

        if (isPlayer)
            sprite.loadUrl("https://projectpokemon.org/images/sprites-models/normal-back/"
                    + rightName.toLowerCase()
                    + ".gif");
        else sprite.loadUrl("https://projectpokemon.org/images/normal-sprite/"
                + rightName.toLowerCase() + ".gif");
    }

    void setUpPlayerCard() {
        int ind = 0;
        for (int i = 0 ; i < playerPokemons_original.size(); i ++) {
            if (playerPokemons_original.get(i).getName().equals(active_player_pokemon.getName())) {
                ind = i;
                break;
            }
        }

        playerActiveCard.setImageResource(player_pokemonCards.get(ind));
        playerActiveCardResId = player_pokemonCards.get(ind);

        //playerActiveCard.setImageResource(player_pokemonCards.get(position));
        //playerActiveCardResId = player_pokemonCards.get(position);
    }

    void setUpBotCard() {
        botActiveCard.setImageResource(bot_pokemonCards.get(0));
    }

    void setUpPlayerAttacksChoice() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1, active_player_pokemon.getAttackNames());

        attacksChoice.setAdapter(adapter);
    }

    void changePlayerDisplay(int position) {
        active_player_pokemon = playerPokemons.get(position);
        while (active_player_pokemon.isNotReady()) {}
        setUpProgressBar(progressBar_player, active_player_pokemon);
        setUpTextView(player_hp,active_player_pokemon);
        setUpSprite(playerPokemonSprite, active_player_pokemon, true);
        setUpPlayerCard();
        setUpPlayerAttacksChoice();
    }

    void changeBotDisplay() {
        // we consider the new pokemon
        active_bot_pokemon = botPokemons.get(0);
        // if we still don't have the datas, we wait (with a ugly, but working, way)
        while (active_bot_pokemon.isNotReady()) {}

        // we set up the widgets
        setUpProgressBar(progressBar_bot, active_bot_pokemon);
        setUpTextView(bot_hp, active_bot_pokemon);
        setUpSprite(botPokemonSprite, active_bot_pokemon, false);
        setUpBotCard();
    }

    void beginBattleWith(int position) {
        // set up player display with first player pokemon
        changePlayerDisplay(position);
        // set up bot display with first bot pokemon
        changeBotDisplay();

        // Show only main menu
        attacksChoice.setVisibility(View.GONE);
        pokemonsChoice.setVisibility(View.GONE);
        findViewById(R.id.linearLayout_player_choice).setVisibility(View.VISIBLE);
        pokemonsChoice.setSelection(0);

        // To avoid to call this method again
        begin = false;
    }

    public void displayPokemonsChoice(View v) {
        Log.i(TAG, "displayPokemonsChoice: attack gone pokemon visible");
        attacksChoice.setVisibility(View.GONE);
        pokemonsChoice.setVisibility(View.VISIBLE);
        //Log.i(TAG, "displayPokemonsChoice: trigger listener ?");
    }

    void changePokemonWith(int position) {
        Log.i(TAG, "changePokemonWith: choix changer pokemon");
        if (active_player_pokemon != playerPokemons.get(position)) {
            // Hide menus
            pokemonsChoice.setVisibility(View.GONE);
            findViewById(R.id.linearLayout_player_choice).setVisibility(View.GONE);

            // Get old name
            String oldPokemonName = active_player_pokemon.getName();

            // setup display with new
            changePlayerDisplay(position);

            // New pokemon name replaces old pokemon name
            String toastTest = active_player_pokemon.getName() + getString(R.string.replace) + oldPokemonName;
            showToast(this, toastTest, 1000);
            //Toast.makeText(this, toastTest, Toast.LENGTH_SHORT).show();

            // End of turn
            fin_tour();
        }
        else Log.i(TAG, "changePokemonWith: choix changer mais rien s'est passe");
    }

    public void displayAttackChoice(View v) {
        Log.i("TAG", "displayAttackChoice: pokemon gone attack visible");
        pokemonsChoice.setVisibility(View.GONE);
        pokemonsChoice.setSelection(0);
        attacksChoice.setVisibility(View.VISIBLE);
    }

    void attackAPokemon(Pokemon attacker, String attkName, Pokemon receiver) {

    }

    private final ListView.OnItemClickListener attack_action =
            (parent, view, position, id) -> {
                // Hide menus
                attacksChoice.setVisibility(View.GONE);
                findViewById(R.id.linearLayout_player_choice).setVisibility(View.GONE);

                Log.i(TAG, "choix attaquer ");
                Log.i("TAG", "here.");
                String attkName = (String)parent.getItemAtPosition(position);
                int dgts = active_player_pokemon.getDgtsFromAttackName(attkName);
                active_player_pokemon.attackPokemon(active_bot_pokemon, dgts);
                ValueAnimator animator = ValueAnimator.ofInt(progressBar_bot.getProgress(),active_bot_pokemon.getPv());
                animator.setDuration(2000);

                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(@NonNull ValueAnimator animation) {
                        int progress = (int) animation.getAnimatedValue();
                        String hpText = progress + "/" + active_bot_pokemon.getMaxPv();
                        float ratio = (float) progress / (float) active_bot_pokemon.getMaxPv();

                        int couleur;
                        if (ratio <= 0.2)
                            couleur = Color.RED;
                        else if (ratio <= 0.5)
                            couleur = Color.rgb(255,165,0);
                        else couleur = Color.GREEN;

                        progressBar_bot.setProgressTintList(ColorStateList.valueOf(couleur));
                        progressBar_bot.setProgress(progress);
                        bot_hp.setText(hpText);
                    }
                });
                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        if (active_bot_pokemon.getPv() == 0) {
                            String toastText = active_bot_pokemon.getName() + getString(R.string.is_dead);
                            Toast.makeText(Arena_1_Activity.this, toastText, Toast.LENGTH_SHORT).show();
                            botPokemons.remove(active_bot_pokemon);
                            replaceBotPokemon();
                        }
                        nb_attempts = 0;
                        fin_tour();
                    }
                });

                animator.start();
                String toastText = active_player_pokemon.getName() + getString(R.string.use) + attkName + getString(R.string.inflicts) + dgts;
                Toast.makeText(this, toastText, Toast.LENGTH_SHORT).show();
            };

    private void replaceBotPokemon() {
        Log.i(TAG, "replaceBotPokemon: changmeent du pokemon du bot");
        if (!botPokemons.isEmpty()) {
            Log.i(TAG, "replaceBotPokemon: il lui reste des pokemons");
            String oldPokemonName = active_bot_pokemon.getName();

            active_bot_pokemon = botPokemons.get(0);
            while (active_bot_pokemon.isNotReady()) {}

            String toastTest = active_bot_pokemon.getName() + getString(R.string.replace) + oldPokemonName;
            Toast.makeText(this, toastTest, Toast.LENGTH_SHORT).show();

            progressBar_bot.setMax(active_bot_pokemon.getMaxPv());
            progressBar_bot.setProgress(active_bot_pokemon.getPv());

            progressBar_bot.setProgressTintList(ColorStateList.valueOf(Color.GREEN));

            String pv_display = active_bot_pokemon.getPv() + "/" + active_bot_pokemon.getMaxPv();
            bot_hp.setText(pv_display);

            String rightName = active_bot_pokemon.getName();
            if (rightName.equals("Nidoran♂"))
                rightName = rightName.replace("Nidoran♂","Nidoran_m");
            else if (rightName.equals("Farfetch'd"))
                rightName = rightName.replace("Farfetch'd","Farfetchd");
            botPokemonSprite.loadUrl(
                    "https://projectpokemon.org/images/normal-sprite/"
                            + rightName.toLowerCase() + ".gif");

            int next_card = bot_pokemonCards.size() - botPokemons.size();
            botActiveCard.setImageResource(bot_pokemonCards.get(next_card));
        }
        else {
            Log.i(TAG, "replaceBotPokemon: il ne lui reste pas des pokemons");
            active_bot_pokemon = null;
            progressBar_bot.setVisibility(View.INVISIBLE);
            bot_hp.setVisibility(View.INVISIBLE);
            botPokemonSprite.destroy();
            botPokemonSprite.setVisibility(View.INVISIBLE);
            botActiveCard.setVisibility(View.INVISIBLE);
        }
    }

    public void run_away(View v) {
        Log.i("TAG", "run_away: attack gone pokemon gone");
        attacksChoice.setVisibility(View.GONE);
        pokemonsChoice.setVisibility(View.GONE);
        pokemonsChoice.setSelection(0);
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.run_away_battle_title);
        alert.setMessage(R.string.run_away_battle);
        alert.setPositiveButton(android.R.string.yes, (dialog, which) -> {
            boolean success = false;
            int very_lucky = (active_player_pokemon.getMaxPv()/4)%256;
            if (very_lucky == 0)
                success = true;
            else {
                int oddsescape = (active_player_pokemon.getMaxPv() * 32 / very_lucky) + 30 * nb_attempts;
                if (oddsescape > 255)
                    success = true;
                else {
                    int luck = random.nextInt(255);
                    if (luck < oddsescape)
                        success = true;
                }
            }
            if (success) {
                goBilan(1); // fuite
                findViewById(R.id.linearLayout_player_choice).setVisibility(View.GONE);
            }
            else {
                Toast.makeText(this, getString(R.string.escape_failed), Toast.LENGTH_SHORT).show();
                nb_attempts ++;
                fin_tour();
            }
        });

        alert.setNegativeButton(android.R.string.no, (dialog, which) -> dialog.cancel());

        alert.show();
    }

    private void fin_tour() {
        Log.i(TAG, "fin_tour: du joueur");
        // Fin du tour du joueur, go pour le tour du bot
        attacksChoice.setVisibility(View.GONE);
        Log.i(TAG, "fin_tour: virer visibilite du choix");
        pokemonsChoice.setVisibility(View.GONE);
        pokemonsChoice.setSelection(0);
        findViewById(R.id.linearLayout_player_choice).setVisibility(View.GONE);

        Toast.makeText(this, getString(R.string.end_of_turn), Toast.LENGTH_SHORT).show();

        if (botPokemons.isEmpty()) {
            goBilan(2); // victoire
        }
        else {
            String attkName = active_bot_pokemon.getBestAttackName();
            Log.i("TAG", "fin_tour: " + attkName);
            int dgts = active_bot_pokemon.getDgtsFromAttackName(attkName);
            active_bot_pokemon.attackPokemon(active_player_pokemon, dgts);
            ValueAnimator animator = ValueAnimator.ofInt(progressBar_player.getProgress(),active_player_pokemon.getPv());
            animator.setDuration(2000);

            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(@NonNull ValueAnimator animation) {
                    int progress = (int) animation.getAnimatedValue();
                    String hpText = progress + "/" + active_player_pokemon.getMaxPv();
                    float ratio = (float) progress / (float) active_player_pokemon.getMaxPv();

                    int couleur;
                    if (ratio <= 0.2)
                        couleur = Color.RED;
                    else if (ratio <= 0.5)
                        couleur = Color.rgb(255,165,0);
                    else couleur = Color.GREEN;

                    progressBar_player.setProgressTintList(ColorStateList.valueOf(couleur));
                    progressBar_player.setProgress(progress);
                    player_hp.setText(hpText);
                }
            });

            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    Log.i(TAG, "fin_tour: fin attaque");
                    if (active_player_pokemon.getPv() == 0) {
                        Log.i(TAG, "fin_tour: joueur mort");
                        playerPokemons.remove(active_player_pokemon);
                        player_pokemonCardsToRemove.add(playerActiveCardResId);
                        String toastText = active_player_pokemon.getName() + getString(R.string.is_dead);
                        Toast.makeText(Arena_1_Activity.this, toastText, Toast.LENGTH_SHORT).show();
                        if (playerPokemons.isEmpty()) {
                            Log.i(TAG, "fin_tour: joueur a plus de pokemons");
                            goBilan(0); //defaite
                        }
                        else {
                            Log.i(TAG, "fin_tour: le joueur a encore des pokemons");
                            adapter.remove(active_player_pokemon.getName());
                            Log.i(TAG, "fin_tour: on retire le pokemon ko du choix");
                            change_by_ko = true;

                            pokemonsChoice.setVisibility(View.VISIBLE);
                            Log.i(TAG, "fin_tour: on affiche la liste de choix");
                        }
                    }
                    else findViewById(R.id.linearLayout_player_choice).setVisibility(View.VISIBLE);
                    Toast.makeText(Arena_1_Activity.this, getString(R.string.end_of_turn), Toast.LENGTH_SHORT).show();
                }
            });

            animator.start();
            String toastText = active_bot_pokemon.getName() + getString(R.string.use) + attkName + getString(R.string.inflicts) + dgts;
            Toast.makeText(this, toastText, Toast.LENGTH_SHORT).show();
        }
    }

    public void replacePlayerPokemonWith(int position) {
        Log.i(TAG, "replacePlayerPokemonWith: pokemon mort, go le replace");
        change_by_ko = false;
        findViewById(R.id.linearLayout_player_choice).setVisibility(View.VISIBLE);

        String oldPokemonName = active_player_pokemon.getName();

        active_player_pokemon = playerPokemons.get(position);
        active_player_pokemon.fetchDatas();
        while (active_player_pokemon.isNotReady()) {}

        String toastTest = active_player_pokemon.getName() + getString(R.string.replace) + oldPokemonName;
        Toast.makeText(this, toastTest, Toast.LENGTH_SHORT).show();

        progressBar_player.setMax(active_player_pokemon.getMaxPv());
        progressBar_player.setProgress(active_player_pokemon.getPv());

        float ratio = (float) active_player_pokemon.getPv() / (float) active_player_pokemon.getMaxPv();

        int couleur;
        if (ratio <= 0.2)
            couleur = Color.RED;
        else if (ratio <= 0.5)
            couleur = Color.rgb(255,165,0);
        else couleur = Color.GREEN;

        progressBar_player.setProgressTintList(ColorStateList.valueOf(couleur));

        String pv_display = active_player_pokemon.getPv() + "/" + active_player_pokemon.getMaxPv();
        player_hp.setText(pv_display);

        String rightName = active_player_pokemon.getName();
        if (rightName.equals("Nidoran♂"))
            rightName = rightName.replace("Nidoran♂","Nidoran_m");
        else if (rightName.equals("Farfetch'd"))
            rightName = rightName.replace("Farfetch'd","Farfetchd");

        playerPokemonSprite.loadUrl(
                "https://projectpokemon.org/images/sprites-models/normal-back/"
                        + rightName.toLowerCase() + ".gif");

        int ind = 0;
        Log.i(TAG, "replacePlayerPokemonWith: " + playerPokemons_original.size());
        for (int i = 0 ; i < playerPokemons_original.size(); i ++) {
            if (playerPokemons_original.get(i).getName().equals(active_player_pokemon.getName())) {
                Log.i(TAG, "replacePlayerPokemonWith: " +playerPokemons_original.get(i).getName());
                Log.i(TAG, "replacePlayerPokemonWith: " +active_player_pokemon.getName());
                Log.i(TAG, "replacePlayerPokemonWith: " +player_pokemonCards.get(i));
                Log.i(TAG, "replacePlayerPokemonWith: " + i);
                ind = i;
                break;
            }
        }
        Log.i(TAG, "replacePlayerPokemonWith: " + ind);
        playerActiveCard.setImageResource(player_pokemonCards.get(ind));
        playerActiveCardResId = player_pokemonCards.get(ind);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1, active_player_pokemon.getAttackNames());

        attacksChoice.setAdapter(adapter);
    }

    public void goBilan(int end) {

        Intent intent = new Intent(this,End_Fight_Activity.class);
        intent.putExtra("result",end);
        Log.i(TAG, "goBilan: nb cartes : " + player.getCardsIds().size());
        ArrayList<Integer> oldCards = new ArrayList<>(player.getCardsIds());
        intent.putExtra("old_list",oldCards);

        switch (end) {
            case 0 :
                Toast.makeText(this, "defaite", Toast.LENGTH_SHORT).show();
                player.removeActiveCards();
                break;
            case 1 :
                Toast.makeText(this, "fuite", Toast.LENGTH_SHORT).show();
                player.removeCards(player_pokemonCardsToRemove);

                break;
            case 2 :
                Toast.makeText(this, "victoire", Toast.LENGTH_SHORT).show();
                player.addNewCards(bot_pokemonCards);
                break;
        }

        intent.putExtra("the_user",player);
        activityResultLauncher.launch(intent);
    }

}