package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity2 extends AppCompatActivity {

        private Toolbar actionbar;
        private ViewPager viewPagerMain;
        private TabLayout tabsMain;
        private TabsAdapter tabsAdapter;

        private FirebaseAuth auth;
        private FirebaseUser currentUser;


        public void init() {
            actionbar = findViewById(R.id.actionBar);
            setSupportActionBar(actionbar);
            getSupportActionBar().setTitle(R.string.app_name);
            auth = FirebaseAuth.getInstance();
            currentUser = auth.getCurrentUser();


            viewPagerMain = findViewById(R.id.viewPagerMain);
            tabsAdapter = new TabsAdapter(getSupportFragmentManager());
            viewPagerMain.setAdapter(tabsAdapter);

            tabsMain = findViewById(R.id.tabsMain);
            tabsMain.setupWithViewPager(viewPagerMain);
        }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        init();
    }

    @Override
    // Aktif kullanıcı yoksa MainActivity1 e yönlendirir
    protected void onStart() {
        if(currentUser == null) {
            Intent mainIntent = new Intent(MainActivity2.this, MainActivity.class);
            startActivity(mainIntent);
        }
            super.onStart();
    }

    @Override
    //  menü oluşturuyorum
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
            return true;

    }

    @Override
    //menüden bir item seçildiğinde
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.mainLogout) {
            //Auth oturumunu kapatmak istiyorum
            auth.signOut();
            // Bunu yaptıktan Login Aktiviteye yönlendirme yapmam gerekiyor
            Intent loginIntent = new Intent(MainActivity2.this,LoginActivity.class);
            startActivity(loginIntent);
            finish();
        }

            if (item.getItemId() == R.id.mySettingsButton) {
                Intent settingsIntent = new Intent (MainActivity2.this,SettingsActivity.class);
                startActivity(settingsIntent);
        }

        if (item.getItemId() == R.id.findFriends_button) {
            Intent allUsersIntent = new Intent (MainActivity2.this,FindPeople.class);
            startActivity(allUsersIntent);
        }

            return true;

        }



}