package com.example.chatapp;

import android.graphics.Color;
import android.media.Image;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessagesViewHolder>

{
        private List<Messages> userMessagesList;
        private FirebaseAuth auth;
        private DatabaseReference UsersDBReference;

    public MessageAdapter(List<Messages> userMessagesList)
    {
            this.userMessagesList = userMessagesList;
    }

    @NonNull
    @Override
    public MessagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.messages_layout_design,parent,false);
        auth = FirebaseAuth.getInstance();
        return new MessagesViewHolder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull MessagesViewHolder holder, int position)
    {
            String message_sender_ID = auth.getCurrentUser().getUid();
            Messages messages = userMessagesList.get(position);

            String fromUserID = messages.getFrom();
            String fromMessageType = messages.getType();

            UsersDBReference = FirebaseDatabase.getInstance().getReference().child("Users").child(fromUserID);

            //veri okuma
            UsersDBReference.addValueEventListener(new ValueEventListener() {


                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String userName =  snapshot.child("user_name").getValue().toString();
                    String userImage = snapshot.child("user_image").getValue().toString();

                    /* Picasso.get().load(userImage).placeholder(R.drawable.defaultpic).into(holder.userProfileImage); */

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            if(fromMessageType.equals("text"))
            {
                holder.messageSendPicture.setVisibility(View.INVISIBLE);
                //Gönderici tarafı
                if(fromUserID.equals(message_sender_ID))
                {
                    holder.messageText.setBackgroundResource(R.drawable.message_background_second);
                    holder.messageText.setTextColor(Color.WHITE);
                    holder.messageText.setGravity(Gravity.RIGHT);
                }
                else
                {   //Alıcı tarafı
                    holder.messageText.setBackgroundResource(R.drawable.message_text_background);
                    holder.messageText.setTextColor(Color.WHITE);
                    holder.messageText.setGravity(Gravity.LEFT);
                }

                holder.messageText.setText(messages.getMessage());
            }
            else
                {   holder.messageText.setVisibility(View.INVISIBLE);
                    holder.messageText.setPadding(0,0,0,0);


                    Picasso.get().load(messages.getMessage()).placeholder(R.drawable.defaultpic).into(holder.messageSendPicture);
            }


    }

    @Override
    public int getItemCount() {
        return userMessagesList.size();
    }




    public class MessagesViewHolder extends RecyclerView.ViewHolder
            {
                    public TextView messageText;
                    public CircleImageView userProfileImage;
                    public ImageView messageSendPicture;

                    public MessagesViewHolder(View view)
                    {
                            super(view);

                            messageText = (TextView) view.findViewById(R.id.message_Text);
                            messageSendPicture = (ImageView) view.findViewById(R.id.messageImageView);
                            /* userProfileImage = (CircleImageView) view.findViewById(R.id.messages_Profile_Image); */

                    }

            }
            }


