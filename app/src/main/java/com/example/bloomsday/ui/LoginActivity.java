package com.example.bloomsday.ui;

import android.content.Intent;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.drawable.IconCompat;

import com.example.bloomsday.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginPage";
    private FirebaseAuth mAuth;
    private EditText email, password;
    private Button email_log_in_button;
    private TextView register, forget_password;
    private ProgressBar progressBarLogin;
    private FloatingActionButton messageLoginActivity;
    private ConstraintLayout constraintLayout;
    private TextView greeting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_login_activity );
        Toolbar toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );
        greeting = (TextView) findViewById( R.id.greeting );
        email_log_in_button = (Button) findViewById( R.id.email_log_in_button );
        constraintLayout = findViewById( R.id.LoginActivity );
        Calendar gregorianCalendar = Calendar.getInstance();
        int timeOfDay = gregorianCalendar.get( GregorianCalendar.HOUR_OF_DAY );
        if (timeOfDay >= 0 && timeOfDay < 16) {
            constraintLayout.setBackground( getDrawable( R.drawable.good_morning ) );
            greeting.setText( R.string.goodMorning );
            email_log_in_button.setBackground( getDrawable( R.drawable.day_time_custom_button ) );
        } else if (timeOfDay >= 16 && timeOfDay < 21) {
            constraintLayout.setBackground( getDrawable( R.drawable.good_morning ) );
            greeting.setText( R.string.goodAfternoon );
            email_log_in_button.setBackground( getDrawable( R.drawable.day_time_custom_button ) );
        } else if (timeOfDay >= 21 && timeOfDay < 24) {
            constraintLayout.setBackground( getDrawable( R.drawable.good_night ) );
            greeting.setText( R.string.goodNight );
            email_log_in_button.setBackground( getDrawable( R.drawable.night_time_custom_button ) );
        }
        mAuth = FirebaseAuth.getInstance();
        email = (EditText) findViewById( R.id.email );
        password = (EditText) findViewById( R.id.password );
        register = (TextView) findViewById( R.id.register );
        forget_password = (TextView) findViewById( R.id.forget_password );
        progressBarLogin = (ProgressBar) findViewById( R.id.progressBarLogin );
        messageLoginActivity = (FloatingActionButton) findViewById( R.id.messageLoginActivity );
        email_log_in_button.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBarLogin.setVisibility( View.VISIBLE );
                final String mEmail = email.getText().toString();
                final String mPass = password.getText().toString();
                if (!mEmail.equals( "" ) && !mPass.equals( "" )) {
                    mAuth.signInWithEmailAndPassword( mEmail, mPass ).addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressBarLogin.setVisibility( View.GONE );
                            if (task.isSuccessful()) {
                                if (mAuth.getCurrentUser().isEmailVerified()) {
                                    toastMessage( "Login successful!" );
                                    startActivity( new Intent( getApplicationContext(), ChooseCharacterActivity.class ) );
                                } else {
                                    toastMessage( "Please check your email address" );
                                }
                            } else {
                                toastMessage( "Login failed! Please try again later" );
                                email.setText( "" );
                                password.setText( "" );
                            }
                        }
                    } );
                } else {
                    toastMessage( "You did not fill in all the fields" );
                }
            }
        } );

        register.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent( getApplicationContext(), com.example.bloomsday.ui.RegisterActivity.class );
                startActivity( intent );
            }
        } );

        forget_password.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity( new Intent( getApplicationContext(), com.example.bloomsday.ui.ResetPasswordActivity.class ) );
            }
        } );
        messageLoginActivity.setTooltipText( "Check my email" );
        messageLoginActivity.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String yahoo = "https://mail.yahoo.com";
                Uri webAddress = Uri.parse( yahoo );
                Intent checkEmail = new Intent( Intent.ACTION_VIEW, webAddress );
                startActivity( checkEmail );
            }
        } );
        getSupportActionBar().hide();
    }

    public void toastMessage(String message) {
        Toast.makeText( this, message, Toast.LENGTH_SHORT ).show();
    }



}
