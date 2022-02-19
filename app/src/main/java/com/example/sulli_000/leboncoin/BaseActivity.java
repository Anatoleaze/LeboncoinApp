package com.example.sulli_000.leboncoin;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by sulli_000 on 22/02/2018.
 */


// Classe absraite car on n'a pas a l'instancier
// Elle permet de créer une classe "generique"  à toutes les classes
// Ici on s'en sert pour créer un menu commun à toutes les activités
// Elle possede des méthodes qui sont utiles a plusieurs classes

public abstract class BaseActivity extends AppCompatActivity {

    public static long NUMETU = 21509624;
    public static String NUMETUString = "21509624";
    public static String url = "https://ensweb.users.info.unicaen.fr/android-api/";
    public static String urlListeAnnonce = url + "?apikey=" + NUMETU + "&method=listAll";
    public static String urlRandom = url + "?apikey=" + NUMETU + "&method=randomAd";
    public static String urlDetails = url + "?apikey=" + NUMETU + "&method=details&id=";
    public static String urlViderListe = url + "?apikey=" + NUMETU + "&method=reset";
    public static String urlRemplirListe = url + "?apikey=" + NUMETU + "&method=populate";
    public final static String URLImageDefaut = recupereCheminDepuisId(R.drawable.defaut);


    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_ressource, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.liste_action:
                intent = new Intent(this, ListeAnnonce.class);
                startActivity(intent);
                return true;
            case R.id.ajoute_annonce_action:
                intent = new Intent(this, DepotAnnonce.class);
                startActivity(intent);
                return true;
            case R.id.compte_action:
                intent = new Intent(this, RecupereInfosUsers.class);
                startActivity(intent);
                return true;
            case R.id.vider_liste:
                viderListe();
                break;
            case R.id.remplir_liste:
                remplirListe();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void makeToast(String message){
        Toast.makeText(getApplicationContext(), message,Toast.LENGTH_LONG).show();
    }

    public static String recupereCheminDepuisId (int resourceId) {
        return Uri.parse("android.resource://"+R.class.getPackage().getName()+"/" +resourceId).toString();
    }

    public static String recupereCheminReelDepuisUri(Uri contentURI, Activity activity) {
        Cursor cursor = activity.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }

    public void viderListe(){
        requeteVolley(urlViderListe);
        startIntent(getApplicationContext(),ListeAnnonce.class);
        makeToast("Liste vidée avec succès");
    }

    public void remplirListe(){
        requeteVolley(urlRemplirListe);
        startIntent(getApplicationContext(),ListeAnnonce.class);
        makeToast("Liste remplie avec succès");
    }

    public void requeteVolley(String url){
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    public void onResponse(JSONObject response) {
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });
        queue.add(jsonObjectRequest);
    }

    public void startIntent(Context courante, Class cible){
        Intent intent = new Intent(courante, cible);
        startActivity(intent);
    }



}
