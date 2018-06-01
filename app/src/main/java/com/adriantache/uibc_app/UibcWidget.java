package com.adriantache.uibc_app;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;

import java.util.Date;


/**
 * Implementation of App Widget functionality.
 */
public class UibcWidget extends AppWidgetProvider {
    private static boolean fromOrNext = true;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        final CharSequence descriptionNext = "Next anniversary:";
        final CharSequence descriptionFrom = "Time since uîbc:";
        CharSequence description;

        if (fromOrNext) {
            description = descriptionFrom;
            fromOrNext = false;
        } else {
            description = descriptionNext;
            fromOrNext = true;
        }

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.uibc_widget);
        views.setTextViewText(R.id.description_text, description);
        views.setTextViewText(R.id.time, getTime(fromOrNext));

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }


    private static String getTime(boolean fromOrNext) {
        Date today = new Date();
        Date anniversary = new Date(1433172600000L);

       /* //method to fetch current time and return time to next/past event
        Date today = new Date();
        Date anniversary = new Date(1433172600000L);

        Calendar todayC = Calendar.getInstance();
        todayC.setTime(today);
        String year = String.valueOf(todayC.get(Calendar.YEAR));

        Date nextAnniversary;

        try {
            nextAnniversary = new SimpleDateFormat("yyyy-MM-dd", Locale.US)
                    .parse(year + "-06-12");
        } catch (ParseException e) {
            e.printStackTrace();
        }*/

        return null;
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
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

