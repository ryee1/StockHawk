package com.sam_chordas.android.stockhawk.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.service.PlotIntentService;

/**
 * Created by richard on 3/30/16.
 */
public class LineGraphActivity extends AppCompatActivity {

    private static final String LOG_TAG = LineGraphActivity.class.getSimpleName();
    private Intent mServiceIntent;

    public static final String EXTRA_SYMBOL_LINEGRAPHACTIVITY = "extra_symbol_linegraph";
    public static Intent newIntent(Context context, String symbol){
        return new Intent(context, LineGraphActivity.class)
                .putExtra(EXTRA_SYMBOL_LINEGRAPHACTIVITY, symbol);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_graph);
        String symbol = getIntent().getStringExtra(EXTRA_SYMBOL_LINEGRAPHACTIVITY);
        Log.e(LOG_TAG, symbol);
        mServiceIntent = PlotIntentService.newIntent(this, symbol);
        startService(mServiceIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
