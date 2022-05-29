package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    final long dayConstant = 86400000;
    public final String APIKey = "S2L63ZZ5U39BJZ6B";
    public String requestURL = "https://www.alphavantage.co/query?";
    String function = "GLOBAL_QUOTE";
    String symbol = "TSLA";
    int fragmentCounter;
    static ArrayList<Stock> myStockList;
    Button homeButton;
    Button searchButton;
    FragmentManager fragmentManager;
    FragmentTransaction transactor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentCounter = 0;

        SharedPreferences load = getSharedPreferences("ARRAY DATA", 0);
        Gson gson = new Gson();
        String json = load.getString("STOCK LIST", null);
        Type type = new TypeToken<ArrayList<Stock>>() {}.getType();
        myStockList = gson.fromJson(json, type);

        if(myStockList == null)
        myStockList = new ArrayList<>();


        searchButton = findViewById(R.id.searchButton);
        homeButton = findViewById(R.id.homeButton);

        fragmentManager = getSupportFragmentManager();
        transactor = fragmentManager.beginTransaction();
        HomeFragment homeFragment = new HomeFragment();
        transactor.add(R.id.fragment_container, homeFragment);
        transactor.commit();
        fragmentCounter++;

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fragmentCounter == 0) {
                    fragmentManager = getSupportFragmentManager();
                    transactor = fragmentManager.beginTransaction();
                    HomeFragment homeFragment = new HomeFragment();
                    transactor.add(R.id.fragment_container, homeFragment);
                    transactor.commit();
                    fragmentCounter++;
                }
                else{
                    fragmentManager = getSupportFragmentManager();
                    transactor = fragmentManager.beginTransaction();
                    HomeFragment homeFragment = new HomeFragment();
                    transactor.replace(R.id.fragment_container, homeFragment);
                    transactor.commit();
                    fragmentCounter++;
                }
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fragmentCounter == 0) {
                    fragmentManager = getSupportFragmentManager();
                    transactor = fragmentManager.beginTransaction();
                    SearchFragment testFragment = new SearchFragment();
                    transactor.add(R.id.fragment_container, testFragment);
                    transactor.commit();
                    fragmentCounter++;
                }
                else{
                    fragmentManager = getSupportFragmentManager();
                    transactor = fragmentManager.beginTransaction();
                    SearchFragment searchFragment = new SearchFragment();
                    transactor.replace(R.id.fragment_container, searchFragment);
                    transactor.commit();
                    fragmentCounter++;
                }
            }
        });

    }

    public static void updateGlobal(ArrayList<Stock> list){
        myStockList = list;
    }

    public static ArrayList<Stock> getGlobal(){
        return myStockList;
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences save = getSharedPreferences("ARRAY DATA", 0);
        SharedPreferences.Editor editor = save.edit();
        Gson gson = new Gson();
        String json = gson.toJson(myStockList);
        editor.putString("STOCK LIST", json);
        editor.apply();
    }
}


