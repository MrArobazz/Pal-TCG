package com.example.paltcg;

import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.InputStream;

public class RulesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rules);

        TextView rules = findViewById(R.id.textView_rules);

        try {
            Resources res = getResources();
            InputStream in_s = res.openRawResource(R.raw.rules);
            byte[] b = new byte[in_s.available()];
            if (in_s.read(b) > -1)
                rules.setText(new String(b));
            in_s.close();
        } catch (Exception e) {
            rules.setText(R.string.error_rules);
        }
    }
}