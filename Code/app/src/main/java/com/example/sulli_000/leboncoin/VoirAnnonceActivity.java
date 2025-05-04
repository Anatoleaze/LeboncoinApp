package com.example.sulli_000.leboncoin;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static android.content.Intent.ACTION_DIAL;


public class VoirAnnonceActivity extends AppCompatActivity implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener{

    public HashMap<String,String> Hash_file_maps ;
    public SliderLayout imageAnnonce;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voir_annonce);

        final TextView texteTitre = this.findViewById(R.id.titre);
        final TextView textePrix = this.findViewById(R.id.prix);
        final TextView texteDescription = this.findViewById(R.id.description);
        final TextView nomVendeur = this.findViewById(R.id.nomVendeur);
        final TextView texteEmail = this.findViewById(R.id.email);
        final TextView texteTel = this.findViewById(R.id.telContact);
        imageAnnonce =findViewById(R.id.image);


        // permet de pouvoir recuperer des valeurs de l'activity qui à démarrer celle-ci
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();


        String urlNoImage = "https://ensweb.users.info.unicaen.fr/android-api/mock-api/completeAd.json";
        String urlEmpty = "https://ensweb.users.info.unicaen.fr/android-api/mock-api/erreur.json";

        String urlWithImage = "";
        if(extras != null){
            String id = extras.getString("urlAnnonce");
            urlWithImage = "https://ensweb.users.info.unicaen.fr/android-api/mock-api/details/" + id + ".json";

        }
        else {
            urlWithImage = "https://ensweb.users.info.unicaen.fr/android-api/mock-api/completeAdWithImages.json";
        }

        //Requête pour récupérer le JSON
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(urlWithImage, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            Boolean success = response.getBoolean("success");
                            Hash_file_maps = new HashMap<String, String>();
                            // On test la valeur du "success" du JSON récupérer  et on affiche ce qu'il faut en conséquence
                                if (success == true && response != null) {
                                    JSONObject data = response.getJSONObject("response");   // On recupère le JSONObject qui contient toutes les données
                                    JSONArray images = data.getJSONArray("images");

                                    // S'il n'y pas d'images, on affiche une image par défaut
                                    if(images.length() == 0){
                                        Hash_file_maps.put("images",String.valueOf(R.drawable.defaut));
                                    }else {
                                        for (int i = 0; i < images.length(); i++) {
                                            Hash_file_maps.put("images", images.get(i).toString());
                                        }
                                    }
                                for(String name : Hash_file_maps.keySet()){
                                        TextSliderView textSliderView = new TextSliderView(VoirAnnonceActivity.this);
                                        textSliderView
                                                .description(name)
                                                .image(Hash_file_maps.get(name))
                                                .setScaleType(BaseSliderView.ScaleType.Fit)
                                                .setOnSliderClickListener(VoirAnnonceActivity.this);
                                        textSliderView.bundle(new Bundle());
                                        textSliderView.getBundle()
                                                .putString("extra",name);
                                        imageAnnonce.addSlider(textSliderView);
                                                                    }

                                    imageAnnonce.setPresetTransformer(SliderLayout.Transformer.Accordion);
                                    imageAnnonce.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
                                    imageAnnonce.setCustomAnimation(new DescriptionAnimation());
                                    imageAnnonce.setDuration(3000);
                                    imageAnnonce.addOnPageChangeListener(VoirAnnonceActivity.this);

                                texteTitre.setText(data.get("titre").toString() + "\n");
                                texteTitre.setTag(data.get("titre").toString());
                                textePrix.setText("\n" + data.get("prix").toString()+" € \n" + data.get("cp").toString() + "  " + data.get("ville").toString() +"\n");
                                texteDescription.setText(data.get("description").toString()+"\n "+"\n "+"Publié le : " + data.get("date").toString() + "\n");
                                nomVendeur.setText("Contactez" + " " + data.get("pseudo").toString() + " :");
                                texteEmail.setText(data.get("emailContact").toString());
                                texteTel.setText(data.get("telContact").toString());
                            }

                            else if(success == false){
                                String data = response.getString("response");
                                texteTitre.setText(data);
                            }
                        }
                        // S'il y a une erreur pour la récupération des valeurs dans le JSON, on arrive ici
                        // et on affiche un petit "Toast" pour l'utilisateur et on quitte cette activity pour revenir à la précédente
                        catch (JSONException e) {

                            Toast.makeText(getApplicationContext(), "Problème de récupération des données : " +'\n' + e.getMessage(), Toast.LENGTH_LONG).show();
                            finish();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        queue.add(jsonObjectRequest);
    }

    @Override
    protected void onStop() {
        imageAnnonce.stopAutoCycle();
        super.onStop();
    }

    public void onSliderClick(BaseSliderView slider) {
        Toast.makeText(this,slider.getBundle().get("extra") + "",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

    @Override
    public void onPageSelected(int position) {
        Log.d("Slider Demo", "Page Changed: " + position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {}

    public void appelVendeur(View v){
        //On récupère le numéro du vendeur
        TextView numberClic = (TextView) v;
        String number = numberClic.getText().toString();
        Uri tel = Uri.parse("tel:" + number);
        //On lance l'intent qui permet de contacter le vendeur
        Intent appelIntent = new Intent(ACTION_DIAL,tel);
        startActivity(appelIntent);
    }

    public void envoieMailVendeur(View v){
        // On récupère le titre et le mail de l'annonce
        TextView emailClic = (TextView) v;
        String email = emailClic.getText().toString();
        String sujet = "Votre annonce : " + this.findViewById(R.id.titre).getTag().toString();
        Uri mailAdresse = Uri.parse("mailto:" + email);

        //On crée un intent avec le mail et le sujet de l'annonce comme paramètres de celle-ci
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, mailAdresse);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, sujet);

        startActivity(Intent.createChooser(emailIntent,"title"));
    }

}


