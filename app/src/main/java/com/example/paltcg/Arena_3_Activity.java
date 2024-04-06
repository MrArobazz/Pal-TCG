package com.example.paltcg;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class Arena_3_Activity extends AppCompatActivity {

    NavigationView navigationView;
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.arena3);
        navigationView = findViewById(R.id.navigationView_arena3);
        toolbar = findViewById(R.id.toolbar_arena3);
        drawerLayout = findViewById(R.id.drawerLayout_arena3);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(Arena_3_Activity.this,drawerLayout,toolbar,R.string.open_menu,R.string.close_menu);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if(menuItem.getItemId() == R.id.attack_arena3) {
                    Toast.makeText(Arena_3_Activity.this,"Attack", Toast.LENGTH_SHORT).show();
                }else{
                    if(menuItem.getItemId() == R.id.defense_arena3) {
                        Toast.makeText(Arena_3_Activity.this, "Defense", Toast.LENGTH_SHORT).show();
                    }
                        else {
                        if (menuItem.getItemId() == R.id.change_arena3) {
                            Toast.makeText(Arena_3_Activity.this, "Change", Toast.LENGTH_SHORT).show();
                        }
                    }

                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

    }
}