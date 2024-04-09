package com.example.paltcg.dataclasses;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.SortedSet;

public class User implements Parcelable {
    String username;
    Boolean gender = false; // false : male, true : female
    Integer profilePicId = 0;

    ArrayList<Integer> cardsIds = new ArrayList<>();

    ArrayList<Integer> deckCardsIds = new ArrayList<>();

    //Champs pour les stats
    Integer  loosed_poke = 0  , won_poke = 0 , won_battles = 0 , loosed_battles = 0;
    Double evaluationMoy = 0.0 ;

    public User() {}

    protected User(Parcel in) {
        username = in.readString();
        byte tmpGender = in.readByte();
        gender = tmpGender != 0;
        profilePicId = in.readInt();
        cardsIds = (ArrayList<Integer>) in.readSerializable();
        deckCardsIds = (ArrayList<Integer>) in.readSerializable();
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeByte((byte) (gender ? 1 : 0));
        dest.writeInt(profilePicId);
        dest.writeSerializable(cardsIds);
        dest.writeSerializable(deckCardsIds);
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

    public void setGender(Boolean gender) {
        this.gender = gender;
    }

    public void setProfilePicId(Integer profilePicId) {
        this.profilePicId = profilePicId;
    }

    public void addNewCards(SortedSet<Integer> newCards) {
        for (Integer cardId : newCards)
            if (!cardsIds.contains(cardId))
                cardsIds.add(cardId);
    }

    public boolean activateCard(int cardPosition) {
        if (getNbActiveCards() < 5) {
            deckCardsIds.add(cardPosition);
            return true;
        }
        return false;
    }

    public void removeDeckCard(int position) {
        Log.i("TAG", "removeDeckCard: remove " + position);
        deckCardsIds.remove(Integer.valueOf(position));
    }

    public void removeCards(ArrayList<Integer> cardsToRemove) {
        cardsIds.removeAll(cardsToRemove);
        deckCardsIds = new ArrayList<>();
    }

    public void removeActiveCards() {
        int nb_removed = 0;
        for (int pos : deckCardsIds) {
            Log.i("TAG", "removeActiveCards: " + pos);
            cardsIds.remove(pos-nb_removed);
            nb_removed ++;
        }
        deckCardsIds = new ArrayList<>();
    }

    public void addNewCards(ArrayList<Integer> newCards) {
        for (Integer newCard : newCards) {
            if (!cardsIds.contains(newCard))
                cardsIds.add(newCard);
        }
    }

    public String getUsername() {
        return username;
    }

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
        double tmp =  evaluationMoy * (won_battles + loosed_battles - 1);
        evaluationMoy = (double)(tmp + evaluation) / (double)(won_battles + loosed_battles);
    }

    public void addWonBattle(){
        won_battles++;
    }

    public void addLoosedBattle(){
        loosed_battles++;
    }

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

    public int getWonPoke(){
        return won_poke;
    }

    public int getLoosedPoke(){
        return loosed_poke;
    }

    public int getNbBattles(){
        return won_battles + loosed_battles;
    }

    public double getEvaluation(){
        return  evaluationMoy;
    }
}
