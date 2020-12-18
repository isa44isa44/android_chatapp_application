package com.example.chatapp;
//Giriş aktivitesi
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ToolbarWidgetWrapper;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.widget.TooltipCompat;

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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

            private Toolbar actionbarLogin;
            private EditText txtEmail, txtPassword;
            private Button btnLogin;

            // veritabanı(firebase) implementi
            private FirebaseAuth auth;
            private FirebaseUser currentUser;


            public void init() {

                actionbarLogin = findViewById(R.id.actionbarLogin);
                setSupportActionBar(actionbarLogin);
                getSupportActionBar().setTitle("Giriş Yap");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);

                auth = FirebaseAuth.getInstance();
                currentUser =  auth.getCurrentUser();

                txtEmail = findViewById(R.id.txtEmailLogin);
                txtPassword = findViewById(R.id.txtPasswordLogin);
                btnLogin = findViewById(R.id.btnLogin);

            }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
            }


    private void loginUser() {
                String email = txtEmail.getText().toString();
                String password = txtPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(this, "Email alanı boş olamaz!",Toast.LENGTH_LONG).show();

                } else if (TextUtils.isEmpty(password)) {
                    Toast.makeText(this, "Şifre alanı boş olamaz!",Toast.LENGTH_LONG).show();
        } else {
                    auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, "Başarıyla giriş yapıldı!",Toast.LENGTH_LONG).show();
                                Intent mainIntent = new Intent(LoginActivity.this,MainActivity2.class);
                                startActivity(mainIntent);

                            } else {
                                Toast.makeText(LoginActivity.this, "Giriş başarısız!",Toast.LENGTH_LONG).show();
                            }

                        }
                    });
                }
}
}