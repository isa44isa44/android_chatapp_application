package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.os.IResultReceiver;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.core.SyncTree;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Result;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {


    private String messageAlanID;
    private String messageReceiverName;
    private String messageReceiverImage;

    private ImageButton SelectImageButton;
    private ImageButton SendMessageButton;
    private EditText InputMessageText;


    private Toolbar ChatToolBar;

    private TextView userNameTitle;
    private CircleImageView userChatProfileImage;
    private DatabaseReference rootRef;
    private StorageReference imageStoreRef;
    private ProgressDialog loadingBar;

    private FirebaseAuth auth;
    private String messageGonderenID;

    private RecyclerView userMessagesListView;
    private final List<Messages> messagesList = new ArrayList<>();

    private LinearLayoutManager linearLayoutManager;

    private MessageAdapter messageAdapter;

    private static int Gallery_Pick = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        rootRef = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        messageGonderenID = auth.getCurrentUser().getUid();

        messageAlanID = getIntent().getExtras().get("visit_user_id").toString();
        messageReceiverName = getIntent().getExtras().get("user_name").toString();
        messageReceiverImage = getIntent().getExtras().get("visit_user_image").toString();
        imageStoreRef = FirebaseStorage.getInstance().getReference().child("Messages_Pictures");

        init();


        userNameTitle.setText(messageReceiverName);
        Picasso.get().load(messageReceiverImage).placeholder(R.drawable.defaultpic).into(userChatProfileImage);

        SendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendMessage();
            }
        });

        SelectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, Gallery_Pick);
            }
        });

    }


    private void SendMessage() {
        String messageText = InputMessageText.getText().toString();
        if (TextUtils.isEmpty(messageText)) {
            Toast.makeText(ChatActivity.this, "Lütfen mesaj yazınız!", Toast.LENGTH_SHORT).show();

        } else {
            String message_gonderen_Ref = "Messages/" + messageGonderenID + "/" + messageAlanID;
            String message_alan_Ref = "Messages/" + messageAlanID + "/" + messageGonderenID;

            DatabaseReference user_message_key = rootRef.child("Messages").child(messageGonderenID).child(messageAlanID).push();
            String message_push_id = user_message_key.getKey();

            Map messageData = new HashMap();
            messageData.put("message", messageText);
            messageData.put("type", "text");
            messageData.put("from", messageGonderenID);
            messageData.put("time", ServerValue.TIMESTAMP);


            Map messageDataDetails = new HashMap();
            messageDataDetails.put(message_gonderen_Ref + "/" + message_push_id, messageData);
            messageDataDetails.put(message_alan_Ref + "/" + message_push_id, messageData);

            rootRef.updateChildren(messageDataDetails, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                    if (error != null) {
                        Log.d("Chat_Log", error.getMessage().toString());
                    }

                    InputMessageText.setText("");
                }
            });

        }

    }


    private void init() {
        ChatToolBar = (Toolbar) findViewById(R.id.chat_bar_layout);
        setSupportActionBar(ChatToolBar);
        ActionBar actionBar = getSupportActionBar();

        loadingBar = new ProgressDialog(this);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater layoutInflater = (LayoutInflater)
                this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_Bar_View = layoutInflater.inflate(R.layout.chat_custom_bar, null);
        actionBar.setCustomView(action_Bar_View);

        userNameTitle = (TextView) findViewById(R.id.textViewCustomProfile);
        userChatProfileImage = (CircleImageView) findViewById(R.id.custom_profile_image);

        SendMessageButton = (ImageButton) findViewById(R.id.imageSendButtonCustom);
        SelectImageButton = (ImageButton) findViewById(R.id.select_image_custom);
        InputMessageText = (EditText) findViewById(R.id.edit_Text_Custom);

        messageAdapter = new MessageAdapter(messagesList);
        userMessagesListView = (RecyclerView) findViewById(R.id.messages_ListView_Users);

        linearLayoutManager = new LinearLayoutManager(this);

        userMessagesListView.setHasFixedSize(true);
        userMessagesListView.setLayoutManager(linearLayoutManager);
        userMessagesListView.setAdapter(messageAdapter);

        FetchMessages();

    }


    private void FetchMessages() {
        rootRef.child("Messages").child(messageGonderenID).child(messageAlanID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Messages messages = snapshot.getValue(Messages.class);
                messagesList.add(messages);
                messageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Gallery_Pick && resultCode == RESULT_OK && data != null) {
            loadingBar.setTitle("Resim gönderiliyor");
            loadingBar.setMessage("Lütfen bekleyiniz!");
            loadingBar.show();

            super.onActivityResult(requestCode, resultCode, data);
            Uri ImageUri = data.getData();
            final String message_gonderen_Ref = "Messages/" + messageGonderenID + "/" + messageAlanID;
            final String message_alan_Ref = "Messages/" + messageAlanID + "/" + messageGonderenID;


            DatabaseReference user_message_key = rootRef.child("Messages").child(messageGonderenID).child(messageAlanID).push();
                final String message_push_id = user_message_key.getKey();
                StorageReference filePath = imageStoreRef.child(message_push_id + ".jpg");

                filePath.putFile(ImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                    {
                        final Task<Uri> firebaseUri = taskSnapshot.getStorage().getDownloadUrl();
                        firebaseUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri)
                            {
                                final String downloadUrl = uri.toString();

                                Map messageData = new HashMap();
                                messageData.put("message", downloadUrl);
                                messageData.put("type", "image");
                                messageData.put("time", ServerValue.TIMESTAMP);
                                messageData.put("from", messageGonderenID);


                                Map messageDataDetails = new HashMap();

                                messageDataDetails.put(message_gonderen_Ref + "/" + message_push_id, messageData);
                                messageDataDetails.put(message_alan_Ref + "/" + message_push_id, messageData);
                                Toast.makeText(ChatActivity.this,"Resim başarıyla gönderildi!",Toast.LENGTH_LONG).show();

                                rootRef.updateChildren(messageDataDetails, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                        if (error != null) {
                                            Log.d("Chat_Log", error.getMessage().toString());
                                        }

                                        InputMessageText.setText("");
                                        loadingBar.dismiss();

                                    }
                                });
                            }
                        });
                    }
                });

            }
        }
    }

