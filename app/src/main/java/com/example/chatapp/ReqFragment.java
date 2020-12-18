package com.example.chatapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;


public class ReqFragment extends Fragment {

    private RecyclerView myReqListView;


    private DatabaseReference FriendsReqReference;
    private DatabaseReference UsersReqReference;

    private DatabaseReference FriendsDatabaseRef;
    private DatabaseReference FriendsReqDatabaseRef;

    private FirebaseAuth auth;


    private View myMainView;


    String current_user_Id;


    public ReqFragment() {

    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
            myMainView = inflater.inflate(R.layout.fragment_req, container, false);
            myReqListView = (RecyclerView) myMainView.findViewById(R.id.req_Recycler_list);


            auth = FirebaseAuth.getInstance();

            FirebaseUser currentUser = auth.getCurrentUser();

            if(currentUser == null) {
            Intent mainIntent = new Intent(getContext(), MainActivity.class);
            startActivity(mainIntent);
            }

            current_user_Id = auth.getCurrentUser().getUid();


            FriendsReqReference = FirebaseDatabase.getInstance().getReference().child("Friend_Request").child(current_user_Id);
            UsersReqReference = FirebaseDatabase.getInstance().getReference().child("Users");

            FriendsDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Friends");
            FriendsReqDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Friend_Request");

            myReqListView.setHasFixedSize(true);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            linearLayoutManager.setReverseLayout(true);
            linearLayoutManager.setStackFromEnd(true);

            myReqListView.setLayoutManager(linearLayoutManager);

            return myMainView;


    }



    @Override
            public void onStart()
                    {
                            super.onStart();


                        FirebaseRecyclerAdapter<Req,ReqViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Req, ReqViewHolder>
                                (
                                        Req.class,
                                        R.layout.req_users_layout,
                                        ReqFragment.ReqViewHolder.class,
                                        FriendsReqReference

                                )

                        {
                            @Override
                            protected void populateViewHolder(ReqViewHolder reqViewHolder, Req recyclerView, int position)
                            {
                                 final String list_users_Id = getRef(position).getKey();
                                DatabaseReference get_type_ref = getRef(position).child("request_type").getRef();

                                // veri okuma
                                get_type_ref.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot)
                                    {

                                        if (snapshot.exists()) {
                                           String request_type = snapshot.getValue().toString();

                                            if (request_type.equals("received"))
                                            {
                                                UsersReqReference.child(list_users_Id).addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot)
                                                    {
                                                        final String userReqName = snapshot.child("user_name").getValue().toString();
                                                        final String pReqImage = snapshot.child("user_image").getValue().toString();
                                                        final String userReqStatus = snapshot.child("user_status").getValue().toString();

                                                        reqViewHolder.setUserName(userReqName);
                                                        reqViewHolder.setProfileImage(pReqImage);
                                                        reqViewHolder.setUser_Status(userReqStatus);

                                                        reqViewHolder.view.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v)
                                                            {
                                                                CharSequence options[] = new CharSequence[]
                                                                        {
                                                                                "Arkadaşlık isteğini Kabul Et",
                                                                                "Arkadaşlık isteğini İptal Et"
                                                                        };

                                                                AlertDialog.Builder builder =  new AlertDialog.Builder(getContext());
                                                                builder.setTitle("Seçenekler");

                                                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialog, int position) {
                                                                        if (position == 0)
                                                                        {
                                                                            Calendar calFordATE = Calendar.getInstance();
                                                                            SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyy");
                                                                            final String saveCurrentDate = currentDate.format(calFordATE.getTime());

                                                                            FriendsDatabaseRef.child(current_user_Id).child(list_users_Id).child("date").setValue(saveCurrentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void aVoid)
                                                                                {
                                                                                    FriendsDatabaseRef.child(list_users_Id).child(current_user_Id).child("date").setValue(saveCurrentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                        @Override
                                                                                        public void onSuccess(Void aVoid)
                                                                                        {
                                                                                            FriendsReqDatabaseRef.child(current_user_Id).child(list_users_Id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                @Override
                                                                                                public void onComplete(@NonNull Task<Void> task)
                                                                                                {
                                                                                                    if(task.isSuccessful())
                                                                                                    {
                                                                                                        FriendsReqDatabaseRef.child(list_users_Id).child(current_user_Id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                            @Override
                                                                                                            public void onComplete(@NonNull Task<Void> task)
                                                                                                            {
                                                                                                                if(task.isSuccessful())
                                                                                                                {
                                                                                                                    Toast.makeText(getContext(), "Arkadaşlık isteği kabul edildi", Toast.LENGTH_SHORT).show();
                                                                                                                }
                                                                                                            }
                                                                                                        });
                                                                                                    }
                                                                                                }
                                                                                            });

                                                                                        }
                                                                                    });
                                                                                }
                                                                            });
                                                                        }
                                                                        if (position == 1)
                                                                        {
                                                                            FriendsReqDatabaseRef.child(current_user_Id).child(list_users_Id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task)
                                                                                {
                                                                                    if(task.isSuccessful())
                                                                                    {
                                                                                        FriendsReqDatabaseRef.child(list_users_Id).child(current_user_Id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<Void> task)
                                                                                            {
                                                                                                if(task.isSuccessful())
                                                                                                {
                                                                                                    Toast.makeText(getContext(), "Arkadaşlık isteği iptal edildi", Toast.LENGTH_SHORT).show();
                                                                                                }
                                                                                            }
                                                                                        });
                                                                                    }
                                                                                }
                                                                            });
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

                                            else if (request_type.equals("sent"))
                                            {
                                                Button req_sent_btn = reqViewHolder.view.findViewById(R.id.req_accept_btn);
                                                req_sent_btn.setText("İstek Gönderildi");

                                                reqViewHolder.view.findViewById(R.id.req_decline_btn).setVisibility(View.INVISIBLE);

                                                UsersReqReference.child(list_users_Id).addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot)
                                                    {
                                                        final String userReqName = snapshot.child("user_name").getValue().toString();
                                                        final String pReqImage = snapshot.child("user_image").getValue().toString();
                                                        final String userReqStatus = snapshot.child("user_status").getValue().toString();

                                                        reqViewHolder.setUserName(userReqName);
                                                        reqViewHolder.setProfileImage(pReqImage);
                                                        reqViewHolder.setUser_Status(userReqStatus);

                                                        reqViewHolder.view.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v)
                                                            {
                                                                CharSequence options[] = new CharSequence[]
                                                                        {
                                                                                "Arkadaşlık isteğini İptal Et"
                                                                        };

                                                                AlertDialog.Builder builder =  new AlertDialog.Builder(getContext());
                                                                builder.setTitle("Seçenekler");

                                                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialog, int position) {

                                                                        if (position == 0)
                                                                        {
                                                                            FriendsReqDatabaseRef.child(current_user_Id).child(list_users_Id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task)
                                                                                {
                                                                                    if(task.isSuccessful())
                                                                                    {
                                                                                        FriendsReqDatabaseRef.child(list_users_Id).child(current_user_Id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<Void> task)
                                                                                            {
                                                                                                if(task.isSuccessful())
                                                                                                {
                                                                                                    Toast.makeText(getContext(), "Arkadaşlık isteği İptal Edildi", Toast.LENGTH_SHORT).show();
                                                                                                }
                                                                                            }
                                                                                        });
                                                                                    }
                                                                                }
                                                                            });
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
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });




                            }
                        };

                        myReqListView.setAdapter(firebaseRecyclerAdapter);
                        firebaseRecyclerAdapter.startListening();

                    }



    public static class ReqViewHolder extends RecyclerView.ViewHolder
        {
            View view;
            public ReqViewHolder(@NonNull View itemView)
            {
                    super(itemView);
                    view = itemView;
            }

            public void setUserName(String userName)
            {
                TextView userNameDisplay = (TextView) view.findViewById(R.id.req_display_name);
                userNameDisplay.setText(userName);
                userNameDisplay.setTextColor(Color.WHITE);
            }

            public void setProfileImage(String profileImage)
            {
                CircleImageView image = view.findViewById(R.id.req_CircleView_Image);
                Picasso.get().load(profileImage).placeholder(R.drawable.defaultpic).into(image);
            }


            public void setUser_Status(String userStatus)
            {
                    TextView reqStatusView = (TextView) view.findViewById(R.id.req_profile_status);
                    reqStatusView.setText(userStatus);
                    reqStatusView.setTextColor(Color.WHITE);

            }

        }


    }

