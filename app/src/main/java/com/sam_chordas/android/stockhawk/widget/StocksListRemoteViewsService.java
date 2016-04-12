package com.sam_chordas.android.stockhawk.widget;

import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.os.Build;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.ui.LineGraphActivity;

/**
 * Created by richard on 4/5/16.
 */
public class StocksListRemoteViewsService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {

            private Cursor data = null;

            @Override
            public void onCreate() {
            }

            @Override
            public void onDataSetChanged() {
                final long identityToken = Binder.clearCallingIdentity();
                data = getContentResolver().query(
                        QuoteProvider.Quotes.CONTENT_URI,
                        new String[]{QuoteColumns._ID, QuoteColumns.SYMBOL, QuoteColumns.BIDPRICE,
                                QuoteColumns.PERCENT_CHANGE, QuoteColumns.CHANGE, QuoteColumns.ISUP},
                        QuoteColumns.ISCURRENT + " = ?",
                        new String[]{"1"},
                        null
                );
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (data == null || !data.moveToPosition(position)) {
                    return null;
                }

                String symbol = data.getString(data.getColumnIndex(QuoteColumns.SYMBOL));
                String change = data.getString(data.getColumnIndex(QuoteColumns.CHANGE));
                String price = data.getString(data.getColumnIndex(QuoteColumns.BIDPRICE));
                RemoteViews views = new RemoteViews(getPackageName(), R.layout.stocks_widget_list_items);
                views.setTextViewText(R.id.widget_listview_symbol, symbol);
                views.setTextViewText(R.id.widget_listview_change, change);
                views.setTextViewText(R.id.widget_listview_price, price);
                int sdk = Build.VERSION.SDK_INT;
                if (data.getInt(data.getColumnIndex("is_up")) == 1) {
                    if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
                        views.setInt(R.id.widget_listview_change, "setBackgroundResource", R.drawable.percent_change_pill_green);
                    } else {
                        views.setInt(R.id.widget_listview_change, "setBackgroundResource", R.drawable.percent_change_pill_green);
                    }
                } else {
                    if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
                        views.setInt(R.id.widget_listview_change, "setBackgroundResource", R.drawable.percent_change_pill_red);
                    } else {
                        views.setInt(R.id.widget_listview_change, "setBackgroundResource", R.drawable.percent_change_pill_red);
                    }
                }


                final Intent fillInIntent = new Intent();
                fillInIntent.putExtra(LineGraphActivity.EXTRA_SYMBOL_LINEGRAPHACTIVITY, symbol);
                views.setOnClickFillInIntent(R.id.widget_list_view_item, fillInIntent);
                return views;
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.stocks_widget_list_items);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }


}
