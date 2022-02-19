package com.example.sulli_000.leboncoin;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by sulli_000 on 08/03/2018.
 */

public class AnnonceViewHolder extends RecyclerView.ViewHolder{

    private TextView info;
    private ImageView image;


    public AnnonceViewHolder(View itemView) {
        super(itemView);
        info = itemView.findViewById(R.id.infoAnnonce);
        image = itemView.findViewById(R.id.image);

    }

    public void bind(ModeleAnnonce annonce) {
        info.setTag(new ModeleAnnonce(annonce.id,annonce.titre,annonce.decription,annonce.prix,annonce.image, annonce.pseudo, annonce.email, annonce.telContact, annonce.ville, annonce.cp,annonce.date));
        info.setText(annonce.titre + " \n\n" + annonce.prix + " €");
        image.setTag(annonce.image[0]);
        Picasso.with(image.getContext()).load(annonce.image[0]).centerCrop().fit().into(image);
    }
}