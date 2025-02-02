package com.lenier.libreryupdate;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.lenier.update_chaker.HelloWord;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        HelloWord helloWord = new HelloWord();

        Button btn = findViewById(R.id.button);

        btn.setOnClickListener(v -> {
                helloWord.prueba();
        });


    }
}