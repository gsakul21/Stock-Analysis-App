package com.example.finalproject;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CustomAdapter extends ArrayAdapter<Stock> {

    Context parentContext;
    List<Stock> list;
    List<Stock> myStockList;
    int xmlResource;

    public CustomAdapter(@NonNull Context context, int resource, @NonNull List<Stock> objects, List<Stock> myStockList) {
        super(context, resource, objects);
        parentContext = context;
        xmlResource = resource;
        list = objects;
        this.myStockList = myStockList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) parentContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View adapterView = layoutInflater.inflate(xmlResource, null);

        final TextView symbol = adapterView.findViewById(R.id.sym);
        final TextView stockName = adapterView.findViewById(R.id.stock_name);
         Button addButton = adapterView.findViewById(R.id.add_button);

        symbol.setText(list.get(position).getSymbol());
        stockName.setText(list.get(position).getName());

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean alreadyAdded = false;
                for(int x = 0; x < myStockList.size(); x++){
                    if(myStockList.get(x).getName().equals(stockName.getText().toString()))
                        alreadyAdded = true;
                }

                if(alreadyAdded == false) {
                    myStockList.add(new Stock(symbol.getText().toString(), stockName.getText().toString()));
                    MainActivity.updateGlobal((ArrayList<Stock>) myStockList);
                    Log.d("LIST CHECK", MainActivity.getGlobal().toString());
                }
                else{
                    Toast.makeText(getContext(), "This stock has already been added.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return adapterView;
    }

}
