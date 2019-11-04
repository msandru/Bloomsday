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
import com.example.bloomsday.ui.ChatActivity;
import com.example.bloomsday.ui.ChooseCharacterActivity;
import com.example.bloomsday.ui.IntroActivity;
import com.example.bloomsday.ui.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ThirdFragment extends Fragment {


    private TextView doneThirdFragment;
    private ViewPager viewPager;
    private TextView backThirdFragment;
    private FirebaseUser firebaseUser;

    public ThirdFragment() {
        // Required empty public constructor
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
                firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                if(firebaseUser != null){
                    Intent newActivity = new Intent( getActivity(), ChooseCharacterActivity.class );
                    startActivity( newActivity );
                    return;
                }
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
