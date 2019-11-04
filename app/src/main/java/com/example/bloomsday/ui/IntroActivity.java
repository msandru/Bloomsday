package com.example.bloomsday.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.example.bloomsday.adapters.IntroAdapter;
import com.example.bloomsday.R;
import com.example.bloomsday.adapters.IntroAdapter;

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
