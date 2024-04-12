package com.example.paltcg;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;

import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;


import com.example.paltcg.dataclasses.User;

public class ProfileActivity extends AppCompatActivity {
    TextView pseudo;
    User user;
    RadioButton man, woman;
    ToggleButton save;
    EditText mail;
    Button back;
    String tmp_mail ="", tmp_username="";
    boolean tmp_gender = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        pseudo =  findViewById(R.id.pseudo_player);
        man =  findViewById(R.id.radioButton_Man);
        woman =  findViewById(R.id.radioButton_Woman);
        back =  findViewById(R.id.button_back);
        mail =  findViewById(R.id.editTextTextEmailAddress_mail);
        save =  findViewById(R.id.toggleButton_save);

        Intent intent = getIntent();
        if(intent != null){
            user = intent.getParcelableExtra("the_user");
            if(user != null) {
                pseudo.setText(user.getUsername());
                tmp_gender = user.getGender();
                tmp_username = user.getUsername();
                tmp_mail = user.getMail();
                mail.setText(user.getMail());
                if (user.getGender()) {
                    woman.setChecked(true);
                } else {
                    man.setChecked(true);
                }
            }
        }

        man.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    tmp_gender = false;
            }
        });


        woman.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    tmp_gender = true;
            }
        });

        save.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    save.setBackgroundColor(Color.GREEN);
                    user.setUsername(pseudo.getText().toString());
                    if(man.isChecked()){
                        user.setGender(false);
                    }else{
                        user.setGender(true);
                    }
                    user.setMail(mail.getText().toString());
                }else{
                    save.setBackgroundColor(Color.RED);
                }
            }
        });

        mail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                back.setVisibility(View.GONE);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                back.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {
                tmp_mail = s.toString();
                back.setVisibility(View.VISIBLE);
            }
        });

        pseudo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                back.setVisibility(View.GONE);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                back.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {
                tmp_username = s.toString();
                back.setVisibility(View.VISIBLE);
            }
        });



    }

    public void save(android.view.View v){
        if(save.isChecked()){
            user.setUsername(tmp_username);
            user.setMail(tmp_mail);
            user.setGender(tmp_gender);
        }

        Intent intent = new Intent(this,HomeActivity.class);
        intent.putExtra("the_user",user);
        setResult(Activity.RESULT_OK,intent);
        finish();
    }


}