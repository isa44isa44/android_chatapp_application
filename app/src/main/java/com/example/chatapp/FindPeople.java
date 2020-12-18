package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindPeople extends AppCompatActivity {

    private Toolbar appToolbar;
    private RecyclerView findUserList;
    private EditText searchInputText;
    private ImageButton searchButton;
    private DatabaseReference FindUserDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_people);

        appToolbar = findViewById(R.id.find_friends_bar);
        setSupportActionBar(appToolbar);
        getSupportActionBar().setTitle("Tüm kullanıcılar");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        searchButton = (ImageButton) findViewById(R.id.search_ppl_btn);
        searchInputText = (EditText) findViewById(R.id.search_input_text);

        findUserList = findViewById(R.id.all_user_list);
        findUserList.setHasFixedSize(true);
        findUserList.setLayoutManager(new LinearLayoutManager(this));
        FindUserDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users");

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String search_userName = searchInputText.getText().toString();
                if(TextUtils.isEmpty(search_userName))
                {
                    Toast.makeText(FindPeople.this, "Arama çubuğu boş olamaz", Toast.LENGTH_SHORT).show();
                }
                SearchFriends(search_userName);
            }
        });


    }


    private void SearchFriends(String search_userName)
    {
        Toast.makeText(FindPeople.this, "Kullanıcı aranıyor...", Toast.LENGTH_SHORT).show();
        Query searchFriends = FindUserDatabaseReference.orderByChild("user_name").startAt(search_userName).endAt(search_userName + "\uf8ff");


        FirebaseRecyclerAdapter<FindUsers, FindUsersViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<FindUsers, FindUsersViewHolder>
                (
                        FindUsers.class,
                        R.layout.find_users_display_layout,
                        FindUsersViewHolder.class,
                        searchFriends

                )

        {
            @Override
            protected void populateViewHolder(FindUsersViewHolder findUsersViewHolder, FindUsers findUsers, int position)
            {
                    findUsersViewHolder.setUser_name(findUsers.getUser_name());
                    findUsersViewHolder.setUser_status(findUsers.getUser_status());
                    findUsersViewHolder.setUser_image(getApplicationContext(), findUsers.getUser_image());

                    findUsersViewHolder.view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String visit_user_id = getRef(position).getKey();
                            Intent profileIntent = new Intent(FindPeople.this,ProfileActivity.class);
                            profileIntent.putExtra("visit_user_id",visit_user_id);
                            startActivity(profileIntent);
                        }
                    });
            }
        };
        findUserList.setAdapter(firebaseRecyclerAdapter);



    }
    public static class FindUsersViewHolder extends RecyclerView.ViewHolder {
        View view;
        public FindUsersViewHolder(@NonNull View itemView)
        {
            super(itemView);
            view = itemView;
        }

            public void setUser_name(String user_name)
            {
                TextView name = view.findViewById(R.id.find_users_username);
                name.setText(user_name);
            }

            public void setUser_status(String user_status)
            {
                TextView status = view.findViewById(R.id.find_users_status);
                status.setText(user_status);
            }

            public void setUser_image(Context ctx, String user_image)
            {
                CircleImageView image = view.findViewById(R.id.find_users_profile_image);
                Picasso.get().load(user_image).placeholder(R.drawable.defaultpic).into(image);

            }

    }

}