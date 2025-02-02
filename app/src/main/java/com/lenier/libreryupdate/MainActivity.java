package com.lenier.libreryupdate;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.lenier.update_chaker.HelloWord;
import com.lenier.update_chaker.UpdateChecker;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String jsonUrl = "https://perf3ctsolutions.com/update.json"; // URL del JSON
        UpdateChecker.checkForUpdate(this,1,jsonUrl,true);

    }
}