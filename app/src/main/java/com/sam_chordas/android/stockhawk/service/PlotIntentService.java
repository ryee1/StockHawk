package com.sam_chordas.android.stockhawk.service;

import android.app.IntentService;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.sam_chordas.android.stockhawk.events.HistoricalDataEvent;
import com.squareup.okhttp.OkHttpClient;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 */
public class PlotIntentService extends IntentService {

    public static final String EXTRA_STARTDATE = "extra_startdate_plotintentservice";
    public static final String EXTRA_ENDDATE = "extra_enddate_plotintentservice";
    public static final String EXTRA_SYMBOL = "extra_symbol_plotintentservice";
    private static final String LOG_TAG = PlotIntentService.class.getSimpleName();
    private OkHttpClient client = new OkHttpClient();

    public static Intent newIntent(Context context, String symbol, String startDate, String endDate) {
        return new Intent(context, PlotIntentService.class)
                .putExtra(EXTRA_SYMBOL, symbol)
                .putExtra(EXTRA_STARTDATE, startDate)
                .putExtra(EXTRA_ENDDATE, endDate);
    }

    public PlotIntentService() {
        super("StockPlotService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ArrayList<ContentProviderOperation> list = new ArrayList<>();
        String symbol = intent.getStringExtra(EXTRA_SYMBOL);
        String startDate = intent.getStringExtra(EXTRA_STARTDATE);
        String endDate = intent.getStringExtra(EXTRA_ENDDATE);
        String url, response;
        JSONArray resultArray;
        StringBuilder urlStringBuilder = new StringBuilder();
        try {
            urlStringBuilder.append("https://query.yahooapis.com/v1/public/yql?q=");
            urlStringBuilder.append(URLEncoder.encode("select * from yahoo.finance.historicaldata where symbol "
                    + "in ( \"" + symbol + "\" ) and startDate = \"" + startDate + "\" and "
                    + "endDate = \"" + endDate + "\"", "UTF-8"));
            urlStringBuilder.append("&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables."
                    + "org%2Falltableswithkeys&callback=");
            url = urlStringBuilder.toString();
            try {
                response = UtilsService.fetchData(client, url);
                EventBus.getDefault().post(new HistoricalDataEvent(response));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Failed to grab json data: " + e.getMessage());
        }
    }
}
