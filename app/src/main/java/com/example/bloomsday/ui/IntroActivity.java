package com.example.bloomsday.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.ViewPager;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.bloomsday.fragments.IntroAdapter;
import com.example.bloomsday.R;

import static com.example.bloomsday.R.layout.activity_intro;

public class IntroActivity extends AppCompatActivity {

    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( activity_intro );


        viewPager = findViewById( R.id.viewPager );
        IntroAdapter adapter = new IntroAdapter( getSupportFragmentManager() );
        viewPager.setAdapter( adapter );
    }
}
