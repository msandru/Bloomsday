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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.bloomsday.R;
import com.example.bloomsday.ui.ChooseCharacterActivity;
import com.example.bloomsday.ui.IntroActivity;
import com.example.bloomsday.ui.LoginActivity;

public class ThirdFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TextView doneThirdFragment;
    private ViewPager viewPager;
    private TextView backThirdFragment;

    public ThirdFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ThirdFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ThirdFragment newInstance(String param1, String param2) {
        ThirdFragment fragment = new ThirdFragment();
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
        View view = inflater.inflate( R.layout.fragment_third, container, false );
        viewPager = getActivity().findViewById( R.id.viewPager );
        doneThirdFragment = view.findViewById( R.id.doneThirdFragment );
        doneThirdFragment.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newActivity = new Intent( getActivity(), LoginActivity.class );
                startActivity( newActivity );
            }
        } );
        LinearLayout linearLayout = view.findViewById( R.id.thirdFragment );
        AnimationDrawable animationDrawable = (AnimationDrawable) linearLayout.getBackground();
        animationDrawable.setEnterFadeDuration( 1000 );
        animationDrawable.setExitFadeDuration( 2000 );
        animationDrawable.start();
        backThirdFragment = view.findViewById( R.id.backThirdFragment );
        backThirdFragment.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem( 1 );
            }
        } );
        return view;
    }


}
