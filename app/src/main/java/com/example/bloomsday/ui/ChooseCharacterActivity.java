package com.example.bloomsday.ui;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;

import com.example.bloomsday.R;
import com.example.bloomsday.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.maps.GeoApiContext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.Calendar;
import java.util.GregorianCalendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChooseCharacterActivity extends AppCompatActivity {

    ListView myListView;
    String[] characters;
    String[] descriptions;
    String TAG = "ChooseYourCharacterActivity";
    private ConstraintLayout constraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_choose_character_activity );
        int[] drawableIds = {R.drawable.bloomsday1,
                R.drawable.bloomsday2, R.drawable.bloomsday3};
        constraintLayout = findViewById( R.id.chooseCharacter );
        Calendar gregorianCalendar = Calendar.getInstance();
        int timeOfDay = gregorianCalendar.get( GregorianCalendar.HOUR_OF_DAY );
        if (timeOfDay >= 0 && timeOfDay < 16) {
            constraintLayout.setBackground( getDrawable( R.drawable.good_morning ) );
        } else if (timeOfDay >= 16 && timeOfDay < 21) {
            constraintLayout.setBackground( getDrawable( R.drawable.good_morning ) );
        } else if (timeOfDay >= 21 && timeOfDay < 24) {
            constraintLayout.setBackground( getDrawable( R.drawable.good_night ) );

        }
        myListView = (ListView) findViewById( R.id.myListView );
        Resources resources = getResources();
        characters = resources.getStringArray( R.array.characters );
        CharactersAdapter charactersAdapter = new CharactersAdapter( this, characters, descriptions, drawableIds );
        myListView.setAdapter( charactersAdapter );

        AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                final DocumentReference locationRef = FirebaseFirestore.getInstance().collection( getString( R.string.collection_users ) )
                        .document( FirebaseAuth.getInstance().getCurrentUser().getUid() );
                locationRef.get().addOnCompleteListener( new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            User newUser = task.getResult().toObject( User.class );
                            if (i == 0) {
                                newUser.setAvatar( R.drawable.bloomsday1 + "" );
                            } else if (i == 1) {
                                newUser.setAvatar( R.drawable.bloomsday2 + "" );
                            } else if (i == 2) {
                                newUser.setAvatar( R.drawable.bloomsday3 + "" );
                            }
                            locationRef.set( newUser );

                            Intent toMapActivity = new Intent( getApplicationContext(), MapsActivity.class );
                            startActivity( toMapActivity );
                        } else
                            Log.d( TAG, "Error when adding a photo" );
                    }
                } );
            }
        };

        myListView.setOnItemClickListener( itemClickListener );

        Toolbar toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );
        getSupportActionBar().hide();


    }

}
