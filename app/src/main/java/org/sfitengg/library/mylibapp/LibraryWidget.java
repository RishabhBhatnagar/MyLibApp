package org.sfitengg.library.mylibapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.RemoteViews;
import android.widget.Toast;

public class LibraryWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            Toast.makeText(context, "updating", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            RemoteViews appWidgetViews = new RemoteViews(context.getPackageName(), R.layout.library_widget);
            appWidgetViews.setOnClickPendingIntent(R.id.img1, pendingIntent);
            appWidgetManager.updateAppWidget(appWidgetId, appWidgetViews);
        }
    }
}