package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.widget.ToolbarWidgetWrapper;
import androidx.appcompat.widget.TooltipCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import javax.net.ssl.SSLEngineResult;

public class StatusActivity extends AppCompatActivity {
    private Toolbar appToolbar;
    private Button  SaveChangesButton;
    private EditText StatusInput;

    private DatabaseReference changeStatusRef;
    private FirebaseAuth auth;
    private ProgressDialog loadingBar;

        public void init() {
            auth = FirebaseAuth.getInstance();
            String user_id = auth.getCurrentUser().getUid();
            changeStatusRef = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);


            SaveChangesButton = findViewById(R.id.save_the_changes_button);
            StatusInput = findViewById(R.id.editTextTextMultiLine);
            loadingBar = new ProgressDialog(this);
        }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        appToolbar = findViewById(R.id.appBarLayout1);
        setSupportActionBar(appToolbar);
        getSupportActionBar().setTitle("Durumu değiştir");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        init();


        /* auth = FirebaseAuth.getInstance();
        String user_id = auth.getCurrentUser().getUid();
        changeStatusRef = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);

        appToolbar = (Toolbar) findViewById(R.id.status_app_bar);
        setSupportActionBar(appToolbar);
        getSupportActionBar().setTitle("Durumu değiştir");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SaveChangesButton = (Button) findViewById(R.id.save_the_changes_button);
        StatusInput = (EditText) findViewById(R.id.editTextTextMultiLine);
        loadingBar = new ProgressDialog(this); */

        SaveChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String new_status = StatusInput.getText().toString();

                ChangeProfileStatus(new_status);

            }
        });
    }


    private void ChangeProfileStatus(String new_status) {
        if (TextUtils.isEmpty(new_status)) {
            Toast.makeText(StatusActivity.this, "Durum yazınız!", Toast.LENGTH_LONG).show();
        }
        else {
            loadingBar.setTitle("Durumu değiştir");
            loadingBar.setMessage("Durumunuz güncellenirken lütfen bekleyiniz..");
            loadingBar.show();


            changeStatusRef.child("user_status").setValue(new_status)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                loadingBar.dismiss();
                                Intent settingsIntent = new Intent(StatusActivity.this, SettingsActivity.class);
                                startActivity(settingsIntent);
                                Toast.makeText(StatusActivity.this, "Durumunuz başarıyla güncellendi", Toast.LENGTH_SHORT).show();
                            }
                                    else
                                        {
                                Toast.makeText(StatusActivity.this, "Hata meydana geldi!", Toast.LENGTH_LONG).show();

                            }
                        }


                    });


        }
    } }