package com.sam_chordas.android.stockhawk.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.events.HistoricalDataEvent;
import com.sam_chordas.android.stockhawk.service.PlotIntentService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import static com.sam_chordas.android.stockhawk.rest.Utils.truncateBidPricetoFloat;

/**
 * Created by richard on 3/30/16.
 */
public class LineGraphActivity extends AppCompatActivity {

    private static final String LOG_TAG = LineGraphActivity.class.getSimpleName();
    public static final String EXTRA_SYMBOL_LINEGRAPHACTIVITY = "extra_symbol_linegraph";
    private static final String MCHARTDATA_KEY = "mchartdata_key";

    private Intent mServiceIntent;
    private LineChart mLineChart;
    private String mSymbol;
    private String mChartData;

    public static Intent newIntent(Context context, String symbol) {
        return new Intent(context, LineGraphActivity.class)
                .putExtra(EXTRA_SYMBOL_LINEGRAPHACTIVITY, symbol);
    }


    @Subscribe
    public void onHistoricalDataEvent(HistoricalDataEvent event) {
        mChartData = event.historicalData;
        setLineChart(mChartData);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_graph);
        mSymbol = getIntent().getStringExtra(EXTRA_SYMBOL_LINEGRAPHACTIVITY);
        mLineChart = (LineChart) findViewById(R.id.chart);

        if(savedInstanceState != null){
            mChartData = savedInstanceState.getString(MCHARTDATA_KEY);
            setLineChart(mChartData);
        }
        else{
            mServiceIntent = PlotIntentService.newIntent(this, mSymbol, getDate(7), getDate(0));
            startService(mServiceIntent);
        }

    }

    private String getDate(int daysSinceToday){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -daysSinceToday);
        return String.format("%04d-%02d-%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)+ 1,
                calendar.get(Calendar.DATE));
    }
    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if(mChartData != null){
            outState.putString(MCHARTDATA_KEY, mChartData);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.line_graph, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.menu_last_week:
                mServiceIntent = PlotIntentService.newIntent(this, mSymbol, getDate(7), getDate(0));
                startService(mServiceIntent);
                break;
            case R.id.menu_last_month:
                mServiceIntent = PlotIntentService.newIntent(this, mSymbol, getDate(30), getDate(0));
                startService(mServiceIntent);
                break;
            case R.id.menu_last_6_months:
                mServiceIntent = PlotIntentService.newIntent(this, mSymbol, getDate(180), getDate(0));
                startService(mServiceIntent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    private void setLineChart(String response){
        JSONObject jsonObject = null;
        JSONArray jsonArray = null;
        ArrayList<Entry> valueComp = new ArrayList<>();
        ArrayList<String> xVals = new ArrayList<String>();
        LineDataSet setComp;
        LineData lineData;
        Entry entry;
        int count = 0;
        try {
            jsonObject = new JSONObject(response);
            jsonObject = jsonObject.getJSONObject("query")
                    .getJSONObject("results");
            jsonArray = jsonObject.getJSONArray("quote");

            if (jsonArray != null && jsonArray.length() != 0) {
                for (int i = jsonArray.length()-1; i >= 0; i--) {
                    jsonObject = jsonArray.getJSONObject(i);
                    xVals.add(jsonObject.getString("Date"));
                    entry = new Entry(truncateBidPricetoFloat(jsonObject.getString("Close")),count);
                    valueComp.add(entry);
                    count++;
                }
                setComp = new LineDataSet(valueComp, mSymbol);
                lineData = new LineData(xVals, setComp);
                mLineChart.setData(lineData);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mLineChart.invalidate();
                    }
                });
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "jsonexception: " + e.getMessage() );
            if(jsonObject != null){
                Log.e(LOG_TAG, "json object: " + jsonObject.toString());
            }
        }
    }

}
