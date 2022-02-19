package com.example.sulli_000.leboncoin;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RecupereInfosUsers extends BaseActivity implements View.OnClickListener {

    EditText pseudo;
    EditText email;
    EditText tel;

    TextView vueNom;
    TextView vueMail;
    TextView vueTel;

    Button sauvegarderProfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Ajout d'un profil");
        setContentView(R.layout.activity_recupere_infos_users);

        sauvegarderProfil = this.findViewById(R.id.envoieDonnees);
        sauvegarderProfil.setOnClickListener(this);

        pseudo = this.findViewById(R.id.pseudo);
        email = this.findViewById(R.id.mail);
        tel = this.findViewById(R.id.tel);

        vueNom = this.findViewById(R.id.nomProfil);
        vueMail = this.findViewById(R.id.mailProfil);
        vueTel = this.findViewById(R.id.telProfil);

        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preferences_file_key),Context.MODE_PRIVATE);

        String nomProfil = sharedPref.getString(getString(R.string.nom_utilisateur),"nom non definit");
        String mailProfil = sharedPref.getString(getString(R.string.email_utilisateur),"mail non definit");
        String telProfil = sharedPref.getString(getString(R.string.tel_utilisateur),"tel non definit");
        vueNom.append(nomProfil);
        vueMail.append(mailProfil);
        vueTel.append(telProfil);
    }

    public void onClick(View view) {
        if(view.getId() == R.id.envoieDonnees){

            SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preferences_file_key),Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();

            String pseudoSTR = pseudo.getText().toString();
            String emailSTR = email.getText().toString();
            String telSTR = tel.getText().toString();

            if(!isPseudoValid(pseudoSTR)){
                pseudo.setError("Veuillez renseigner un nom valide");
            }
            else if(!isEmailValid(emailSTR)){
                email.setError("Veuillez renseigner un mail valide");
            }
            else if(!isValidMobile(telSTR)){
                tel.setError("Veuillez renseigner un telephone valide");
            }
            else{
                editor.putString(getString(R.string.nom_utilisateur),pseudoSTR);
                editor.putString(getString(R.string.email_utilisateur),emailSTR);
                editor.putString(getString(R.string.tel_utilisateur), telSTR);
                editor.commit();
                Toast.makeText(getApplicationContext(),"Profil enregistré avec succès !", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    public static boolean isEmailValid(String email) {
        return !(email == null || TextUtils.isEmpty(email)) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isPseudoValid(String pseudo){
        if(TextUtils.isEmpty(pseudo)){
            return false;
        }
        else{
            return true;
        }
    }
    public static boolean isValidMobile(String phone) {
        return android.util.Patterns.PHONE.matcher(phone).matches();
    }

}
