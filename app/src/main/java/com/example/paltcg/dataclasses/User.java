package com.example.paltcg.dataclasses;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.SortedSet;

public class User implements Parcelable {
    String username;
    Boolean gender = false; // false : male, true : female
    Integer profilePicId = 0;

    ArrayList<Integer> cardsIds = new ArrayList<>();

    ArrayList<Integer> deckCardsIds = new ArrayList<>();

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

    public String getUsername() {
        return username;
    }

    public Boolean getGender() {
        return gender;
    }

    public Integer getProfilePicId() {
        return profilePicId;
    }

    public int getNbCards() {
        return cardsIds.size();
    }

    public ArrayList<Integer> getCardsIds() {
        return cardsIds;
    }
}
