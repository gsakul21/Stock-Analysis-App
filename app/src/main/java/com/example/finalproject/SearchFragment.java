package com.example.finalproject;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    View view;
    EditText searchInput;
    AsyncThread searchThread;
    ArrayList<Stock> searchResults = new ArrayList<>();
    ArrayList<Stock> myStockList = MainActivity.getGlobal();
    CustomAdapter searchAdapter;
    ListView searchDisplay;

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        view = inflater.inflate(R.layout.fragment_search, container, false);

        searchInput = view.findViewById(R.id.searchInput);
        searchDisplay = view.findViewById(R.id.searchResult);

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().equals("")){
                    searchResults = new ArrayList<>();
                    searchAdapter = new CustomAdapter(getContext(), R.layout.custom_adapter, searchResults, myStockList);
                    searchDisplay.setAdapter(searchAdapter);
                }
                else {
                    searchThread = new AsyncThread();
                    searchThread.execute(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    public void stockSearch(String input){

    }
    public class AsyncThread extends AsyncTask<String, Void, Void> {

        JSONObject object;

        @Override
        protected Void doInBackground(String... strings) {


            try{
                String input = "https://www.alphavantage.co/query?function=SYMBOL_SEARCH"+"&keywords="+strings[0]+"&apikey=S2L63ZZ5U39BJZ6B";
                Log.d("OUTPUT", input);
                URL requester = new URL(input);
                Log.d("URL", "THIS IS NOT WORKING");
                URLConnection connection = requester.openConnection();
                Log.d("URL CONNECTION", "Connection Established");
                InputStream stream = connection.getInputStream();
                Log.d("URL INPUTSTREAM", "InputStream Open");
                BufferedReader br = new BufferedReader(new InputStreamReader(stream));
                String content = "";
                String fin = "";
                while ((content = br.readLine()) != null) {
                    Log.d("OUTPUT", content);
                    fin = fin+""+content;
                }
                Log.d("OUTFIN", fin);
                object = new JSONObject(fin);

                int JSONArraySize = object.getJSONArray("bestMatches").length();

                searchResults = new ArrayList<>();
                for(int x = 0; x < JSONArraySize; x++){
                    Log.d("SYMBOL CHECK", object.getJSONArray("bestMatches").getJSONObject(x).getString("1. symbol"));
                  searchResults.add(new Stock(object.getJSONArray("bestMatches").getJSONObject(x).getString("1. symbol"), object.getJSONArray("bestMatches").getJSONObject(x).getString("2. name")) );
                }

            }
            catch(Exception e){
                Log.d("URL ERROR", e.toString());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            searchAdapter = new CustomAdapter(getContext(), R.layout.custom_adapter, searchResults, myStockList);
            searchDisplay.setAdapter(searchAdapter);
        }
    }

}

