package com.example.paltcg.dataclasses;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class User implements Parcelable {
    String username;
    Boolean gender; // false : male, true : female
    Integer profilePicId;

    int[] cardsIds;

    int[] deckCardsIds;

    public User() {}

    protected User(Parcel in) {
        username = in.readString();
        byte tmpGender = in.readByte();
        gender = tmpGender != 0;
        profilePicId = in.readInt();
        cardsIds = in.createIntArray();
        deckCardsIds = in.createIntArray();
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeByte((byte) (gender ? 1 : 0));
        dest.writeInt(profilePicId);
        dest.writeIntArray(cardsIds);
        dest.writeIntArray(deckCardsIds);
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

    public String getUsername() {
        return username;
    }

    public Boolean getGender() {
        return gender;
    }

    public Integer getProfilePicId() {
        return profilePicId;
    }
}
