package com.sam_chordas.android.stockhawk.service;

import android.app.IntentService;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.squareup.okhttp.OkHttpClient;

import org.json.JSONException;
import org.json.JSONObject;

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

    public static final String EXTRA_SYMBOL_PLOTINTENTSERVICE = "extra_symbol_plotintentservice";
    private static final String LOG_TAG = PlotIntentService.class.getSimpleName();
    private OkHttpClient client = new OkHttpClient();
    public static Intent newIntent(Context context, String symbol){
        return new Intent(context, PlotIntentService.class)
                .putExtra(EXTRA_SYMBOL_PLOTINTENTSERVICE, symbol);
    }
    public PlotIntentService() {
        super("StockPlotService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ArrayList<ContentProviderOperation> list = new ArrayList<>();
        String symbol = intent.getStringExtra(EXTRA_SYMBOL_PLOTINTENTSERVICE);
        String url, response;
        String startDate = "2013-09-03";
        String endDate = startDate;
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
                try {
                    Log.e(LOG_TAG, url);
                    JSONObject jsonObject = new JSONObject(response);
                    jsonObject = jsonObject.getJSONObject("query")
                            .getJSONObject("results")
                            .getJSONObject("quote");
                    Log.e(LOG_TAG, jsonObject.getString("Close"));
                } catch (JSONException e) {
                    Log.e(LOG_TAG, "Failed to connect to JsonObject: " + e.getMessage());
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }catch(IOException e){
            Log.e(LOG_TAG, "Failed to grab json data: " + e.getMessage());
        }
    }
}
