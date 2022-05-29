package com.example.finalproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

public class HomeFragment extends Fragment {

    View view;
    ArrayList<Stock> myStockList = MainActivity.getGlobal();
    ListView homeDisplay;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_home, container, false);

        homeDisplay = view.findViewById(R.id.homeStockDisplay);

        if(myStockList != null) {
            HomeAdapter stockListAdapter = new HomeAdapter(getContext(), R.layout.home_adapter, myStockList);
            homeDisplay.setAdapter(stockListAdapter);

            homeDisplay.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent openNewWindow = new Intent(getActivity(), individualViewActivity.class);
                    openNewWindow.putExtra("Stock Symbol", myStockList.get(position).getSymbol());
                    startActivity(openNewWindow);
                }
            });
        }

        return view;
    }

    public class HomeAdapter extends ArrayAdapter<Stock> {

        Context parentContext;
        int xmlResource;
        List<Stock> myStockList;


        public HomeAdapter(@NonNull Context context, int resource, @NonNull List<Stock> objects) {
            super(context, resource, objects);

            parentContext = context;
            xmlResource = resource;
            myStockList = objects;
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) parentContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            View adapterView = layoutInflater.inflate(xmlResource, null);

            final TextView symbol = adapterView.findViewById(R.id.home_symbol);
            final TextView price = adapterView.findViewById(R.id.price);
            final TextView percentChange = adapterView.findViewById(R.id.percent_change);
            final Button removeButton = adapterView.findViewById(R.id.remove_stock);

            removeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myStockList.remove(position);
                    MainActivity.updateGlobal((ArrayList<Stock>) myStockList);
                    HomeAdapter adapter = new HomeAdapter(getContext(), R.layout.home_adapter, myStockList);
                    adapter.notifyDataSetChanged();
                    homeDisplay.setAdapter(adapter);
                }
            });


            symbol.setText(myStockList.get(position).getSymbol());
            symbol.setTextColor(Color.BLACK);
            HomeAsyncThread setThread = new HomeAsyncThread(price, percentChange);
            setThread.execute(symbol.getText().toString());

            return adapterView;
        }


        public class HomeAsyncThread extends AsyncTask<String, Void, Void> {

            JSONObject object;
            private String priceInfo;
            private String percentChangeInfo;
            TextView price;
            TextView percentChange;

            public HomeAsyncThread(View a, View b){
                price = (TextView)a;
                percentChange = (TextView)b;
            }

            @Override
            protected Void doInBackground(String... strings) {

                try{
                    String input = "https://www.alphavantage.co/query?function=GLOBAL_QUOTE"+"&symbol="+strings[0]+"&apikey=S2L63ZZ5U39BJZ6B";
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

                    priceInfo = "$"+object.getJSONObject("Global Quote").getString("05. price");
                    Log.d("PRICE INFO", priceInfo);
                    percentChangeInfo =  object.getJSONObject("Global Quote").getString("10. change percent");
                    Log.d("PRICE INFO", percentChangeInfo);
                }
                catch(Exception e){
                    Log.d("URL ERROR", e.toString());
                }



                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                price.setText(priceInfo);
                price.setTextColor(Color.BLACK);

                if(percentChangeInfo != null) {
                    if (percentChangeInfo.charAt(0) == '-')
                        percentChange.setTextColor(Color.RED);
                    else
                        percentChange.setTextColor(Color.GREEN);
                }

                percentChange.setText(percentChangeInfo);
            }

        }

    }
}

