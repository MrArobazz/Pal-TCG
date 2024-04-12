package com.example.paltcg.dataclasses;

import android.app.Activity;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.paltcg.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;
import java.util.SortedSet;

public class User implements Parcelable {
    String username;
    Boolean gender = false; // false : male, true : female
    Integer profilePicId = 0;

    ArrayList<Integer> cardsIds = new ArrayList<>();

    ArrayList<Integer> deckCardsIds = new ArrayList<>();

    //Champs pour les stats
    Integer  loosed_poke = 0  , won_poke = 0 , won_battles = 0 , loosed_battles = 0 , flee_battles = 0 , level = 1 , exp = 0;
    Double evaluationMoy = 0.0 ;



    String mail = "";

    public User() {}

    protected User(Parcel in) {
        username = in.readString();
        byte tmpGender = in.readByte();
        gender = tmpGender != 0;
        profilePicId = in.readInt();
        cardsIds = (ArrayList<Integer>) in.readSerializable();
        deckCardsIds = (ArrayList<Integer>) in.readSerializable();
        loosed_poke = in.readInt();
        won_poke = in.readInt();
        won_battles = in.readInt();
        loosed_battles = in.readInt();
        evaluationMoy = in.readDouble();
        flee_battles = in.readInt();
        mail = in.readString();
        level = in.readInt();
        exp = in.readInt();
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeByte((byte) (gender ? 1 : 0));
        dest.writeInt(profilePicId);
        dest.writeSerializable(cardsIds);
        dest.writeSerializable(deckCardsIds);
        dest.writeInt(loosed_poke);
        dest.writeInt(won_poke);
        dest.writeInt(loosed_battles);
        dest.writeInt(won_battles);
        dest.writeDouble(evaluationMoy);
        dest.writeInt(flee_battles);
        dest.writeString(mail);
        dest.writeInt(level);
        dest.writeInt(exp);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public void setUsername(String username) {
        this.username = username;
    }

    public void setMail(String mail) {this.mail = mail;}

    public void setGender(Boolean gender) {
        this.gender = gender;
    }

    public void setLevel(){this.level++;}
    public void setExp(int exp){this.exp += exp;}


    public void setProfilePicId(Integer profilePicId) {
        this.profilePicId = profilePicId;
    }

    public void addNewCards(SortedSet<Integer> newCards) {
        for (Integer cardId : newCards)
            if (!cardsIds.contains(cardId))
                cardsIds.add(cardId);
    }

    public void addNewCards(ArrayList<Integer> newCards) {
        // we check if we don't already have the card
        for (Integer newCard : newCards) {
            if (!cardsIds.contains(newCard))
                // if not we add it
                cardsIds.add(newCard);
        }
    }

    public boolean activateCard(int cardPosition) {
        // to fill our hand
        if (getNbActiveCards() < 5) {
            deckCardsIds.add(cardPosition);
            return true;
        }
        return false;
    }

    public void removeDeckCard(int position) {
        // here we remove a card from his position in the list
        Log.i("TAG", "removeDeckCard: remove " + position);
        deckCardsIds.remove(Integer.valueOf(position));
    }

    public void removeCards(ArrayList<Integer> cardsToRemove) {
        // here we remove all specified cards
        cardsIds.removeAll(cardsToRemove);
        deckCardsIds = new ArrayList<>();
    }

    public void removeActiveCards() {
        // here we remove all cards that were in our hand
        ArrayList<Integer> tmp_cardsIds = new ArrayList<>(cardsIds);

        for (int pos : deckCardsIds) {
            Integer cardid = tmp_cardsIds.get(pos);
            cardsIds.remove(cardid);
        }
        deckCardsIds = new ArrayList<>();
    }

    public String getUsername() {
        return username;
    }
    public boolean getGender(){return gender;}
    public String getMail(){return mail;}
    public int getLevel(){return level;}
    public int getExp(){return exp;}
    public Integer getProfilePicId() {
        return profilePicId;
    }

    public int getNbCards() {
        return cardsIds.size();
    }

    private int getNbActiveCards() { return deckCardsIds.size(); }

    public ArrayList<Integer> getCardsIds() {
        return cardsIds;
    }

    public Integer getCardId(int position) { return cardsIds.get(position);}

    public ArrayList<Integer> getDeckCardsIds() { return deckCardsIds;}

    public void setEvaluation(double evaluation){
        double tmp =  evaluationMoy * (getNbBattles() - 1);
        evaluationMoy = (tmp + evaluation) / (double)(getNbBattles());
    }

    public void addWonBattle(){
        won_battles++;
    }

    public void addLoosedBattle(){
        loosed_battles++;
    }
    public void addFleeBattle(){flee_battles++;}

    public void addLoosedPoke(int nb_losed){
        loosed_poke += nb_losed;
    }

    public void addWonPoke(int nb_won){
        won_poke += nb_won;
    }

    public int getNbWonBattle(){
        return won_battles;
    }

    public int getLoosedBattle(){
        return loosed_battles;
    }
    public int getFleeBattle(){return flee_battles;}

    public int getWonPoke(){
        return won_poke;
    }

    public int getLoosedPoke(){
        return loosed_poke;
    }

    public int getNbBattles(){
        return won_battles + loosed_battles + flee_battles;
    }

    public double getEvaluation(){
        return  evaluationMoy;
    }

    //corps du mail a envoyer
    public String getStats(){
        return ("Pseudo: "+ username +"\n"+
                "Level: "+ level+"\n"+
                "Number of Battles: "+getNbBattles()+"\n"+
                "Number of Loosed Battles: "+loosed_battles+"\n"+
                "Number of Won Battles: "+won_battles+"\n"+
                "Number of Flee: "+flee_battles+"\n"+
                "Number of Won Pokemons: "+won_poke+"\n"+
                "Number of Loosed Pokemons: "+loosed_poke+"\n"+
                "Number of Possessed Pokemons: "+getNbCards()+"\n"+
                "My Evaluation of the Battles: "+evaluationMoy+"\n");
    }

    public void saveUser(Activity activity) {
        // we will save in internal storage
        File folder = activity.getApplicationContext().getFilesDir();
        File fileout = new File(folder, "user.txt");
        try (FileOutputStream fos = new FileOutputStream(fileout)){
            // we use printstream to easier manipulations
            PrintStream ps = new PrintStream(fos);
            // we hard save datas
            ps.println(username);
            ps.println(gender);
            ps.println(profilePicId);
            for (int cardid : cardsIds) {
                ps.println(cardid);
            }
            ps.println(-1);
            for (int pos : deckCardsIds) {
                ps.println(pos);
            }
            ps.println(-1);
            ps.println(loosed_poke);
            ps.println(won_poke);
            ps.println(won_battles);
            ps.println(loosed_battles);
            ps.println(flee_battles);
            ps.println(evaluationMoy);

            ps.close();

            Toast.makeText(activity.getApplicationContext(), activity.getString(R.string.saved_file), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(activity.getApplicationContext(), activity.getString(R.string.save_error), Toast.LENGTH_SHORT).show();
            throw new RuntimeException(e);
        }
    }

    public boolean loadUserIfExists(Activity activity) {
        // we get the save file
        File folder = activity.getApplicationContext().getFilesDir();
        File readFrom = new File(folder, "user.txt");
        // we check if it exists
        if (readFrom.exists()) {
            try (FileInputStream fis = new FileInputStream(readFrom)) {
                // we use scanner to easier manipulations
                Scanner sc = new Scanner(fis);
                sc.useLocale(Locale.US);
                username = sc.nextLine();
                gender = sc.nextBoolean();
                profilePicId = sc.nextInt();
                while (sc.hasNextInt()) {
                    int nextInt = sc.nextInt();
                    if (nextInt != -1)
                        cardsIds.add(nextInt);
                    else break;
                }
                while (sc.hasNextInt()) {
                    int nextInt = sc.nextInt();
                    if (nextInt != -1)
                        deckCardsIds.add(nextInt);
                    else break;
                }
                loosed_poke = sc.nextInt();
                won_poke = sc.nextInt();
                won_battles = sc.nextInt();
                loosed_battles = sc.nextInt();
                flee_battles = sc.nextInt();
                evaluationMoy = sc.nextDouble();

                sc.close();
                Toast.makeText(activity.getApplicationContext(), activity.getString(R.string.loaded_file), Toast.LENGTH_SHORT).show();
                return true;
            } catch (IOException e) {
                Toast.makeText(activity.getApplicationContext(), activity.getString(R.string.load_error), Toast.LENGTH_SHORT).show();
                throw new RuntimeException(e);
            }
        }
        return false;
    }
}







