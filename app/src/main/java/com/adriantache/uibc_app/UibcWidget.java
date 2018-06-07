package com.adriantache.uibc_app;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;

import java.util.Calendar;


/**
 * Implementation of App Widget functionality.
 */
public class UibcWidget extends AppWidgetProvider {
    private static final int ERROR_VALUE = -1;
    private static boolean fromOrNext = true;
    private static String description;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        final String descriptionNext = "Next anniversary:";
        final String descriptionFrom = "Time since uîbc:";

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
        //set anniversary
        int anniversaryDay = 12;
        int anniversaryMonth = 6;
        int anniversaryYear = 2015;

        //get today
        Calendar todayC = Calendar.getInstance();
        int todayDay = todayC.get(Calendar.DAY_OF_MONTH);
        int todayMonth = todayC.get(Calendar.MONTH);
        int todayYear = todayC.get(Calendar.YEAR);

        int days = ERROR_VALUE;
        int months = ERROR_VALUE;
        int years = ERROR_VALUE;

        //calculate time since anniversary
        if (fromOrNext) {
            if (todayMonth == anniversaryMonth) {
                if (todayDay < anniversaryDay) {
                    days = todayDay;
                    months = 11;
                    years = todayYear - anniversaryYear - 1;
                } else if (todayDay > anniversaryDay) {
                    days = todayDay;
                    months = 0;
                    years = todayYear - anniversaryYear;
                }
            } else if (todayMonth < anniversaryMonth) {
                years = todayYear - anniversaryYear - 1;

                if (todayDay < anniversaryDay) {
                    days = 31 - anniversaryDay + todayDay;
                    months = 12 - anniversaryMonth + todayMonth - 1;
                } else if (todayDay == anniversaryDay) {
                    days = 0;
                    months = 12 - anniversaryMonth + todayMonth;
                } else {
                    days = todayDay - anniversaryDay;
                    months = 12 - anniversaryMonth + todayMonth;
                }
            } else {
                years = todayYear - anniversaryYear;

                if (todayDay < anniversaryDay) {
                    days = 31 - anniversaryDay + todayDay;
                    months = todayMonth - anniversaryMonth - 1;
                } else if (todayDay == anniversaryDay) {
                    days = 0;
                    months = todayMonth - anniversaryMonth;
                } else {
                    days = todayDay - anniversaryDay;
                    months = todayMonth - anniversaryMonth;
                }
            }
        }
        //calculate time until next anniversary
        else {
            //period will never be a year or more
            years = 0;

            //calculate time until next anniversary
            if (todayMonth == anniversaryMonth) {
                if (todayDay < anniversaryDay) {
                    months = 0;
                    days = anniversaryDay - todayDay;
                } else {
                    months = 11;
                    days = 30 - todayDay + anniversaryDay;
                }
            } else if (todayMonth < anniversaryMonth) {
                if (todayDay < anniversaryDay) {
                    months = anniversaryMonth - todayMonth;
                    days = anniversaryDay - todayDay;
                } else if (todayDay == anniversaryDay) {
                    days = 0;
                    months = anniversaryMonth - todayMonth;
                } else {
                    months = anniversaryMonth - todayMonth - 1;
                    days = 30 - todayDay + anniversaryDay;
                }
            } else {
                if (todayDay < anniversaryDay) {
                    months = 12 - todayMonth + anniversaryMonth;
                    days = anniversaryDay - todayDay;
                } else if (todayDay == anniversaryDay) {
                    days = 0;
                    months = 12 - todayMonth + anniversaryMonth;
                } else {
                    months = 12 - todayMonth + anniversaryMonth - 1;
                    days = 30 - todayDay + anniversaryDay;
                }
            }
        }

        //I'm being paranoid. But then again math isn't always my friend.
        if (days > 31) days = 31;

        //todo replace output with a nice conditional StringBuilder

        String yearText = years == 1 ? "Year" : "Years";
        String monthText = months == 1 ? "Month" : "Months";
        String dayText = days == 1 ? "Day" : "Days";

        if (days == ERROR_VALUE || months == ERROR_VALUE || years == ERROR_VALUE) return "ERROR";
        else if (years == 0 && months == 0) return "Just " + days + " " + dayText + "!";
        else if (years == 0) return months + " " + monthText + " and " + days + " " + dayText;
        else
            return years + " " + yearText + ", " + months + " " + monthText + " and " + days + " " + dayText;
    }

    private static Calendar getNextAnniversary(int todayDay, int todayMonth, int todayYear) {
        int day = 12;
        int month = 6;
        int year = todayYear;

        if (todayMonth == 6) {
            if (todayDay < 12)
                year = todayYear;
            else if (todayDay > 12)
                year = todayYear + 1;
        } else if (todayMonth < 6)
            year = todayYear;
        else
            year = todayYear + 1;

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        return calendar;
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

