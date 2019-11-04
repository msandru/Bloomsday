package com.example.bloomsday.ui;

import android.content.Intent;
import android.os.Bundle;

import com.example.bloomsday.adapters.MessageAdapter;
import com.example.bloomsday.adapters.UserAdapter;
import com.example.bloomsday.fragments.FriendsFragment;
import com.example.bloomsday.models.Chat;
import com.example.bloomsday.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bloomsday.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {
    private CircleImageView profile_image;
    private TextView username;
    private Intent intent;
    private String userid;
    private List<Chat> mchat;
    private ImageButton btn_send, btn_back;
    private EditText text_send;
    private FirebaseUser fuser;
    private String TAG = "MessageActivity";
    private RecyclerView messageRecycleView;
    private int picture;
    private DocumentReference referenceFindingFriend;
    private Query collectionReferenceReadingMessages;
    private MessageAdapter messageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_message );
        Toolbar toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );
        messageRecycleView = (RecyclerView) findViewById( R.id.messageRecycleView );
        text_send = findViewById( R.id.text_send );
        profile_image = findViewById( R.id.messageAvatar );
        username = findViewById( R.id.messageUsername );
        btn_send = findViewById( R.id.btn_send );
        btn_back = findViewById( R.id.btn_back );
        intent = getIntent();
        userid = intent.getStringExtra( "userid" );
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        referenceFindingFriend = FirebaseFirestore.getInstance().collection( getString( R.string.collection_users ) ).
                document( userid );
        Log.d( TAG, "The userid is " + userid );
        messageRecycleView.setHasFixedSize( true );
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager( getApplicationContext() );
        linearLayoutManager.setStackFromEnd( true );
        messageRecycleView.setLayoutManager( linearLayoutManager );
        referenceFindingFriend.get().addOnCompleteListener( new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    User user = task.getResult().toObject( User.class );
                    profile_image.setImageResource( Integer.parseInt( user.getAvatar() ) );
                    picture = Integer.parseInt( user.getAvatar() );
                    username.setText( user.getUsername() );
                    readMesagges( fuser.getUid(), userid, user.getAvatar() );
                }
            }
        } );
        btn_send.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = text_send.getText().toString();
                if (!msg.equals( "" )) {
                    sendMessage( fuser.getUid(), userid, msg );
                    readMesagges( fuser.getUid(), userid, Integer.toString( picture ) );
                } else {
                    Toast.makeText( MessageActivity.this, "You can't send empty message", Toast.LENGTH_SHORT ).show();
                }
                text_send.setText( "" );
            }
        } );

        btn_back.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToUsers = new Intent( MessageActivity.this, ChatActivity.class );
                startActivity( goToUsers );
            }
        } );

        setSupportActionBar( toolbar );

        getSupportActionBar().hide();
    }

    private void sendMessage(String sender, final String receiver, String message) {

        CollectionReference referenceAddingMessage = FirebaseFirestore.getInstance().collection( getString( R.string.collection_chat_messages ) );

        Chat chat = new Chat( sender, receiver, message, false, null );

        referenceAddingMessage.add( chat );


    }

    private void readMesagges(final String myid, final String userid, final String imageurl) {
        mchat = new ArrayList<>();
        collectionReferenceReadingMessages = FirebaseFirestore.getInstance().collection( getString( R.string.collection_chat_messages ) ).orderBy( "timestamp" );
        collectionReferenceReadingMessages.get().addOnCompleteListener( new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Chat chat = new Chat( document.getString( "sender" ), document.getString( "receiver" ), document.getString( "message" ), document.getBoolean( "isseen" ), null );
                        if (chat.getReceiver().equals( myid ) && chat.getSender().equals( userid ) ||
                                chat.getReceiver().equals( userid ) && chat.getSender().equals( myid )) {
                            mchat.add( chat );
                        }

                        messageAdapter = new MessageAdapter( MessageActivity.this, mchat, imageurl );
                        messageRecycleView.setAdapter( messageAdapter );
                    }
                } else {
                    Log.d( TAG, "Error getting documents: ", task.getException() );
                }
            }
        } );

    }



}

