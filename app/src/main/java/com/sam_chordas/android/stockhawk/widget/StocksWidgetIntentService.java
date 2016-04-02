package com.sam_chordas.android.stockhawk.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViews;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class StocksWidgetIntentService extends IntentService {

    private static final String LOG_TAG = StocksWidgetIntentService.class.getSimpleName();
    public StocksWidgetIntentService() {
        super("StocksWidgetIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,
                StocksWidgetProvider.class));

        Cursor data = getContentResolver().query(
                QuoteProvider.Quotes.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        if(data == null){
            return;
        }
        if(!data.moveToFirst()){
            data.close();
            return;
        }

        String stock = data.getString(data.getColumnIndex(QuoteColumns.SYMBOL));
        data.close();
        for(int appWidgetId : appWidgetIds){
            RemoteViews views = new RemoteViews(getPackageName(), R.layout.stocks_widget);

            views.setTextViewText(R.id.widget_single_stock_title, stock);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
