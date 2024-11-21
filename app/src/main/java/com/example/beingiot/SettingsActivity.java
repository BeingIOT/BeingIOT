package com.example.beingiot;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    EditText etNameSet;
    Button btnNameSet;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_settings);
        etNameSet = findViewById(R.id.etNameSet);
        btnNameSet = findViewById(R.id.btnNameSet);

        btnNameSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.tvRoomName.setText(etNameSet.getText());
            }
        });
    }
}
