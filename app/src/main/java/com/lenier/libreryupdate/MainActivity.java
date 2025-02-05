package com.lenier.libreryupdate;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.lenier.update_chaker.UpdateChecker;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PackageInfo pinfo = null;
        try {
            pinfo = getPackageManager().getPackageInfo(getPackageName(),0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        int currentVersion = pinfo.versionCode;
        String currentVersionCode = String.valueOf(currentVersion);

        String jsonUrl = "https://perf3ctsolutions.com/update.json"; // URL del JSON
        UpdateChecker.checkForUpdate(this,currentVersion,jsonUrl,true);

        TextView txt = findViewById(R.id.textView);
        txt.setText(currentVersionCode);



    }
}