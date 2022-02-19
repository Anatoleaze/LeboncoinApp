package com.example.sulli_000.leboncoin;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ListeAnnonce extends BaseActivity {

    private RecyclerView recyclerView;
    private ArrayList<ModeleAnnonce> liste;
    public static String tagImageAnnonce;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Liste des annonces");
        setContentView(R.layout.activity_liste_annonce);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(BaseActivity.urlListeAnnonce, null,
                new Response.Listener<JSONObject>() {
                    public void onResponse(JSONObject response) {

                        try {
                            liste = new ArrayList<>();
                            JSONArray listAnnonce = response.getJSONArray("response");

                            for(int i = 0; i < listAnnonce.length(); i++) {
                                JSONObject jsonAnnonce = listAnnonce.getJSONObject(i);
                                JSONArray images = jsonAnnonce.getJSONArray("images");

                                String[] liste_images;
                                // On regarde si l'annonce possède une image
                                // Si ce n'est pas le cas, on lui ajoute une image par defaut
                                if(images.length() > 0) {
                                    liste_images = new String[images.length()];
                                    for(int j = 0; j < images.length(); j++){
                                        liste_images[j] = images.getString(j);
                                    }
                                } else
                                    {
                                        liste_images = new String[1];
                                        liste_images[0] = BaseActivity.URLImageDefaut;
                                    }

                                liste.add(new ModeleAnnonce(jsonAnnonce.getString("id"), jsonAnnonce.getString("titre"), jsonAnnonce.getString("description"), jsonAnnonce.getString("prix"), liste_images, jsonAnnonce.getString("pseudo"), jsonAnnonce.getString("emailContact"), jsonAnnonce.getString("telContact"), jsonAnnonce.getString("ville"), jsonAnnonce.getString("cp"), jsonAnnonce.getString("date")));
                            }

                            AnnonceAdapter adapter = new AnnonceAdapter(liste);
                            recyclerView.setAdapter(adapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(jsonObjectRequest);
    }


    public void viewDetail(View v) {
        TextView clicked = (TextView) v;
        ModeleAnnonce annonce = (ModeleAnnonce) clicked.getTag();
        Intent intent = new Intent(ListeAnnonce.this, VoirAnnonce.class);
        intent.putExtra("annonce",annonce);
        startActivity(intent);
    }

    public void zoom(View v){
        tagImageAnnonce = v.getTag().toString();
        // Si le tag (l'url de l'image) est différent de celui de l'image par defaut, on autorise un zoom de l'image affiché
        // dans la liste d'annonce
        // Le zoom sur l'image par défaut n'étant pas utile
        if(tagImageAnnonce != BaseActivity.URLImageDefaut) {
            Intent intent = new Intent(this, Activity_zoom.class);
            intent.putExtra(v.getTag().toString(), tagImageAnnonce);
            startActivity(intent);
        }
    }




}
