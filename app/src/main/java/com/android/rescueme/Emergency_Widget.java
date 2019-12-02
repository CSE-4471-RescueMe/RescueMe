package com.android.rescueme;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 */
public class Emergency_Widget extends AppWidgetProvider {
    private static final String SHOW_POPUP_DIALOG_ACTION = "com.javacodegeeks.android.showpopupdialog";
    /*static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.emergency__widget);

        remoteViews.setOnClickPendingIntent(R.id.button, pendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }*/

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            //updateAppWidget(context, appWidgetManager, appWidgetId);
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                    R.layout.emergency__widget);

            // Create an intent that when received will launch the PopUpActivity
            Intent intent = new Intent(context, Emergency_Widget.class);
            intent.setAction(SHOW_POPUP_DIALOG_ACTION);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                    0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            // Set up the onClickListener of the widget
            // Now, when the widget is pressed the pendingIntent will be sent

            remoteViews.setOnClickPendingIntent(R.id.button, pendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);

    }
    @Override
    public void onReceive(final Context context, Intent intent) {

        // If the intent is the one that we've defined to launch the pop up dialog
        // then create and launch the PopUpActivity
        if (intent.getAction().equals(SHOW_POPUP_DIALOG_ACTION)) {

            Intent popUpIntent = new Intent(context, PopUpActivity.class);
            popUpIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(popUpIntent);

        }

        super.onReceive(context, intent);

    }
    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

