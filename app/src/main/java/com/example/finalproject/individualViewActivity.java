package com.example.finalproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.IMarker;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.MPPointF;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class individualViewActivity extends AppCompatActivity {
    final long dayConstant = 86400000;
     LineChart mChart;
     Button backButton;
     Button evaluateButton;
     JSONObject stockData;
     TextView openPrice;
     TextView closePrice;
     TextView lowPrice;
     TextView highPrice;
     TextView volumePrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_view);

        backButton = findViewById(R.id.backButton);
        evaluateButton = findViewById(R.id.evaluate_button);
        openPrice = findViewById(R.id.open_value);
        closePrice = findViewById(R.id.close_value);
        lowPrice = findViewById(R.id.low_value);
        highPrice = findViewById(R.id.high_value);
        volumePrice = findViewById(R.id.volume_value);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        String stock_symbol = getIntent().getStringExtra("Stock Symbol");

        mChart = findViewById(R.id.stock_graph);

        //mChart.setOnChartGestureListener(individualViewActivity.this);
        //mChart.setOnChartValueSelectedListener(individualViewActivity.this);

       AsyncThread dataThread = new AsyncThread(openPrice, closePrice, lowPrice, highPrice, volumePrice, mChart, evaluateButton);
       dataThread.execute(stock_symbol);



    }




    public class AsyncThread extends AsyncTask<String, Void, Void> {

        private JSONObject object;
        ArrayList<Entry> yValues = new ArrayList<>();

        private String openPrice;
        private String closePrice;
        private String lowPrice;
        private String highPrice;
        private String volumeInfo;
        private int indicator;

        private TextView open;
        private TextView close;
        private TextView low;
        private TextView high;
        private TextView volume;
        private LineChart chart;
        private Button evaluate;

        public AsyncThread(View open, View close, View low, View high, View volume, View chart, View evaluate){
                this.open = (TextView)open;
                this.close = (TextView)close;
                this.low = (TextView)low;
                this.high = (TextView)high;
                this.volume = (TextView)volume;
                this.chart = (LineChart)chart;
                this.evaluate = (Button)evaluate;
        }

        @Override
        protected Void doInBackground(String... strings) {


            try{
                String input = "https://www.alphavantage.co/query?"+"function=TIME_SERIES_DAILY"+"&symbol="+strings[0]+"&apikey=S2L63ZZ5U39BJZ6B";
                Log.d("OUTPUT", input);
                URL requester = new URL(input);
                URLConnection connection = requester.openConnection();
                InputStream stream = connection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(stream));
                String content = "";
                String fin = "";
                while ((content = br.readLine()) != null) {
                    fin = fin+""+content;
                }
                Log.d("OUTFIN", fin);
                object = new JSONObject(fin);

                Log.d("TEST OUTPUT", object.getJSONObject("Time Series (Daily)").getJSONObject("2020-06-03").getString("1. open"));


                stockData = object.getJSONObject("Time Series (Daily)");

                JSONArray primaryKey = stockData.names();

                openPrice = stockData.getJSONObject(primaryKey.getString(0)).getString("1. open");
                closePrice = stockData.getJSONObject(primaryKey.getString(0)).getString("4. close");
                lowPrice = stockData.getJSONObject(primaryKey.getString(0)).getString("3. low");
                highPrice = stockData.getJSONObject(primaryKey.getString(0)).getString("2. high");
                volumeInfo = stockData.getJSONObject(primaryKey.getString(0)).getString("5. volume");

                ArrayList<Double> fiveDayMAvg = new ArrayList<>();
                ArrayList<Double> tenDayMAvg = new ArrayList<>();
                ArrayList<Double> twentyDayMAvg = new ArrayList<>();

                JSONArray key = stockData.names();
                int counter = 0;
                double sum = 0;
                for(int i = key.length() - 1; i > -1; i--){
                    String keys = key.getString(i);
                    if(stockData.getJSONObject(keys) != null) {
                        String value = stockData.getJSONObject(keys).getString("4. close");
                        Float dataPoint = Float.parseFloat(value);
                        yValues.add(new Entry(counter, dataPoint));

                        double dataVal =Double.parseDouble(value);
                        sum += dataVal;
                        if(counter % 5 == 0)
                            fiveDayMAvg.add(sum/5.0);

                        if(counter % 10 == 0)
                            tenDayMAvg.add(sum/10.0);

                        if(counter % 20 == 0)
                            twentyDayMAvg.add(sum/20.0);

                        counter++;
                    }
                }

                Log.d("ARRAY CHECK", yValues.toString());
                Log.d("5DAYMAVG", fiveDayMAvg.toString());
                Log.d("10DAYMAVG", tenDayMAvg.toString());
                Log.d("20DAYMAVG", twentyDayMAvg.toString());

                int changeVal = 0;

                for(int x = 0; x < twentyDayMAvg.size(); x++){

                    if(x != 0) {
                        if ((fiveDayMAvg.get(x) < tenDayMAvg.get(x)) && ((fiveDayMAvg.get(x - 1) > tenDayMAvg.get(x - 1))))
                            changeVal -= 1;

                        if ((tenDayMAvg.get(x) < twentyDayMAvg.get(x)) && ((tenDayMAvg.get(x - 1) > twentyDayMAvg.get(x - 1))))
                            changeVal -= 1;

                        if ((fiveDayMAvg.get(x) > tenDayMAvg.get(x)) && ((fiveDayMAvg.get(x - 1) < tenDayMAvg.get(x - 1))))
                            changeVal += 1;

                        if ((tenDayMAvg.get(x) > twentyDayMAvg.get(x)) && ((tenDayMAvg.get(x - 1) < twentyDayMAvg.get(x - 1))))
                            changeVal += 1;
                    }

                }

                if(changeVal == 0){
                    if( (fiveDayMAvg.get(0) > tenDayMAvg.get(0)) && ( tenDayMAvg.get(0) > twentyDayMAvg.get(0) ))
                        indicator = 100;
                    else
                        indicator = -100;
                }
                else
                    indicator = changeVal;



            }
            catch(Exception e){
                Log.d("DATA GATHERING ERROR", e.toString());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            open.setText("Open: $"+openPrice);
            open.setTextColor(Color.BLACK);
            close.setText("Close: $"+closePrice);
            close.setTextColor(Color.BLACK);
            low.setText("Low: $"+lowPrice);
            low.setTextColor(Color.BLACK);
            high.setText("High: $"+highPrice);
            high.setTextColor(Color.BLACK);
            volume.setText("Volume Traded: "+volumeInfo);
            volume.setTextColor(Color.BLACK);

            Log.d("CHECK1","Values for Data have been set." );

            chart.setDragEnabled(true);
            chart.setScaleEnabled(false);

            Log.d("CHECK2", "Drag and Scale set.");

            LineDataSet values = new LineDataSet(yValues, "Stock Prices For Last 100 Days");

            Log.d("CHECK3", "LineDataSet made.");

            values.setDrawValues(false);
            values.setDrawCircles(false);
            values.setColor(Color.GREEN);
            values.setValueTextColor(Color.GREEN);
            values.setDrawFilled(true);
            Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.fade_green);
            values.setFillDrawable(drawable);

            Log.d("CHECK4", "LineDataSet settings customized.");

            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(values);

            Log.d("CHECK5", "Arraylist of LineDataSets created and values added");

            LineData data = new LineData(dataSets);

            Log.d("CHECK6", "LineData created");

            chart.setData(data);

            Log.d("CHECK7", "Chart has been set with data");

            Description description = new Description();
            description.setText("");
            chart.setDescription(description);
            chart.setDrawGridBackground(false);

            XAxis xAxis = chart.getXAxis();
            YAxis yAxisLeft = chart.getAxisLeft();
            yAxisLeft.setDrawAxisLine(true);
            yAxisLeft.setDrawLabels(false);
            xAxis.setDrawLabels(false);
            xAxis.setDrawAxisLine(true);

            chart.setTouchEnabled(true);
            IMarker marker = new MyMarkerView(getApplicationContext(), R.layout.custom_marker_view_layout);
            chart.setMarker(marker);

            chart.invalidate();

            String message = "";
            if(indicator == 2)
                message = "It appears that the stock is starting to increase at a rapid rate, it would be better to buy shares now or to hold onto what you already have.";
            else if(indicator == 1)
                message = "It appears that the stock is starting to increase, this could be a false trend, but it is recommended to consider buying shares of this stock";
            else if(indicator == -1)
                message = "It appears that the stock is starting to decrease, this could be a false trend, but it is recommended to consider selling shares of this stock or not buying.";
            else if(indicator == -2)
                message = "It appears that the stock is starting to decrease at a rapid rate, it would be better to buy sell shares now or not buy at all";
            else if(indicator == 100)
                message = "The stock shows no significant changes in path, it is not a good time to buy shares as the price seems to continue rising";
            else
                message = "The stock shows no significant changes in path, it is recommended that you sell shares as the price seems to continue dropping";

            final String endResult = message;

            evaluate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(individualViewActivity.this);
                    builder.setMessage(endResult);
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            });



        }

    }

    public class MyMarkerView extends MarkerView {

        private TextView tvContent;

        public MyMarkerView(Context context, int layoutResource) {
            super(context, layoutResource);

            // find your layout components
            tvContent = (TextView) findViewById(R.id.tvContent);
        }

        // callbacks everytime the MarkerView is redrawn, can be used to update the
// content (user-interface)
        @Override
        public void refreshContent(Entry e, Highlight highlight) {

            tvContent.setText("" + e.getY());

            // this will perform necessary layouting
            super.refreshContent(e, highlight);
        }

        private MPPointF mOffset;

        @Override
        public MPPointF getOffset() {

            if(mOffset == null) {
                // center the marker horizontally and vertically
                mOffset = new MPPointF(-(getWidth() / 2), -getHeight());
            }

            return mOffset;
        }}


}
