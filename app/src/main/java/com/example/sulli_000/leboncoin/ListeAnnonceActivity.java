package com.example.sulli_000.leboncoin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

public class ListeAnnonceActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<ModeleAnnonce> liste;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_liste_annonce);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));

        String urlListAnnonce = "https://ensweb.users.info.unicaen.fr/android-api/mock-api/liste.json";

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(urlListAnnonce, null,
                new Response.Listener<JSONObject>() {
                    public void onResponse(JSONObject response) {

                        try {
                            liste = new ArrayList<>();
                            JSONArray listAnnonce = response.getJSONArray("response");

                            for(int i = 0; i < listAnnonce.length(); i++) {
                                JSONObject jsonAnnonce = listAnnonce.getJSONObject(i);
                                liste.add(new ModeleAnnonce(jsonAnnonce.getString("id"), jsonAnnonce.getString("titre"), jsonAnnonce.getString("description"), jsonAnnonce.getString("prix"), jsonAnnonce.getString("pseudo"), jsonAnnonce.getString("emailContact"), jsonAnnonce.getString("telContact"), jsonAnnonce.getString("ville"), jsonAnnonce.getString("cp"), jsonAnnonce.getString("date"),jsonAnnonce.getJSONArray("images")));
                            }

                            recyclerView.setAdapter(new AnnonceAdapter(liste));

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


<<<<<<< HEAD
    public void viewDetail(View v) {
        TextView clicked=(TextView) v;
        Intent intent = new Intent(this, VoirAnnonceActivity.class);
=======
    /*public void viewDetail(View v) {
        TextView clicked = (TextView) v;
        Intent intent = new Intent(ListeAnnonceActivity.this, VoirAnnonceActivity.class);
>>>>>>> a74231a09ee590e5bea494842e7c83149c15e9a3
        intent.putExtra("urlAnnonce",clicked.getTag().toString());
        startActivity(intent);

    }*/


    public class AnnonceViewHolder extends RecyclerView.ViewHolder {
        private TextView info;
        private ImageView image;


        public AnnonceViewHolder(View itemView) {
            super(itemView);
            info = itemView.findViewById(R.id.InfoAnnonce);
            image = itemView.findViewById(R.id.image);
        }

        public void bind(ModeleAnnonce annonce) {
            info.setTag(annonce.getId());
            info.setText(annonce.getTitre() + " \n" + annonce.getPrix()+" €");

            try {
                if (annonce.getListeImage().length() != 0) {
                    Picasso.with(image.getContext()).load(annonce.getListeImage().get(0).toString()).centerCrop().fit().into(image);
                } else {
                    Picasso.with(image.getContext()).load(R.drawable.defaut).centerCrop().fit().into(image);
                }
            } catch (JSONException e) {
                Picasso.with(image.getContext()).load(R.drawable.defaut).centerCrop().fit().into(image);
            }
        }
    }

    public class AnnonceAdapter extends RecyclerView.Adapter<AnnonceViewHolder>{

        List<ModeleAnnonce> liste;

        public AnnonceAdapter(List<ModeleAnnonce> list) {this.liste = list;}

        @Override
        public AnnonceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item,parent,false);
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
}
