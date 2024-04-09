package com.example.paltcg;

import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Calendar;

public class ProfileActivity extends AppCompatActivity {
    CalendarView birthday;
    TextView pseudo;

    EditText date_birthday;

    private boolean have_set_bithday = false;


    private void setBirthday(android.view.View view){
       birthday.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
           @Override
           public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
               if(!have_set_bithday){
                   String date = ""+dayOfMonth+"/"+month+"/"+year;
                    date_birthday.setText(date);
                    have_set_bithday = true;
               }
           }
       });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        birthday = (CalendarView) findViewById(R.id.calendarView_birthday);
        pseudo = (TextView) findViewById(R.id.pseudo_player);
        date_birthday = (EditText) findViewById(R.id.editTextDate_birthday);

    }


}