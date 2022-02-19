package com.example.sulli_000.leboncoin;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import pub.devrel.easypermissions.EasyPermissions;

import static android.content.Intent.ACTION_DIAL;


public class VoirAnnonce  extends BaseActivity implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener, View.OnClickListener {

    private String id;
    private String pseudo;
    private static final int REQUEST_GALLERY_CODE = 200;
    private static  final int REQUEST_REQUEST_CODE = 300;

    protected static TextView texteTitre;
    protected static TextView textePrix;
    protected static TextView texteVille;
    protected static TextView texteCp;
    protected static TextView texteDescription;
    protected static TextView texteDate;
    protected static TextView nomVendeur;
    protected static TextView texteEmail;
    protected static TextView texteTel;
    protected static ImageView iconeAjoutImage;

    private ModeleAnnonce annonce;

    protected SliderLayout sliderLayout;
    protected HashMap<Integer,String> Hash_file_maps ;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voir_annonce);

        Hash_file_maps = new HashMap<Integer, String>();
        sliderLayout =findViewById(R.id.slider);

        texteTitre = this.findViewById(R.id.titre);
        textePrix = this.findViewById(R.id.prix);
        texteVille = this.findViewById(R.id.ville);
        texteCp = this.findViewById(R.id.cp);
        texteDescription = this.findViewById(R.id.description);
        texteDate = this.findViewById(R.id.date);
        nomVendeur = this.findViewById(R.id.nomVendeur);
        texteEmail = this.findViewById(R.id.email);
        texteTel = this.findViewById(R.id.telContact);

        iconeAjoutImage = this.findViewById(R.id.ajoutImage);
        iconeAjoutImage.setOnClickListener(this);

        // Permet de pouvoir recuperer extras qui ont été donnés à cette activity
        // Ici on souhaite recuperer de l'annonce qui a été cliquée
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        // Si il y a un extras, on recupere celui pour afficher l'annonce donnée
        if (extras != null) {
            ModeleAnnonce annonce = (ModeleAnnonce) extras.getParcelable("annonce");

            this.pseudo = annonce.pseudo;
            this.id = annonce.id;
            this.annonce = annonce;

            getSupportActionBar().setTitle("Annonce de " + annonce.pseudo);

            // On transforme la date recuperée en format standard français
            Date date = new Date(Long.parseLong(annonce.date) * 1000L);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:MM:SS ");
            String dateStr = simpleDateFormat.format(date);

            texteTitre.setText(annonce.titre);
            textePrix.setText(annonce.prix + " €");
            texteVille.setText(annonce.ville);
            texteCp.setText(annonce.cp);
            texteDescription.setText(annonce.decription);
            texteDate.setText("Publié le : " + dateStr);
            nomVendeur.setText("Contactez " + annonce.pseudo);
            texteEmail.setText(annonce.email);
            texteTel.setText(annonce.telContact);


            // Si annonce.image est null, cela veut dire que l'annonce que l'on veut afficher vient d'être déposée
            // On lui ajoute donc une image par defaut
            if(annonce.image ==  null) {
                Hash_file_maps.put(1, BaseActivity.URLImageDefaut);}

            // Sinon on ajoute les images que contient l'annonce
            else if(annonce.image.length > 0){
                for (int i = 0; i < annonce.image.length; i++) {
                    Hash_file_maps.put(i + 1, annonce.image[i]);
                }
            }
        }

        for(Integer name : Hash_file_maps.keySet()){
            TextSliderView textSliderView = new TextSliderView(this);
            textSliderView
                    .description(String.valueOf(name))
                    .image(Hash_file_maps.get(name))
                    .setScaleType(BaseSliderView.ScaleType.Fit)
                    .setOnSliderClickListener(this);
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle()
                    .putString("extra", String.valueOf(name));
            sliderLayout.addSlider(textSliderView);
        }
        sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        sliderLayout.setCustomAnimation(new DescriptionAnimation());
        sliderLayout.addOnPageChangeListener(this);
        onStop();
    }

    @Override
    protected void onStop() {
        sliderLayout.stopAutoCycle();
        super.onStop();
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

    @Override
    public void onPageSelected(int position){}

    @Override
    public void onPageScrollStateChanged(int state) {}

    private void appelVendeur(View v) {
        //On récupère le numéro du vendeur
        TextView numberClic = (TextView) v;
        String number = numberClic.getText().toString();
        Uri tel = Uri.parse("tel:" + number);
        //On lance l'intent qui permet de contacter le vendeur
        Intent appelIntent = new Intent(ACTION_DIAL, tel);
        startActivity(appelIntent);
    }

    private void envoieMailVendeur(View v) {
        // On récupère le titre et le mail de l'annonce
        TextView emailClic = (TextView) v;
        String email = emailClic.getText().toString();
        String sujet = "Votre annonce : " + this.findViewById(R.id.titre).getTag().toString();
        Uri mailAdresse = Uri.parse("mailto:" + email);

        //On crée un intent avec le mail et le sujet de l'annonce comme paramètres de celle-ci
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, mailAdresse);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, sujet);
        startActivity(Intent.createChooser(emailIntent, "title"));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, VoirAnnonce.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_GALLERY_CODE && resultCode == Activity.RESULT_OK) {
            Uri imageUri = data.getData();
            if(EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                String cheminImage = BaseActivity.recupereCheminReelDepuisUri(imageUri, VoirAnnonce.this);
                File file = new File(cheminImage);
                ajoutImage(file,imageUri);
            }
            else{
                EasyPermissions.requestPermissions(this, "ok", REQUEST_REQUEST_CODE, Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }
    }

    private void chargerImageDepuisGallerie() {
        Intent gallerieIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(gallerieIntent, REQUEST_GALLERY_CODE);
    }


    private void ajoutImage(File file, Uri uri){
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();
        RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .setType(MultipartBody.FORM)
                .addFormDataPart("apikey", "21509624")
                .addFormDataPart("method", "addImage")
                .addFormDataPart("id", this.id)
                .addFormDataPart("photo", "image.png",
                        RequestBody.create(MediaType.parse(getContentResolver().getType(uri)), file))
                .build();
        final Request request = new Request.Builder().url(BaseActivity.url)
                .post(requestBody).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
            }

            @Override
            public void onFailure(Call call, IOException e) {
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.ajoutImage) {
            // On verifie que le pseudo de l'annonce correspond au pseudo enregistré dans les preferences
            // L'utilisateur peut donc ajouter une imageu uniquement aux annonces qu'il a deposé
            SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(getString(R.string.preferences_file_key), Context.MODE_PRIVATE);
            String nom = sharedPref.getString(getString(R.string.nom_utilisateur), "nom non definit");
            if(nom.equals(this.pseudo)){
                chargerImageDepuisGallerie();
            } else{
                Toast.makeText(getApplicationContext(), "Vous n'avez pas l'authorisation d'ajouter une image", Toast.LENGTH_LONG).show();
            }

        }
    }


    protected ModeleAnnonce getCurrentAnnonce(){
        return this.annonce;
    }

    protected void setCurrentAnnonce(ModeleAnnonce annonce){
        this.annonce = annonce;
    }


}


