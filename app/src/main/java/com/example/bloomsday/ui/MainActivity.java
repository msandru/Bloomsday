package com.example.bloomsday.ui;

import android.content.Intent;
import android.os.Bundle;

import com.example.bloomsday.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ConstraintLayout constraintLayout;
    private ImageView ballonImage;
    Animation frombottom;
    Animation fromtop;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        Button startPilgrimageButton = (Button) findViewById( R.id.startPilgrimageButton );
        frombottom = AnimationUtils.loadAnimation( this, R.anim.frombottom );
        fromtop = AnimationUtils.loadAnimation( this, R.anim.fromtop );
        startPilgrimageButton.setAnimation( frombottom );
        ImageView ballonImage = (ImageView) findViewById( R.id.ballonImage );
        ballonImage.setAnimation( fromtop );
        constraintLayout = findViewById( R.id.mainActivity );
        Calendar gregorianCalendar = Calendar.getInstance();
        int timeOfDay = gregorianCalendar.get( GregorianCalendar.HOUR_OF_DAY );
        if (timeOfDay >= 0 && timeOfDay < 16) {
            constraintLayout.setBackground( getDrawable( R.drawable.good_morning ) );
            startPilgrimageButton.setBackground( getDrawable( R.drawable.day_time_custom_button ) );
            ballonImage.setImageResource( R.drawable.day_ballon );
        } else if (timeOfDay >= 16 && timeOfDay < 21) {
            constraintLayout.setBackground( getDrawable( R.drawable.good_morning ) );
            startPilgrimageButton.setBackground( getDrawable( R.drawable.day_time_custom_button ) );
            ballonImage.setImageResource( R.drawable.day_ballon );
        } else if (timeOfDay >= 21 && timeOfDay < 24) {
            constraintLayout.setBackground( getDrawable( R.drawable.good_night ) );
            startPilgrimageButton.setBackground( getDrawable( R.drawable.night_time_custom_button ) );
            ballonImage.setImageResource( R.drawable.night_ballon );

        }

        startPilgrimageButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent( getApplicationContext(), IntroActivity.class );
                startActivity( intent );


            }
        } );
        Toolbar toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );

        getSupportActionBar().hide();
    }



}