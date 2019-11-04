package com.example.bloomsday.fragments;

import android.content.Context;
import android.content.Intent;
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
import com.example.bloomsday.ui.ChooseCharacterActivity;

public class FirstFragment extends Fragment {


    private TextView nextFirstFragment;
    private ViewPager viewPager;

    public FirstFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate( R.layout.fragment_first, container, false );
        viewPager = getActivity().findViewById( R.id.viewPager );
        nextFirstFragment = view.findViewById( R.id.nextFirstFragment );
        nextFirstFragment.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                viewPager.setCurrentItem( 1 );
            }
        } );
        LinearLayout linearLayout = view.findViewById( R.id.firstFragment );
        AnimationDrawable animationDrawable = (AnimationDrawable) linearLayout.getBackground();
        animationDrawable.setEnterFadeDuration( 1000 );
        animationDrawable.setExitFadeDuration( 2000 );
        animationDrawable.start();
        return view;

    }

}
