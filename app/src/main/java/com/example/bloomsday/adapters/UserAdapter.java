package com.example.bloomsday.adapters;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bloomsday.R;
import com.example.bloomsday.models.Chat;
import com.example.bloomsday.models.User;
import com.example.bloomsday.ui.MessageActivity;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.Text;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private ArrayList<User> users;
    private Context context;
    String theLastMessage;
    boolean isChat;
    String TAG = "UserAdapter";

    public UserAdapter(Context context, ArrayList<User> users, boolean isChat) {
        this.context = context;
        this.users = users;
        this.isChat = isChat;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View userItem = LayoutInflater.from( context ).inflate( R.layout.user_item, parent, false );
        return new UserAdapter.ViewHolder( userItem );
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final User user = users.get( position );
        holder.username.setText( user.getUsername() );
        holder.avatar.setImageResource( Integer.parseInt( user.getAvatar() ) );
        if (isChat) {
            Log.d( TAG, "Display the last conversations" );
            lastMessage( user.getUser_id(), holder.last_msg );
        } else {
            holder.last_msg.setVisibility( View.GONE );
        }
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection( "Users" ).document(user.getUser_id());
        documentReference.get().addOnCompleteListener( new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (isChat){
                    User user1 = task.getResult().toObject( User.class );
                    if (user1.getStatus().toString().equals("online")){
                        holder.img_on.setVisibility(View.VISIBLE);
                        holder.img_off.setVisibility(View.GONE);
                    } else {
                        holder.img_on.setVisibility(View.GONE);
                        holder.img_off.setVisibility(View.VISIBLE);
                    }
                } else {
                    holder.img_on.setVisibility(View.GONE);
                    holder.img_off.setVisibility(View.GONE);
                }
            }
        } );

        holder.itemView.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent( context, MessageActivity.class );
                intent.putExtra( "userid", user.getUser_id() );
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(
                        (Activity) context,
                        Pair.create( view, "imageTransition" ) );
                context.startActivity( intent, options.toBundle() );
            }
        } );
    }


    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView avatar;
        private TextView username;
        private TextView last_msg;
        private ImageView img_on;
        private ImageView img_off;

        public ViewHolder(View itemView) {
            super( itemView );

            avatar = (CircleImageView) itemView.findViewById( R.id.profilePicture );
            username = (TextView) itemView.findViewById( R.id.profileUsername );
            last_msg = (TextView) itemView.findViewById( R.id.last_msg );
            img_on = itemView.findViewById( R.id.img_on );
            img_off = itemView.findViewById( R.id.img_off );

        }
    }

    private void lastMessage(final String userid, final TextView last_msg) {
        theLastMessage = "default";
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Query reference = FirebaseFirestore.getInstance().collection( "ChatMessages" ).orderBy( "timestamp" );
        reference.get().addOnCompleteListener( new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d( TAG, "Iteration through the documents :" );
                        Chat chat = new Chat( document.get( "sender" ).toString(),
                                document.get( "receiver" ).toString(), document.get( "message" ).toString() );
                        if (firebaseUser != null && chat != null) {
                            if (chat.getReceiver().equals( firebaseUser.getUid() ) && chat.getSender().equals( userid ))
                                theLastMessage = chat.getMessage();

                            if (chat.getReceiver().equals( userid ) && chat.getSender().equals( firebaseUser.getUid() ))
                                theLastMessage = "You: " + chat.getMessage();
                                Log.d( TAG, "The last message is: " + theLastMessage );

                        }

                    }
                    switch (theLastMessage) {
                        case "default":
                            last_msg.setText( "default" );
                            break;

                        default:
                            last_msg.setText( theLastMessage );
                            break;
                    }

                } else {
                    Log.d( TAG, "Error getting documents : ", task.getException() );
                }
            }
        } );
    }
}

