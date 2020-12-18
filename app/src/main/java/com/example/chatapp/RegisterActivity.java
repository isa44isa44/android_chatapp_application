package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

        private Toolbar actionbarRegister;
        private EditText txtUsername, txtEmail, txtPassword;
        private Button btnRegister;

        private FirebaseAuth auth;
        private DatabaseReference storeUserDefaultDataReference;


        public void init() {
            actionbarRegister = findViewById(R.id.actionbarRegister);
            setSupportActionBar(actionbarRegister);
            getSupportActionBar().setTitle("Hesap Oluştur");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            auth = FirebaseAuth.getInstance();

            txtUsername = findViewById(R.id.txtRegisterUsername);
            txtEmail = findViewById(R.id.txtRegisterEmail);
            txtPassword = findViewById(R.id.txtRegisterPassword);
            btnRegister = findViewById(R.id.btnRegister);


        }

        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();
        btnRegister.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewAcc();

            }
        }));

        }

    private void createNewAcc() {
            String username = txtUsername.getText().toString();
            String email = txtEmail.getText().toString();
            String password = txtPassword.getText().toString();


        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "Kullanıcı adı alanı boş olamaz!",Toast.LENGTH_LONG).show(); }
        else if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Email alanı boş olamaz!", Toast.LENGTH_LONG).show();
        }
        else if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "Kullanıcı adı alanı boş olamaz!",Toast.LENGTH_LONG).show(); }
        else if (TextUtils.isEmpty(password)) {
            Toast.makeText( this, "Şifre alanı boş olamaz!",Toast.LENGTH_LONG).show();
        }

        else {
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this, "Hesabınız başarılı bir şekilde oluşturulmuştur!", Toast.LENGTH_LONG).show();

                        /*Veritabanına veri oluşturuyorum */
                       String current_user_Id = auth.getCurrentUser().getUid();
                        storeUserDefaultDataReference = FirebaseDatabase.getInstance().getReference().child("Users").child(current_user_Id);
                        storeUserDefaultDataReference.child("user_name").setValue(username);
                        storeUserDefaultDataReference.child("user_status").setValue("Boss is using Chatapp.Developed by Alp Bereket");
                        storeUserDefaultDataReference.child("user_image").setValue("default_profile");
                        storeUserDefaultDataReference.child("user_thumb_image").setValue("default_image")
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()) {
                                            /* Kullanıcı kaydını oluşturduktan sonra otomatik login activity'e yönlendirilsin */
                                        Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                                            startActivity(loginIntent);
                                                finish();
                                        }
                                    }
                                });

                    } else {
                        Toast.makeText(RegisterActivity.this,"Hata oluştu", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

}


