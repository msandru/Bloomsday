package com.example.bloomsday.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.bloomsday.R;
import com.example.bloomsday.adapters.UserAdapter;
import com.example.bloomsday.models.Chat;
import com.example.bloomsday.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageFragment extends Fragment {


    private RecyclerView recyclerView;
    private String TAG = "MessageFragment";
    private UserAdapter userAdapter;
    private ArrayList<User> mUsers;
    private List<String> usersList;
    FirebaseUser fuser;
    CollectionReference reference;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate( R.layout.fragment_message, container, false );

        recyclerView = view.findViewById( R.id.recycler_view );
        recyclerView.setHasFixedSize( true );
        recyclerView.setLayoutManager( new LinearLayoutManager( getContext() ) );

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        usersList = new ArrayList<>();
        reference = FirebaseFirestore.getInstance().collection( getString( R.string.collection_chat_messages ) );
        reference.get().addOnCompleteListener( new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    usersList.clear();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Chat chat = new Chat( document.get( "sender" ).toString(),
                                document.get( "receiver" ).toString(), document.get( "message" ).toString() );
                        if (chat.getSender().equals( fuser.getUid() )) {
                            usersList.add( chat.getReceiver() );
                        }
                        if (chat.getReceiver().equals( fuser.getUid() )) {
                            usersList.add( chat.getSender() );
                        }
                    }
                    readUsers();
                }

            }
        } );


        return view;
    }


    private void readUsers() {
        CollectionReference reference = FirebaseFirestore.getInstance().
                collection( getString( R.string.collection_users ) );
        mUsers = new ArrayList<>();
        reference.get().addOnCompleteListener( new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    mUsers.clear();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        User user = new User();
                        user.setUsername( document.get( "username" ).toString() );
                        user.setAvatar( document.get( "avatar" ).toString() );
                        user.setUser_id( document.get( "user_id" ).toString() );
                        user.setAvatar( document.get( "avatar" ).toString() );
                        for (String id : usersList) {
                            if (user.getUser_id().equals( id )) {
                                if (mUsers.size() != 0) {
                                    if (!mUsers.contains( user )) {
                                        mUsers.add( user );
                                        Log.d( TAG, TAG + " : The user is " + user.getUsername() );
                                    }
                                } else {
                                    mUsers.add( user );
                                    Log.d( TAG, TAG + ": The user is " + user.getUsername() );
                                }
                            }
                        }
                    }
                    userAdapter = new UserAdapter( getContext(), mUsers, true );
                    recyclerView.setAdapter( userAdapter );
                } else {
                    Log.d( TAG, "Error getting documents: ", task.getException() );
                }
            }
        } );

    }
}
