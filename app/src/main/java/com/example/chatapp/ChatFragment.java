package com.example.chatapp;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatFragment extends Fragment
{
    private RecyclerView myChatsList;
    private DatabaseReference FriendsReference;
    private DatabaseReference UsersReference;
    private FirebaseAuth auth;
    String current_user_Id;


    private View myMainView;




    public ChatFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        myMainView = inflater.inflate(R.layout.fragment_chat, container, false);

        myChatsList = (RecyclerView) myMainView.findViewById(R.id.chats_list);

        auth = FirebaseAuth.getInstance();
        current_user_Id = auth.getCurrentUser().getUid();
        FriendsReference = FirebaseDatabase.getInstance().getReference().child("Friends").child(current_user_Id);
        UsersReference = FirebaseDatabase.getInstance().getReference().child("Users");

        myChatsList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        myChatsList.setLayoutManager(linearLayoutManager);

        // Inflate the layout for this fragment
        return myMainView;
    }



    @Override
    public void onStart()
    {
        super.onStart();

        FirebaseRecyclerAdapter<Chats, ChatFragment.ChatViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Chats, ChatFragment.ChatViewHolder>
                (
                        Chats.class,
                        R.layout.find_users_display_layout,
                        ChatFragment.ChatViewHolder.class,
                        FriendsReference
                )

        {
            @Override
            protected void populateViewHolder(ChatFragment.ChatViewHolder ChatViewHolder, Chats chats, int position)
            {

                String list_user_id = getRef(position).getKey();

                //database veri okuma
                UsersReference.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot)
                    {
                        String userName = snapshot.child("user_name").getValue().toString();
                        String pImage = snapshot.child("user_image").getValue().toString();
                        String userStatus = snapshot.child("user_status").getValue().toString();

                        ChatViewHolder.setUserName(userName);
                        ChatViewHolder.setPImage(pImage);

                        ChatViewHolder.setUserStatus(userStatus);

                        ChatViewHolder.view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v)
                            {
                                Intent chatIntent = new Intent (getContext(),ChatActivity.class);
                                chatIntent.putExtra("visit_user_id",list_user_id);
                                chatIntent.putExtra("user_name",userName);
                                chatIntent.putExtra("visit_user_image",pImage);
                                startActivity(chatIntent);
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        };

        myChatsList.setAdapter(firebaseRecyclerAdapter);
    }



    public static class ChatViewHolder extends RecyclerView.ViewHolder
    {
        View view;
        public ChatViewHolder(@NonNull View itemView)
        {
            super(itemView);
            view = itemView;
        }

        public void setUserName(String userName)
        {
            TextView userNameDisplay = (TextView) view.findViewById(R.id.find_users_username);
            userNameDisplay.setText(userName);
            userNameDisplay.setTextColor(Color.WHITE);
            userNameDisplay.setTextSize(20);
            userNameDisplay.setTypeface(Typeface.DEFAULT_BOLD);
        }
        public void setPImage(String pImage)
        {
            CircleImageView image = view.findViewById(R.id.find_users_profile_image);
            Picasso.get().load(pImage).placeholder(R.drawable.defaultpic).into(image);
        }

        public void setUserStatus(String userStatus) {
            TextView user_status = (TextView) view.findViewById(R.id.find_users_status);
            user_status.setText(userStatus);
            user_status.setTextColor(Color.YELLOW);
            user_status.setTextSize(15);
            user_status.setTypeface(Typeface.DEFAULT_BOLD);
        }
    }
}