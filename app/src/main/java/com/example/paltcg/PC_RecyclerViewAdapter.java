package com.example.paltcg;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.paltcg.dataclasses.User;

import java.util.ArrayList;

public class PC_RecyclerViewAdapter extends RecyclerView.Adapter<PC_RecyclerViewAdapter.MyViewHolder> {

    Context context;
    User user;
    ArrayList<Integer> cardsIds;

    public PC_RecyclerViewAdapter(Context context, User user) {
        this.context = context;
        this.user = user;
        this.cardsIds = user.getCardsIds();
    }

    @NonNull
    @Override
    public PC_RecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.cards_row, parent, false);

        return new PC_RecyclerViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PC_RecyclerViewAdapter.MyViewHolder holder, int position) {
        // at each new row to display
        Log.i("TAG", "onBindViewHolder: " + position);
        // we will always have a left one, so we set the image ressource
        holder.leftCard.setImageResource(cardsIds.get(position*2));
        // and we see if this cards was activated by the user to check the checkbox or not
        holder.isLeftEnabled.setChecked(user.getDeckCardsIds().contains(Integer.valueOf(position*2)));
        // if the right one exists
        if (position*2+1 < cardsIds.size()) {
            // we put it visible because if we don't, it will hide some cards if we going up again
            holder.rightCard.setVisibility(View.VISIBLE);
            holder.isRightEnabled.setVisibility(View.VISIBLE);
            holder.rightCard.setImageResource(cardsIds.get(position*2 + 1));
            holder.isRightEnabled.setChecked(user.getDeckCardsIds().contains(Integer.valueOf(position*2+1)));
        }
        else {
            // we hide it
            holder.rightCard.setVisibility(View.INVISIBLE);
            holder.isRightEnabled.setVisibility(View.INVISIBLE);
        }
        // we add listeners on checkboxes
        holder.isLeftEnabled.setOnClickListener(v -> activateCard(v, position*2));
        holder.isRightEnabled.setOnClickListener(v -> activateCard(v, position * 2 + 1));
    }

    @Override
    public int getItemCount() {
        // because we have two cards per row
        return (cardsIds.size()+1)/2;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView leftCard, rightCard;
        CheckBox isLeftEnabled, isRightEnabled;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            leftCard = itemView.findViewById(R.id.imageView_theLeftCard);
            rightCard = itemView.findViewById(R.id.imageView_theRightCard);
            isLeftEnabled = itemView.findViewById(R.id.checkBox_isLeftEnabled);
            isRightEnabled = itemView.findViewById(R.id.checkBox_isRightEnabled);
        }
    }

    private void activateCard(View v, int position) {
        CheckBox cb = (CheckBox) v;

        if (cb.isChecked()) {
            if (!user.activateCard(position)) {
                Toast.makeText(context, "Only 5 cards can be active.", Toast.LENGTH_SHORT).show();
                cb.setChecked(false);
            }
        }
        else {
            user.removeDeckCard(position);
        }

        for (int id : user.getDeckCardsIds())
            Log.i("TAG", "activeCard : " + id);
    }
}
