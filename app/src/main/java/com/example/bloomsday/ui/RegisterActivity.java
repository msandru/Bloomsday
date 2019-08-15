package com.example.bloomsday.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.example.bloomsday.models.User;

import com.example.bloomsday.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterPage";
    private FirebaseAuth mAuth;
    private EditText passwordRegister, emailRegister, confirmPassword, userName;
    private Button email_sign_up_button;
    private ProgressBar progressBarRegister;
    private FirebaseFirestore mFirebaseDataBase;
    private FloatingActionButton messageRegisterActivity;
    private ConstraintLayout constraintLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_register_activity );
        Toolbar toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );
        constraintLayout = findViewById( R.id.RegisterActivity );
        email_sign_up_button = (Button) findViewById( R.id.email_sign_up_button );
        Calendar gregorianCalendar = Calendar.getInstance();
        int timeOfDay = gregorianCalendar.get( GregorianCalendar.HOUR_OF_DAY );
        if (timeOfDay >= 0 && timeOfDay < 16) {
            constraintLayout.setBackground( getDrawable( R.drawable.good_morning ) );
            email_sign_up_button.setBackground( getDrawable( R.drawable.day_time_custom_button ) );
        } else if (timeOfDay >= 16 && timeOfDay < 21) {
            constraintLayout.setBackground( getDrawable( R.drawable.good_morning ) );
            email_sign_up_button.setBackground( getDrawable( R.drawable.day_time_custom_button ) );
        } else if (timeOfDay >= 21 && timeOfDay < 24) {
            constraintLayout.setBackground( getDrawable( R.drawable.good_night ) );
            email_sign_up_button.setBackground( getDrawable( R.drawable.night_time_custom_button ) );
        }
        mAuth = FirebaseAuth.getInstance();
        emailRegister = (EditText) findViewById( R.id.emailRegister );
        passwordRegister = (EditText) findViewById( R.id.passwordRegister );
        confirmPassword = (EditText) findViewById( R.id.confirmPassword );
        progressBarRegister = (ProgressBar) findViewById( R.id.progressBarRegister );
        mFirebaseDataBase = FirebaseFirestore.getInstance();
        messageRegisterActivity = (FloatingActionButton) findViewById( R.id.messageRegisterActivity );

        messageRegisterActivity.setTooltipText( "Check my email" );
        messageRegisterActivity.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String yahoo = "https://mail.yahoo.com";
                Uri webAddress = Uri.parse( yahoo );
                Intent checkEmail = new Intent( Intent.ACTION_VIEW, webAddress );
                startActivity( checkEmail );
            }
        } );
        email_sign_up_button.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBarRegister.setVisibility( View.VISIBLE );
                final String mEmail = emailRegister.getText().toString();
                String mPass = passwordRegister.getText().toString();
                String mConfirm = confirmPassword.getText().toString();
                if (!mConfirm.equals( mPass )) {
                    toastMessage( "Passwords do not match" );
                }
                if (!mEmail.equals( "" ) && !mPass.equals( "" ) && mConfirm.equals( mPass )) {
                    mAuth.createUserWithEmailAndPassword( mEmail, mPass ).addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                User user = new User();
                                user.setEmail( mEmail );
                                user.setUsername( mEmail.substring( 0, mEmail.indexOf( "@" ) ) );
                                user.setUser_id( mAuth.getCurrentUser().getUid() );
                                FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder().
                                        setTimestampsInSnapshotsEnabled( true )
                                        .build();
                                mFirebaseDataBase.setFirestoreSettings( settings );
                                DocumentReference newUserRef = mFirebaseDataBase
                                        .collection( getString( R.string.collection_users ) )
                                        .document( mAuth.getCurrentUser().getUid() );

                                newUserRef.set( user ).addOnCompleteListener( new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d( TAG, "The user has been successfully added into the database" );
                                            mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener( new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        progressBarRegister.setVisibility( View.GONE );
                                                        toastMessage( "Registration successful!. Please check your email for verification" );
                                                        Intent intent = new Intent( getApplicationContext(), LoginActivity.class );
                                                        startActivity( intent );
                                                    } else {
                                                        toastMessage( task.getException().getMessage() );
                                                    }
                                                }
                                            } );

                                        } else {
                                            Log.d( TAG, "Something went wrong." );
                                        }
                                    }
                                } );
                            }
                        }
                    } );
                } else {
                    toastMessage( "You did not fill in all the fields" );
                }
            }
        } );
        getSupportActionBar().hide();
    }

    public void toastMessage(String message) {
        Toast.makeText( this, message, Toast.LENGTH_SHORT ).show();
    }
}
