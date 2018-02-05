package com.example.emyeraky.nearlymap.Application_config;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.widget.Toast;

import com.example.emyeraky.nearlymap.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MyApplication extends MultiDexApplication {

    private static DatabaseReference myRef;

    public static DatabaseReference getDatabaseReference() {
        myRef = FirebaseDatabase.getInstance().getReference();
        return myRef;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        try {

            if (!FirebaseApp.getApps(this.getBaseContext()).isEmpty())
                FirebaseDatabase.getInstance().setPersistenceEnabled(true);

            /*Picasso.Builder builder = new Picasso.Builder(this.getBaseContext());
            builder.downloader(new OkHttpDownloader(this.getBaseContext(), Integer.MAX_VALUE));
            Picasso built = builder.build();
            built.setIndicatorsEnabled(true);
            built.setLoggingEnabled(true);
            Picasso.setSingletonInstance(built);*/
        } catch (Exception e) {
            Toast.makeText(MyApplication.this, R.string.Error_MyApplication + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


}
