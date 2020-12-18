package com.example.chatapp;
// Ana sayfa aktivitesi
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private Button btnWelcomeLogin, btnWelcomeRegister;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;

    public void init() {
        btnWelcomeLogin = findViewById(R.id.btnWelcomeLogin);
        btnWelcomeRegister = findViewById(R.id.btnWelcomeRegister);
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

        btnWelcomeLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Yönlendirmeler
                Intent intentLogin = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intentLogin);


            }
        });


        btnWelcomeRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Yönlendirmeler
                Intent intentRegister = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intentRegister);

            }
        });
    }
    @Override
    // Aktif kullanıcı yoksa MainActivity1 e yönlendirir
    protected void onStart() {
        if(currentUser != null) {
            Intent mainIntent = new Intent(MainActivity.this, MainActivity.class);
            startActivity(mainIntent);
            finish();
        }
        super.onStart();
    }
}


