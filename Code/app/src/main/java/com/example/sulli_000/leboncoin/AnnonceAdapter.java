package com.example.sulli_000.leboncoin;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by sulli_000 on 08/03/2018.
 */

public class AnnonceAdapter extends RecyclerView.Adapter<AnnonceViewHolder>{

    List<ModeleAnnonce> liste;

    public AnnonceAdapter(List<ModeleAnnonce> list) {this.liste = list;}

    @Override
    public AnnonceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item, parent, false);
        return new AnnonceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AnnonceViewHolder holder, int position) {
        ModeleAnnonce annonce = liste.get(position);
        holder.bind(annonce);
    }

    @Override
    public int getItemCount() {
        return liste.size();
    }
}
