package com.example.sulli_000.leboncoin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;


// Classe qui permet de creer un intent qui affiche une image en grand

public class Activity_zoom extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoom);
        Intent intent=getIntent();
        String url=intent.getStringExtra(ListeAnnonce.tagImageAnnonce);
        ImageView img=findViewById(R.id.grandeImage);
        Picasso.with(getBaseContext()).load(url).fit().centerInside().into(img);
    }

}


