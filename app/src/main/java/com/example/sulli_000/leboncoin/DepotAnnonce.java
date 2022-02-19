package com.example.sulli_000.leboncoin;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.test.espresso.remote.EspressoRemoteMessage;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class DepotAnnonce extends BaseActivity implements View.OnClickListener {
    private EditText titre;
    private EditText prix;
    private EditText description;
    private EditText cp;
    private EditText ville;
    private Button bouton_depot;

    public ModeleAnnonce annonce;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Depot d'annonce");
        setContentView(R.layout.activity_depot_annonce);
        bouton_depot = this.findViewById(R.id.bouton_depot);
        bouton_depot.setOnClickListener(this);

        titre = this.findViewById(R.id.titreValue);
        prix = this.findViewById(R.id.prixValue);
        description = this.findViewById(R.id.descriptionValue);
        cp = this.findViewById(R.id.cpValue);
        ville = this.findViewById(R.id.villeValue);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.bouton_depot) {

            final String titre_value = titre.getText().toString();
            final String prix_value = prix.getText().toString();
            final String description_value = description.getText().toString();
            final String cp_value = cp.getText().toString();
            final String ville_value = ville.getText().toString();

            // On verifie que l'utilisateur remplit au moins les champs titre, prix et ville
            // car s'il n'y a pas de titre, il est inutile de faire la requête, le serveur repondra forcement false a la requête
            if (chaineVide(titre_value)) {
                titre.setError("Veuillez renseigner un titre");
            }

            else if(chaineVide(prix_value)){
                prix.setError("Veuillez renseigner un prix");
            }

            else if(chaineVide(ville_value)){
                ville.setError("Veuillez renseigner une ville");
            }

            else {
                RequestQueue MyRequestQueue = Volley.newRequestQueue(this);
                StringRequest MyStringRequest = new StringRequest(com.android.volley.Request.Method.POST, BaseActivity.url, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        try {
                            // On recupère la reponse du serveur
                            JSONObject json = new JSONObject(response);
                            Boolean success = json.getBoolean("success");

                            // Si l'annonce est validée par le serveur, on la récupère et on l'affiche
                            if (success) {
                                makeToast("Annonce déposée avec succès !");

                                JSONObject reponse = json.getJSONObject("response");
                                JSONArray images = reponse.getJSONArray("images");

                                String[] liste_images = null;

                                annonce = new ModeleAnnonce(reponse.getString("id"), reponse.getString("titre"), reponse.getString("description"), reponse.getString("prix"), liste_images, reponse.getString("pseudo"), reponse.getString("emailContact"), reponse.getString("telContact"), reponse.getString("ville"), reponse.getString("cp"), reponse.getString("date"));
                                // On fait d'abord le "finish" pour que l'utilisateur
                                // soit rediriger sur l'accueil s'il choisit de cliquer sur le bouton "retour"
                                // et non sur le formulaire de dépot de l'annonce  -->  plus "sympa"

                                finish();
                                afficheAnnonceCree(annonce);
                            }

                            // Sinon on affiche le message d'erreur
                            else {
                                String erreur = json.getString("response");
                                Toast.makeText(getApplicationContext(),erreur.toString() + "\n Annonce non enregistrée" , Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(),e.getMessage()+ "\n Annonce non enregistrée" , Toast.LENGTH_LONG).show();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),"Problème de connexion internet \n Annonce non enregistrée", Toast.LENGTH_LONG).show();
                    }
                }) {

                    protected Map<String, String> getParams() {

                        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(getString(R.string.preferences_file_key), Context.MODE_PRIVATE);
                        String nom = sharedPref.getString(getString(R.string.nom_utilisateur), "nom non definit");
                        String mail = sharedPref.getString(getString(R.string.email_utilisateur), "mail non definit");
                        String tel = sharedPref.getString(getString(R.string.tel_utilisateur), "tel non definit");

                        Map<String, String> MyData = new HashMap<String, String>();
                        MyData.put("apikey", BaseActivity.NUMETUString);
                        MyData.put("method", "save");
                        MyData.put("titre", titre_value);
                        MyData.put("description", description_value);
                        MyData.put("prix", prix_value);
                        MyData.put("pseudo", nom);
                        MyData.put("emailContact", mail);
                        MyData.put("telContact", tel);
                        MyData.put("ville", ville_value);
                        MyData.put("cp", cp_value);

                        return MyData;
                    }
                };
                MyRequestQueue.add(MyStringRequest);
            }
        }
    }


    public void afficheAnnonceCree(ModeleAnnonce annonce){
        Intent intent = new Intent(DepotAnnonce.this, VoirAnnonce.class);
        intent.putExtra("annonce",annonce);
        startActivity(intent);
    }

    public static boolean chaineVide(String str){
        if(TextUtils.isEmpty(str)) {
            return true;
        }
        else {return false;}
    }
}
