package com.example.bloomsday.fragments;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.bloomsday.R;

public class SecondFragment extends Fragment {


    private ViewPager viewPager;
    private TextView nextSecondFragment;
    private TextView backSecondFragment;

    public SecondFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate( R.layout.fragment_second, container, false );
        viewPager = getActivity().findViewById( R.id.viewPager );
        nextSecondFragment = view.findViewById( R.id.nextSecondFragment );
        nextSecondFragment.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem( 2 );
            }
        } );
        LinearLayout linearLayout = view.findViewById( R.id.secondFragment );
        AnimationDrawable animationDrawable = (AnimationDrawable) linearLayout.getBackground();
        animationDrawable.setEnterFadeDuration( 1000 );
        animationDrawable.setExitFadeDuration( 2000 );
        animationDrawable.start();
        backSecondFragment = view.findViewById( R.id.backSecondFragment );
        backSecondFragment.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem( 0 );
            }
        } );
        return view;

    }

}
