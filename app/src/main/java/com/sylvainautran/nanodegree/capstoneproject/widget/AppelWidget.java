package com.sylvainautran.nanodegree.capstoneproject.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.TaskStackBuilder;
import android.widget.RemoteViews;

import com.sylvainautran.nanodegree.capstoneproject.CallsDetailsActivity;
import com.sylvainautran.nanodegree.capstoneproject.CallsListActivity;
import com.sylvainautran.nanodegree.capstoneproject.R;

public class AppelWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {

            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

            rv.setRemoteAdapter(R.id.list_view, new Intent(context, AppelWidgetService.class));
            rv.setEmptyView(R.id.list_view, R.id.empty_view);

            Intent Intent = new Intent(context, CallsListActivity.class);
            PendingIntent pendingIntent = TaskStackBuilder.create(context)
                    .addNextIntentWithParentStack(Intent)
                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            rv.setOnClickPendingIntent(R.id.empty_view, pendingIntent);

            Intent clickIntentTemplate = new Intent(context, CallsDetailsActivity.class);
            PendingIntent clickPendingIntentTemplate = TaskStackBuilder.create(context)
                    .addNextIntentWithParentStack(clickIntentTemplate)
                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            rv.setPendingIntentTemplate(R.id.list_view, clickPendingIntentTemplate);

            appWidgetManager.updateAppWidget(appWidgetId, rv);
        }
    }
}