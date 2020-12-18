package com.example.chatapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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


public class FriendsFragment extends Fragment {

    private RecyclerView myFriendList;

    private DatabaseReference FriendsReference;
    private DatabaseReference UsersReference;
    private FirebaseAuth auth;
    String current_user_Id;


    private View myMainView;



    public FriendsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
         myMainView =  inflater.inflate(R.layout.fragment_friends, container, false);
         myFriendList = (RecyclerView) myMainView.findViewById(R.id.friends_list);

         auth = FirebaseAuth.getInstance();
        current_user_Id = auth.getCurrentUser().getUid();

         FriendsReference = FirebaseDatabase.getInstance().getReference().child("Friends").child(current_user_Id);
         UsersReference = FirebaseDatabase.getInstance().getReference().child("Users");

         myFriendList.setLayoutManager(new LinearLayoutManager(getContext()));

         return myMainView;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        FirebaseRecyclerAdapter<Friends, FriendsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>
                (
                        Friends.class,
                        R.layout.find_users_display_layout,
                        FriendsViewHolder.class,
                        FriendsReference
                )

        {
            @Override
            protected void populateViewHolder(FriendsViewHolder friendsViewHolder, Friends friends, int position)
            {
                friendsViewHolder.setDate(friends.getDate());

                String list_user_id = getRef(position).getKey();

                //database veri okuma
                UsersReference.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot)
                    {
                       String userName = snapshot.child("user_name").getValue().toString();
                       String pImage = snapshot.child("user_image").getValue().toString();

                            friendsViewHolder.setUserName(userName);
                            friendsViewHolder.setPImage(pImage);

                            friendsViewHolder.view.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v)
                                {
                                        CharSequence options[] = new CharSequence[]
                                                {
                                                        userName + "'in Profili",
                                                        "Mesaj Gönder"
                                                };

                                    AlertDialog.Builder builder =  new AlertDialog.Builder(getContext());
                                    builder.setTitle("Seçenekler");

                                    builder.setItems(options, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int position) {
                                            if (position == 0)
                                            {
                                                Intent profileIntent = new Intent (getContext(),ProfileActivity.class);
                                                profileIntent.putExtra("visit_user_id",list_user_id);
                                                startActivity(profileIntent);
                                            }
                                            if (position == 1)
                                            {
                                                Intent chatIntent = new Intent (getContext(),ChatActivity.class);
                                                chatIntent.putExtra("visit_user_id",list_user_id);
                                                chatIntent.putExtra("user_name",userName);
                                                chatIntent.putExtra("visit_user_image",pImage);
                                                startActivity(chatIntent);
                                            }

                                        }
                                    });
                                    builder.show();

                                }
                            });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        };

        myFriendList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class FriendsViewHolder extends RecyclerView.ViewHolder
    {
        View view;
        public FriendsViewHolder(@NonNull View itemView)
        {
            super(itemView);
            view = itemView;
        }


        public void setDate(String date)
        {
            TextView FriendsDate = (TextView) view.findViewById(R.id.find_users_status);
            FriendsDate.setText("Şu tarihten beri arkadaşsınız: \n" + date);
            FriendsDate.setTextColor(Color.WHITE);
            FriendsDate.setTextSize(15);

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
    }

}