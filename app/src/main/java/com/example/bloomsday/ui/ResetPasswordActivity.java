package com.example.bloomsday.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.example.bloomsday.R;
import com.example.bloomsday.ui.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class ResetPasswordActivity extends AppCompatActivity {
    private static final String TAG = "ResetPasswordActivity";
    private FirebaseAuth mAuth;
    private EditText emailReset;
    private Button passwordReset;
    private FloatingActionButton messageResetActivity;
    private ConstraintLayout constraintLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_reset_password_activity );
        Toolbar toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );

        constraintLayout = findViewById( R.id.ResetPasswordActivity);
        Calendar gregorianCalendar = Calendar.getInstance();
        int timeOfDay = gregorianCalendar.get( GregorianCalendar.HOUR_OF_DAY );
        if (timeOfDay >= 0 && timeOfDay < 16) {
            constraintLayout.setBackground( getDrawable( R.drawable.good_morning ) );

        } else if (timeOfDay >= 16 && timeOfDay < 21) {
            constraintLayout.setBackground( getDrawable( R.drawable.good_morning ) );

        } else if (timeOfDay >= 21 && timeOfDay < 24) {
            constraintLayout.setBackground( getDrawable( R.drawable.good_night ) );

        }
        mAuth = FirebaseAuth.getInstance();
        emailReset = (EditText) findViewById( R.id.emailReset );
        passwordReset = (Button) findViewById( R.id.passwordReset );

        messageResetActivity = (FloatingActionButton) findViewById( R.id.messageResetActivity );

        messageResetActivity.setTooltipText( "Check my email" );
        messageResetActivity.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String yahoo = "https://mail.yahoo.com";
                Uri webAddress = Uri.parse( yahoo );
                Intent checkEmail = new Intent( Intent.ACTION_VIEW, webAddress );
                startActivity( checkEmail );
            }
        } );
        passwordReset.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!emailReset.getText().toString().equals( "" )) {
                    mAuth.sendPasswordResetEmail( emailReset.getText().toString() ).addOnCompleteListener( new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                toastMessage( "Password successfully changed" );
                                startActivity( new Intent( getApplicationContext(), LoginActivity.class ) );
                            } else {
                                toastMessage( "An error occured" );
                            }
                        }
                    } );
                } else {
                    toastMessage( "Please enter a valid email address" );
                }
            }
        } );
        getSupportActionBar().hide();
    }

    public void toastMessage(String message) {
        Toast.makeText( this, message, Toast.LENGTH_SHORT ).show();
    }
}
