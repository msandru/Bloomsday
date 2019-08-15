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
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private TextView nextFirstFragment;
    private ViewPager viewPager;

    public FirstFragment() {
        // Required empty public constructor
    }


    public static FirstFragment newInstance(String param1, String param2) {
        FirstFragment fragment = new FirstFragment();
        Bundle args = new Bundle();
        args.putString( ARG_PARAM1, param1 );
        args.putString( ARG_PARAM2, param2 );
        fragment.setArguments( args );
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        if (getArguments() != null) {
            mParam1 = getArguments().getString( ARG_PARAM1 );
            mParam2 = getArguments().getString( ARG_PARAM2 );

        }
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
