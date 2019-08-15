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
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ViewPager viewPager;
    private TextView nextSecondFragment;
    private TextView backSecondFragment;

    public SecondFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SecondFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SecondFragment newInstance(String param1, String param2) {
        SecondFragment fragment = new SecondFragment();
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
