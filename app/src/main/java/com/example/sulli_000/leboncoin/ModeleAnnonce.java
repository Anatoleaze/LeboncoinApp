package com.example.sulli_000.leboncoin;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by sulli_000 on 25/01/2018.
 */

public class ModeleAnnonce implements Parcelable {

    public String id;
    public String titre;
    public String decription;
    public String prix;
    public String[] image;
    public String pseudo;
    public String email;
    public String telContact;
    public String ville;
    public String cp;
    public String date;

    public static final Creator<ModeleAnnonce> CREATOR = new Creator<ModeleAnnonce>() {
        @Override
        public ModeleAnnonce createFromParcel(Parcel in) {
            return new ModeleAnnonce(in);
        }

        @Override
        public ModeleAnnonce[] newArray(int size) {
            return new ModeleAnnonce[size];
        }
    };

    public ModeleAnnonce(String _id, String _titre, String _decription, String _prix, String[] _image, String _pseudo, String _email, String _telContact, String _ville, String _cp, String _date){
        this.id = _id;
        this.titre = _titre;
        this.decription = _decription;
        this.prix = _prix;
        this.image = _image;
        this.pseudo = _pseudo;
        this.email = _email;
        this.telContact = _telContact;
        this.ville = _ville;
        this.cp = _cp;
        this.date = _date;
    }

    private ModeleAnnonce(Parcel in){
        this.id = in.readString();
        this.titre = in.readString();
        this.decription = in.readString();
        this.prix = in.readString();
        this.image = in.createStringArray();
        this.pseudo = in.readString();
        this.email = in.readString();
        this.telContact = in.readString();
        this.ville = in.readString();
        this.cp = in.readString();
        this.date = in.readString();
    }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.id);
        parcel.writeString(this.titre);
        parcel.writeString(this.decription);
        parcel.writeString(this.prix);
        parcel.writeStringArray(this.image);
        parcel.writeString(this.pseudo);
        parcel.writeString(this.email);
        parcel.writeString(this.telContact);
        parcel.writeString(this.ville);
        parcel.writeString(this.cp);
        parcel.writeString(this.date);
    }
}
