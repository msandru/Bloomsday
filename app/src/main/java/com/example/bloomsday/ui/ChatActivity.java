package com.example.bloomsday.ui;

import android.content.Intent;
import android.os.Bundle;

import com.example.bloomsday.fragments.FriendsFragment;
import com.example.bloomsday.fragments.MessageFragment;
import com.example.bloomsday.models.User;
import com.example.bloomsday.util.UserClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.bloomsday.R;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    CircleImageView profileImage;
    TextView username;
    DocumentReference reference;
    FirebaseUser firebaseUser;
    TabLayout chatTabLayout;
    ViewPager chatViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_chat );

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        profileImage = (CircleImageView) findViewById( R.id.profileImage );
        username = (TextView) findViewById( R.id.username );
        reference = FirebaseFirestore.getInstance().collection( getString( R.string.collection_users ) ).
                document( firebaseUser.getUid() );
        chatTabLayout = (TabLayout) findViewById( R.id.chatTabLayout );
        chatViewPager = (ViewPager) findViewById( R.id.chatViewPager );
        reference.get().addOnCompleteListener( new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                User user = task.getResult().toObject( User.class );
                profileImage.setImageResource( Integer.parseInt( user.getAvatar() ) );
                username.setText( user.getUsername() );
            }
        } );

        ViewPagerAdapter chatViewPagerAdapter = new ViewPagerAdapter( getSupportFragmentManager() );
        chatViewPagerAdapter.addFragment( new MessageFragment(), "Chats" );
        chatViewPagerAdapter.addFragment( new FriendsFragment(), "Users" );
        chatViewPager.setAdapter( chatViewPagerAdapter );
        chatTabLayout.setupWithViewPager( chatViewPager );
        Toolbar toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );
        getSupportActionBar().hide();


    }

    class ViewPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<Fragment> chatFragments;
        private ArrayList<String> chatTitles;

        public ViewPagerAdapter(FragmentManager fm) {
            super( fm );
            this.chatFragments = new ArrayList<>();
            this.chatTitles = new ArrayList<>();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return chatTitles.get( position );
        }


        @Override
        public Fragment getItem(int position) {
            return chatFragments.get( position );
        }

        @Override
        public int getCount() {
            return chatFragments.size();
        }

        public void addFragment(Fragment chatFragment, String chatTitle) {
            this.chatFragments.add( chatFragment );
            this.chatTitles.add( chatTitle );
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");

    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");

    }

    private void status(String status) {
        DocumentReference reference = FirebaseFirestore.getInstance().collection( "Users" ).
                document(FirebaseAuth.getInstance().getCurrentUser().getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put( "status", status );

        reference.update( hashMap );
    }

}
