package com.example.emyeraky.nearlymap.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.emyeraky.nearlymap.R;

/**
 * Created by Emy Eraky on 7/12/2017.
 */

public class MapRemoteViewsService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new MapRemoteViewsFactory(getApplicationContext(),intent);
    }
}
class MapRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory{
    private Context mContext=null;
    private int mWidgetId;
    private static final String[] items={"Hospital", "Hotels", "Restuarant", "TravelAgancy"};
    private static final int[] images={R.drawable.aidkit,R.drawable.hotell, R.drawable.cooker,R.drawable.airplane};


    public MapRemoteViewsFactory(Context context, Intent intent) {
        mContext = context;
        mWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }
    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return (items.length);
    }

    @Override
    public RemoteViews getViewAt(int i) {
        RemoteViews row=new RemoteViews(mContext.getPackageName(),
                R.layout.row_item);
        row.setTextViewText(R.id.txt, items[i]);
        row.setImageViewResource(R.id.img, images[i]);

        Intent intent=new Intent();
        Bundle extras=new Bundle();

        extras.putString(MapWidgetProvider.EXTRA, items[i]);
        intent.putExtras(extras);
        row.setOnClickFillInIntent(R.id.widget_list_item, intent);

        return(row);
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
