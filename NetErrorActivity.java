package com.example.drugaddictscouncelling;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public class NetErrorActivity extends AppCompatActivity
{
    SwipeRefreshLayout refresh;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net_error);
        refresh=findViewById(R.id.swipeRefresh);
        Toast.makeText(NetErrorActivity.this,"please connect internet and refresh it",Toast.LENGTH_SHORT).show();
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh()
            {
                new Timer().schedule(new TimerTask()
                {

                    public void run()
                    {
                        if (isOnline())
                        {

                            Intent intent = new Intent( NetErrorActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();

                        }
                        else
                            {

                        }
                    }
                }, 2000);


            }
        });

    }

    public boolean isOnline()
    {
        System.out.println("executeCommand");
        Runtime localRuntime = Runtime.getRuntime();
        try {
            int i = localRuntime.exec("/system/bin/ping -c 1 8.8.8.8")
                    .waitFor();
            System.out.println(" mExitValue " + i);
            boolean bool = false;
            if (i == 0) {
                bool = true;
            }
            return bool;
        }
        catch (InterruptedException localInterruptedException)
        {
            localInterruptedException.printStackTrace();
            System.out.println(" Exception:" + localInterruptedException);
            return false;
        }
        catch (IOException localIOException)
        {
            localIOException.printStackTrace();
            System.out.println(" Exception:" + localIOException);
        }
        return false;
    }
}
