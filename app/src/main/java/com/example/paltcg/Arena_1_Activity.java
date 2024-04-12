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
    ArrayAdapter<String> adapterForPokemonsChoice;

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
    private int nb_attempts = 0;
    private final String TAG = "COMBAT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.arena1);


        findViewById(R.id.linearLayout_player_choice).setVisibility(View.GONE);
        random = new Random();

        //Get the compoents
        getComponents();


        // Get the player and his cards
        Intent intent = getIntent();
        player = intent.getParcelableExtra("the_user");
        int background = intent.getIntExtra("background", -1);
        if (background != -1) {
            arena.setBackgroundResource(background);
        }

        // Find pokemons of the player cards
        pokemonCardsIds = getResources().obtainTypedArray(R.array.pokemon_cards_ids);
        generatePlayerTeam();
        generateBotTeam();
        pokemonCardsIds.recycle();

        // Add the pokemons to the choice list
        ArrayList<String> playerSpinnerArray = new ArrayList<>();
        playerSpinnerArray.add(getResources().getString(R.string.choose_a_pokemon));
        for (Pokemon pokemon : playerPokemons) {
            playerSpinnerArray.add(pokemon.getName());
        }

        // We add the pokemons (names) to the spinner
        adapterForPokemonsChoice = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, playerSpinnerArray);

        adapterForPokemonsChoice.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pokemonsChoice.setAdapter(adapterForPokemonsChoice);
        pokemonsChoice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                handleItemSelected(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });


        // We don't let the user go back because it will make escape useless
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() { showToast(Arena_1_Activity.this, getString(R.string.try_to_press_back_button),2500);}
        });

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                this::handleActivityResult
        );

        attacksChoice.setOnItemClickListener(attack_action);

        Log.i(TAG, "onCreate: Début combat");
    }

    private void getComponents() {
        arena = findViewById(R.id.constraint_layout_arena);

        progressBar_bot = findViewById(R.id.progressBar_pv_bot_arene1);
        progressBar_player = findViewById(R.id.progressBar_pv_player_arene1);

        playerPokemonSprite = findViewById(R.id.webView_sprite_player_arena1);
        botPokemonSprite = findViewById(R.id.webView_sprite_bot_arena1);
        playerPokemonSprite.getSettings().setUseWideViewPort(true);
        playerPokemonSprite.getSettings().setLoadWithOverviewMode(true);
        botPokemonSprite.getSettings().setUseWideViewPort(true);
        botPokemonSprite.getSettings().setLoadWithOverviewMode(true);

        attacksChoice = findViewById(R.id.listView_attackChoice);

        player_hp = findViewById(R.id.textView_pv_player_arena1);
        bot_hp = findViewById(R.id.textView_pv_bot);

        playerActiveCard = findViewById(R.id.imageView_carte_active_player);
        botActiveCard = findViewById(R.id.imageView_carte_active_bot);

        pokemonsChoice = findViewById(R.id.spinner_poke_choice);
    }

    private void generatePlayerTeam() {
        // we will get the drawables and names of the pokemon in the user hand
        String[] pokemonNames = getResources().getStringArray(R.array.pokemon_names);
        for (int cardid : player.getDeckCardsIds()) {
            // for each card in the hand, we try to get the drawable and the name
            for (int i = 0; i < pokemonCardsIds.length(); i++) {
                if (player.getCardId(cardid) == pokemonCardsIds.getResourceId(i, -1)) {
                    Pokemon pokemon = new Pokemon(pokemonNames[i], i + 1);
                    // as soon as we have a pokemon, we start to fetch the datas to save time
                    pokemon.fetchDatas();

                    // we add the pokemon to the list
                    player_pokemonCards.add(player.getCardId(cardid));
                    Log.i(TAG, "onCreate: " + pokemon.getName() + " " + player.getCardId(cardid));
                    playerPokemons.add(pokemon);
                    playerPokemons_original.add(pokemon);
                }
            }
        }
    }

    private void generateBotTeam() {
        // we do the same with the team of the bot but with random ids for the hand
        ArrayList<Integer> randomCardsIds = new ArrayList<>();
        String[] pokemonNames = getResources().getStringArray(R.array.pokemon_names);
        // we still need to have 5 differents cards
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
    }

    private void handleItemSelected(int position) {
        if (position == 0) {
            return; // Handle default selection or no action
        }
        Log.i("TAG", "onItemSelected: test with " + position);
        handleBattleOrChange(position - 1);
    }
    private void handleBattleOrChange(int selectedIndex) {
        // we will have differents behaviours depending of the context of the battle
        // if the pokemon is dead
        if (change_by_ko) {
            // we will replace it by another
            replacePlayerPokemonWith(selectedIndex);
            pokemonsChoice.setVisibility(View.GONE);
            pokemonsChoice.setSelection(0);
        } else {
            if (begin) {
                // if it is the beginning
                beginBattleWith(selectedIndex);
            } else {
                // if the user wants to change the pokemon
                changePokemonWith(selectedIndex);
            }
        }
    }

    private void handleActivityResult(ActivityResult result) {
        if (result.getResultCode() == Activity.RESULT_OK) {
            Intent resultDatas = result.getData();
            if (resultDatas != null) {
                player = resultDatas.getParcelableExtra("the_user");
                //Log.i(TAG, "handleActivityResult: " + player.getEvaluation());
                Intent returnIntent = new Intent();
                returnIntent.putExtra("the_user",player);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        }
    }

    void showToast(Context ctx, String message) {
        showToast(ctx,message,1000);
    }
    void showToast(Context ctx, String message, int duration) {
        // i handle toasts to make them faster
        // because i show a lot of toasts to follow the battle
        // and default length are too long (yes 2.5 seconds are long even if she doesn't agree)
        final Toast toast = Toast.makeText(ctx, message,Toast.LENGTH_SHORT);
        toast.show();

        Handler handler = new Handler();
        handler.postDelayed(toast::cancel, duration);
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
        // there are a few names that are not okay so we need to change them
        // for now it is here but we could put that somewhere else
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
    }

    void setUpBotCard() {
        int nextCard = bot_pokemonCards.size() - botPokemons.size();
        botActiveCard.setImageResource(bot_pokemonCards.get(nextCard));
    }

    void setUpPlayerAttacksChoice() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, active_player_pokemon.getAttackNames());

        attacksChoice.setAdapter(adapter);
    }

    void changePlayerDisplay(int position) {
        // we display the new pokemon
        active_player_pokemon = playerPokemons.get(position);

        // we wait if he's not ready
        while (active_player_pokemon.isNotReady()) {}
        // we put infos
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
        progressBar_bot.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
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
            pokemonsChoice.setSelection(0);
            findViewById(R.id.linearLayout_player_choice).setVisibility(View.GONE);

            // Get old name
            String oldPokemonName = active_player_pokemon.getName();

            // setup display with new
            changePlayerDisplay(position);

            // New pokemon name replaces old pokemon name
            String toastTest = active_player_pokemon.getName() + getString(R.string.replace) + oldPokemonName;
            showToast(this, toastTest);

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

    void attackAPokemon(Pokemon attacker, boolean isPlayer, String attkName, Pokemon receiver) {
        int dgts = attacker.getDgtsFromAttackName(attkName);
        attacker.attackPokemon(receiver, dgts);
        ProgressBar healthbar;
        TextView healthText;
        if (isPlayer) {
            healthbar = progressBar_bot;
            healthText = bot_hp;
        }
        else {
            healthbar = progressBar_player;
            healthText = player_hp;
        }
        // to animate the decline in progress bar
        ValueAnimator animator = ValueAnimator.ofInt(healthbar.getProgress(),receiver.getPv());
        animator.setDuration(2000);

        animator.addUpdateListener(animation -> {
            // at each frame (is that like Update function from Unity Hakim ?)
            int progress = (int) animation.getAnimatedValue();
            // we get the new value
            String hpText = progress + "/" + receiver.getMaxPv();
            // we set the new text
            float ratio = (float) progress / (float) receiver.getMaxPv();
            // we calculate the ratio for the color
            int color;
            if (ratio <= 0.2)
                color = Color.RED;
            else if (ratio <= 0.5)
                color = Color.rgb(255,165,0);
            else color = Color.GREEN;

            // we set up everything
            healthbar.setProgressTintList(ColorStateList.valueOf(color));
            healthbar.setProgress(progress);
            healthText.setText(hpText);
        });

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // when animation ends
                // we keep going the game
                // if the receiver is dead
                if (receiver.getPv() == 0) {
                    String toastText = receiver.getName() + getString(R.string.is_dead);
                    showToast(Arena_1_Activity.this, toastText);
                    // if the receiver is the bot
                    if (isPlayer) {
                        nb_attempts = 0;
                        // we remove the pokemon and we will try to replace it
                        botPokemons.remove(receiver); //active_bot_pokemon
                        replaceBotPokemon();
                        // we will let the bot play
                        fin_tour();
                    }
                    // if the receiver is the player
                    else {
                        // we remove the pokemon and add it to the removed ones in case player escapes
                        playerPokemons.remove(receiver); //active_player_pokemon
                        player_pokemonCardsToRemove.add(playerActiveCardResId);
                        // if player is dead we end the battle
                        if (playerPokemons.isEmpty()) {
                            goBilan(0);
                        }
                        else {
                            // we will say to the player to replace his pokemon
                            adapterForPokemonsChoice.remove(active_player_pokemon.getName());
                            change_by_ko = true;
                            pokemonsChoice.setVisibility(View.VISIBLE);
                        }
                    }
                }
                else if (isPlayer) {
                    // normal behaviour
                    // he attacks so we reset nb_attempts used for escape luck
                    nb_attempts = 0;
                    // we will let the bot play
                    fin_tour();
                }
                else {
                    findViewById(R.id.linearLayout_player_choice).setVisibility(View.VISIBLE);
                }
                showToast(Arena_1_Activity.this, getString(R.string.end_of_turn));
            }
        });

        animator.start();
        String toastText = attacker.getName() + getString(R.string.use) + attkName + getString(R.string.inflicts) + dgts;
        Log.i(TAG, "attackAPokemon: " + toastText);
        showToast(this,toastText,1000);
    }
    private final ListView.OnItemClickListener attack_action =
        (parent, view, position, id) -> {
            // Hide menus
            attacksChoice.setVisibility(View.GONE);
            findViewById(R.id.linearLayout_player_choice).setVisibility(View.GONE);

            Log.i(TAG, "choix attaquer ");
            Log.i("TAG", "here.");
            String attkName = (String)parent.getItemAtPosition(position);
            attackAPokemon(active_player_pokemon,true,attkName,active_bot_pokemon);

    };

    private void replaceBotPokemon() {
        // we will always replace with the first pokemon for the bot
        Log.i(TAG, "replaceBotPokemon: changmeent du pokemon du bot");
        if (!botPokemons.isEmpty()) {
            Log.i(TAG, "replaceBotPokemon: il lui reste des pokemons");
            String oldPokemonName = active_bot_pokemon.getName();

            changeBotDisplay();

            String toastTest = active_bot_pokemon.getName() + getString(R.string.replace) + oldPokemonName;
            showToast(this,toastTest);
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
            findViewById(R.id.linearLayout_player_choice).setVisibility(View.GONE);
            // it is the same operation in the game but with speed and not health points
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
            }
            else {
                showToast(this, getString(R.string.escape_failed));
                nb_attempts ++;
                fin_tour();
            }
        });

        alert.setNegativeButton(android.R.string.no, (dialog, which) -> dialog.cancel());

        alert.show();
    }

    private void fin_tour() {

        showToast(this, getString(R.string.end_of_turn));

        if (botPokemons.isEmpty()) {
            goBilan(2); // victoire
        }
        else {
            String attkName = active_bot_pokemon.getBestAttackName();
            attackAPokemon(active_bot_pokemon, false, attkName, active_player_pokemon);
        }
    }

    public void replacePlayerPokemonWith(int position) {
        Log.i(TAG, "replacePlayerPokemonWith: pokemon mort, go le replace");

        change_by_ko = false;

        String oldPokemonName = active_player_pokemon.getName();

        changePlayerDisplay(position);

        String toastTest = active_player_pokemon.getName() + getString(R.string.replace) + oldPokemonName;
        showToast(this,toastTest);

        float ratio = (float) active_player_pokemon.getPv() / (float) active_player_pokemon.getMaxPv();

        int couleur;
        if (ratio <= 0.2)
            couleur = Color.RED;
        else if (ratio <= 0.5)
            couleur = Color.rgb(255,165,0);
        else couleur = Color.GREEN;

        progressBar_player.setProgressTintList(ColorStateList.valueOf(couleur));

        findViewById(R.id.linearLayout_player_choice).setVisibility(View.VISIBLE);
    }

    public void goBilan(int end) {

        Intent intent = new Intent(this,End_Fight_Activity.class);
        intent.putExtra("result",end);
        Log.i(TAG, "goBilan: nb cartes : " + player.getCardsIds().size());
        ArrayList<Integer> oldCards = new ArrayList<>(player.getCardsIds());
        intent.putExtra("old_list",oldCards);

        switch (end) {
            case 0 :
                player.removeActiveCards();
                break;
            case 1 :
                player.removeCards(player_pokemonCardsToRemove);
                break;
            case 2 :
                player.addNewCards(bot_pokemonCards);
                break;
        }

        intent.putExtra("the_user",player);
        activityResultLauncher.launch(intent);
    }

}