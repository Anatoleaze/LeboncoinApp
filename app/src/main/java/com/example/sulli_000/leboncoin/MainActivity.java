package com.example.sulli_000.leboncoin;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.content.Intent;


public class MainActivity extends AppCompatActivity {


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {

                case R.id.navigation_dashboard:
                    Intent intent2 = new Intent(MainActivity.this,DepotAnnonce.class);
                    startActivity(intent2);
                    return true;
                case R.id.navigation_home:
                    Intent intent = new Intent(MainActivity.this, VoirAnnonceActivity.class);
                    startActivity(intent);
                    return true;
                case R.id.navigation_notifications:
                    Intent intent3 = new Intent(MainActivity.this, ListeAnnonceActivity.class);
                    startActivity(intent3);
                    return true;
                case R.id.navigation_profil:

                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        //navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

}