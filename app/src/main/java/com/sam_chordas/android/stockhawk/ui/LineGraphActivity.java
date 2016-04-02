package com.sam_chordas.android.stockhawk.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.sam_chordas.android.stockhawk.R;

/**
 * Created by richard on 3/30/16.
 */
public class LineGraphActivity extends AppCompatActivity {

    private static final String LOG_TAG = LineGraphActivity.class.getSimpleName();
    public static final String EXTRA_SYMBOL_LINEGRAPHACTIVITY = "extra_symbol_linegraph";
    private static final String BASE_URL = "http://chart.finance.yahoo.com/z?s=";
    private static final String APPEND_URL = "&q=l&l=on&z=s";

    private ImageView mLineGraphImage;
    public static Intent newIntent(Context context, String symbol){
        return new Intent(context, LineGraphActivity.class)
                .putExtra(EXTRA_SYMBOL_LINEGRAPHACTIVITY, symbol);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_graph);
        mLineGraphImage = (ImageView) findViewById(R.id.line_graph_image);
        String symbol = getIntent().getStringExtra(EXTRA_SYMBOL_LINEGRAPHACTIVITY);
        String days = "&t=7d";
        Glide.with(this)
                .load(BASE_URL + symbol + days + APPEND_URL)
                .fitCenter()
                .into(mLineGraphImage);
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
