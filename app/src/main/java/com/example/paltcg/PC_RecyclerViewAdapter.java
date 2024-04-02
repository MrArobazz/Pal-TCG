package com.example.paltcg;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PC_RecyclerViewAdapter extends RecyclerView.Adapter<PC_RecyclerViewAdapter.MyViewHolder> {

    Context context;
    ArrayList<PokemonCard> cards;
    public PC_RecyclerViewAdapter(Context context, ArrayList<PokemonCard> cards) {
        this.context = context;
        this.cards = cards;
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
        Log.i("TAG", "onBindViewHolder: " + position);
        holder.leftCard.setImageResource(cards.get(position*2).getImage());
        holder.isLeftEnabled.setChecked(false);
        if (position*2+1 < getItemCount()*2-1) {
            holder.rightCard.setVisibility(View.VISIBLE);
            holder.isRightEnabled.setVisibility(View.VISIBLE);
            holder.rightCard.setImageResource(cards.get(position*2 + 1).getImage());
            holder.isRightEnabled.setChecked(false);
        }
        else {
            holder.rightCard.setVisibility(View.INVISIBLE);
            holder.isRightEnabled.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return cards.size()/2+1;
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
}
