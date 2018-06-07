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

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        final String descriptionNext = "Next anniversary:";
        final String descriptionFrom = "Time since uîbc:";

        String description;
        if (fromOrNext) {
            description = descriptionFrom;
            fromOrNext = false;
        } else {
            description = descriptionNext;
            fromOrNext = true;
        }

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.uibc_widget);
        if (anniversary()) {
            views.setTextViewText(R.id.description_text, "For the past " + getYears());
            views.setTextViewText(R.id.time, "I LOVE YOU!");
            views.setImageViewResource(R.id.thumbnail,R.drawable.heart);
        } else {
            views.setTextViewText(R.id.description_text, description);
            views.setTextViewText(R.id.time, getTime(!fromOrNext));
            views.setImageViewResource(R.id.thumbnail,R.drawable.logo);
        }

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private static String getYears() {
        Calendar todayC = Calendar.getInstance();
        int year = todayC.get(Calendar.YEAR);

        return String.valueOf(year - 2015);
    }

    private static boolean anniversary() {
        int anniversaryDay = 12;
        int anniversaryMonth = 6;
        Calendar todayC = Calendar.getInstance();
        int todayDay = todayC.get(Calendar.DAY_OF_MONTH);
        int todayMonth = todayC.get(Calendar.MONTH);

        return anniversaryDay == todayDay && anniversaryMonth == todayMonth;
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

        if (days == ERROR_VALUE || months == ERROR_VALUE || years == ERROR_VALUE) return "ERROR";

        StringBuilder sb = new StringBuilder();
        if (years > 0) {
            sb.append(years);
            sb.append(" ");

            if (years == 1) sb.append("Year");
            else sb.append("Years");
        }
        if (months > 0) {
            if (sb.length() != 0) sb.append(", ");

            sb.append(months);
            sb.append(" ");

            if (months == 1) sb.append("Month");
            else sb.append("Months");
        }
        if (days > 0) {
            boolean exclamation = false;
            if (sb.length() != 0) sb.append(" and ");
            else {
                sb.append("Just ");
                exclamation = true;
            }

            sb.append(days);
            sb.append(" ");

            if (days == 1) sb.append("Day");
            else sb.append("Days");

            if (exclamation) sb.append("!");
        }

        if (sb.length() != 0) return sb.toString();
        else return "ERROR";
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

