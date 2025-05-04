package com.example.sulli_000.leboncoin;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class RecupereInfosUsersActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recupere_infos_users);

        Button sauvegarderProfil = this.findViewById(R.id.envoieDonnees);

        TextView pseudo = this.findViewById(R.id.pseudo);
        TextView mail = this.findViewById(R.id.mail);
        TextView tel = this.findViewById(R.id.tel);


        sauvegarderProfil.setOnClickListener(this);
    }

    public void onClick(View view) {
        if(view.getId() == R.id.envoieDonnees){
           // SharedPreferences sharedPref = getApplicat
        }



    }
}
