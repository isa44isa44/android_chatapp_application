package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private Button SendFriendRequestButton;
    private Button DeclineFriendRequestButton;
    private TextView ProfileName;
    private TextView ProfileStatus;
    private ImageView ProfileImage;


    private DatabaseReference UsersReference;

    private String CURRENT_STATE;
    private DatabaseReference FriendRequestReference;
    private FirebaseAuth auth;
    String sender_user_id;
    String receiver_User_Id;

    private DatabaseReference FriendsReference;

    public void btnSet() {
        SendFriendRequestButton = (Button) findViewById(R.id.profile_send_friend_req_button);
        DeclineFriendRequestButton = (Button) findViewById(R.id.profile_decline_friend_req_button);
        ProfileName = (TextView) findViewById(R.id.textViewUsername);
        ProfileStatus = (TextView) findViewById(R.id.textViewUserStatus);
        ProfileImage = (CircleImageView) findViewById(R.id.profile_visit_user_image);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //Database referans alma metodları
        FriendRequestReference = FirebaseDatabase.getInstance().getReference().child("Friend_Request");
        auth = FirebaseAuth.getInstance();
        sender_user_id = auth.getCurrentUser().getUid();
        FriendsReference = FirebaseDatabase.getInstance().getReference().child("Friends");
        UsersReference = FirebaseDatabase.getInstance().getReference().child("Users");
        receiver_User_Id = getIntent().getExtras().get("visit_user_id").toString();
        btnSet();




        //Mevcut Durum belirleme
        CURRENT_STATE = "not_friends";

        //database veri okuma
        UsersReference.child(receiver_User_Id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                String name = (String) snapshot.child("user_name").getValue();
                String status = (String) snapshot.child("user_status").getValue();
                String image  = (String) snapshot.child("user_image").getValue();

                ProfileName.setText(name);
                ProfileStatus.setText(status);
                Picasso.get().load(image).placeholder(R.drawable.defaultpic).into(ProfileImage);

                FriendRequestReference.child(sender_user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot)
                    {
                            if(snapshot.hasChild(receiver_User_Id)) {
                                String req_type = snapshot.child(receiver_User_Id).child("request_type").getValue().toString();

                                if (req_type.equals("sent"))
                                {
                                    CURRENT_STATE = "request_sent";
                                    SendFriendRequestButton.setText("İsteği İptal Et");

                                    DeclineFriendRequestButton.setVisibility(View.INVISIBLE);
                                    DeclineFriendRequestButton.setEnabled(false);
                                }

                                else if(req_type.equals("received"))
                                {
                                    CURRENT_STATE = "request_received";
                                    SendFriendRequestButton.setText("Arkadaşlık isteğini kabul et");

                                    DeclineFriendRequestButton.setVisibility(View.VISIBLE);
                                    DeclineFriendRequestButton.setEnabled(false);

                                    DeclineFriendRequestButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v)
                                        {
                                                DeclineFriendRequest();
                                        }
                                    });
                                }
                            }

                        else
                        {
                            FriendsReference.child(sender_user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot)
                                {
                                    if(snapshot.hasChild(receiver_User_Id))
                                    {
                                        CURRENT_STATE = "friends";
                                        SendFriendRequestButton.setText("Arkadaşlıktan çıkar");

                                        DeclineFriendRequestButton.setVisibility(View.INVISIBLE);
                                        DeclineFriendRequestButton.setEnabled(false);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


       DeclineFriendRequestButton.setVisibility(View.INVISIBLE);
       DeclineFriendRequestButton.setEnabled(false);


        /* Kendime arkadaşlık isteği göndermeyi engelliyorum */
        if(!sender_user_id.equals(receiver_User_Id))
        {
            SendFriendRequestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    SendFriendRequestButton.setEnabled(false);

                    if(CURRENT_STATE.equals("not_friends"))
                    {
                        SendFriendRequestToAPerson();
                    }

                    if(CURRENT_STATE.equals("request_sent"))
                    {
                        CancelFriendRequest();
                    }

                    if(CURRENT_STATE.equals("request_received"))
                    {
                        AcceptFriendRequest();
                    }

                    if(CURRENT_STATE.equals("friends"))
                    {
                        UnFriendAFriend();
                    }

                }
            });
        }
        else
        {
            DeclineFriendRequestButton.setVisibility(View.INVISIBLE);
            SendFriendRequestButton.setVisibility(View.INVISIBLE);
        }

    }

    private void DeclineFriendRequest()
    {
        FriendRequestReference.child(sender_user_id).child(receiver_User_Id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if(task.isSuccessful())
                {
                    FriendRequestReference.child(receiver_User_Id).child(sender_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if(task.isSuccessful())
                            {
                                SendFriendRequestButton.setEnabled(true);
                                CURRENT_STATE = "not_friends";
                                SendFriendRequestButton.setText("Arkadaşlık isteği gönder");
                                DeclineFriendRequestButton.setVisibility(View.INVISIBLE);
                                DeclineFriendRequestButton.setEnabled(false);
                            }
                        }
                    });
                }
            }
        });
    }

    /* Arkadaşlıktan çıkarma methodu */
    private void UnFriendAFriend()
    {
            FriendsReference.child(sender_user_id).child(receiver_User_Id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task)
                {
                    if(task.isSuccessful())
                    {
                        FriendsReference.child(receiver_User_Id).child(sender_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task)
                            {
                                if(task.isSuccessful())
                                {
                                    SendFriendRequestButton.setEnabled(true);
                                    CURRENT_STATE = "not_friends";
                                    SendFriendRequestButton.setText("Arkadaşlık İsteği Gönder");

                                    DeclineFriendRequestButton.setVisibility(View.INVISIBLE);
                                    SendFriendRequestButton.setVisibility(View.INVISIBLE);
                                }
                            }
                        });
                    }
                }
            });
    }

    private void AcceptFriendRequest()
    {
        Calendar calFordATE = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyy");
        final String saveCurrentDate = currentDate.format(calFordATE.getTime());

        FriendsReference.child(sender_user_id).child(receiver_User_Id).child("date").setValue(saveCurrentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid)
            {
                    FriendsReference.child(receiver_User_Id).child(sender_user_id).child("date").setValue(saveCurrentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid)
                        {
                            FriendRequestReference.child(sender_user_id).child(receiver_User_Id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if(task.isSuccessful())
                                    {
                                        FriendRequestReference.child(receiver_User_Id).child(sender_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task)
                                            {
                                                if(task.isSuccessful())
                                                {
                                                    SendFriendRequestButton.setEnabled(true);
                                                    CURRENT_STATE = "friends";
                                                    SendFriendRequestButton.setText("Arkadaşlıktan Çıkar");
                                                    DeclineFriendRequestButton.setVisibility(View.INVISIBLE);
                                                    DeclineFriendRequestButton.setEnabled(false);
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

    private void CancelFriendRequest()
    {
        FriendRequestReference.child(sender_user_id).child(receiver_User_Id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if(task.isSuccessful())
                {
                    FriendRequestReference.child(receiver_User_Id).child(sender_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                                if(task.isSuccessful())
                                {
                                        SendFriendRequestButton.setEnabled(true);
                                        CURRENT_STATE = "not_friends";
                                        SendFriendRequestButton.setText("Arkadaşlık isteği gönder");

                                        DeclineFriendRequestButton.setVisibility(View.INVISIBLE);
                                        DeclineFriendRequestButton.setEnabled(false);
                                }
                        }
                    });
                }
            }
        });

    }

    private void SendFriendRequestToAPerson()
    {
            FriendRequestReference.child(sender_user_id).child(receiver_User_Id).child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task)
                {
                    if(task.isSuccessful())
                    {
                            FriendRequestReference.child(receiver_User_Id).child(sender_user_id).child("request_type").setValue("received").addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if(task.isSuccessful())
                                    {
                                        SendFriendRequestButton.setEnabled(true);
                                        CURRENT_STATE = "request_sent";
                                        SendFriendRequestButton.setText("İsteği İptal Et");

                                        DeclineFriendRequestButton.setVisibility(View.INVISIBLE);
                                        DeclineFriendRequestButton.setEnabled(false);
                                    }

                                }
                            });
                    }
                }
            });

    }
}