package com.adriantache.uibc_app;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.Calendar;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import static androidx.core.app.NotificationCompat.CATEGORY_EVENT;
import static androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC;

/**
 * Implementation of App Widget functionality.
 */
public class UibcWidget extends AppWidgetProvider {
    private static final int ERROR_VALUE = -1;
    private static boolean fromOrNext = true;
    private static int LAST_NOTIFICATION_YEAR;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        String description;
        if (fromOrNext) {
            description = "Time since uîbc:";
        } else {
            description = "Next anniversary:";
        }

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.uibc_widget);
        if (anniversary()) {
            String years = getYears();

            views.setTextViewText(R.id.description_text, "Together " + years + " Years!");
            views.setTextViewText(R.id.time, "I LOVE YOU!");
            views.setImageViewResource(R.id.thumbnail, R.drawable.heart);

            if (isItTenAM() && LAST_NOTIFICATION_YEAR != Calendar.getInstance().get(Calendar.YEAR))
                triggerNotification(context, years);
        } else {
            views.setTextViewText(R.id.description_text, description);
            views.setTextViewText(R.id.time, getTime(!fromOrNext));
            views.setImageViewResource(R.id.thumbnail, R.drawable.logo);
        }

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private static boolean isItTenAM() {
        Calendar today = Calendar.getInstance();
        return today.get(Calendar.HOUR_OF_DAY) == 10;
    }

    private static void triggerNotification(Context context, String years) {
        final String NOTIFICATION_CHANNEL = "uibc";

        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //define the importance level of the notification
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            //build the actual notification channel, giving it a unique ID and name
            NotificationChannel channel =
                    new NotificationChannel(NOTIFICATION_CHANNEL, NOTIFICATION_CHANNEL, importance);

            //set a description for the channel
            String description = "A channel which shows uibc anniversaries";
            channel.setDescription(description);

            //set notification LED colour
            channel.setLightColor(Color.RED);

            // Register the channel with the system
            NotificationManager notificationManager = (NotificationManager) context.
                    getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        //build the notification
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL)
                        .setSmallIcon(R.drawable.logo)
                        .setContentTitle("Happy " + years + " years anniversary!")
                        .setContentText("Your uibc loves you very much <3")
                        .setAutoCancel(true)
                        .setCategory(CATEGORY_EVENT)
                        .setColor(0xFF4081)
                        .setVisibility(VISIBILITY_PUBLIC)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        //trigger the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(12, notificationBuilder.build());

        //set notification year to prevent triggering multiple notifications for one year
        LAST_NOTIFICATION_YEAR = Calendar.getInstance().get(Calendar.YEAR);
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
        int todayMonth = todayC.get(Calendar.MONTH) + 1;

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
        int todayMonth = todayC.get(Calendar.MONTH) + 1;
        int todayYear = todayC.get(Calendar.YEAR);

        Log.i("TODAY", "anniversary: " + todayDay + " " + todayMonth + " " + todayYear);

        int days;
        int months = ERROR_VALUE;
        int years = ERROR_VALUE;

        //calculate time since anniversary
        if (fromOrNext) {
            //ignore days to simplify things
            days = 0;

            if (todayMonth == anniversaryMonth) {
                if (todayDay < anniversaryDay) {
                    months = 11;
                    years = todayYear - anniversaryYear - 1;
                } else if (todayDay > anniversaryDay) {
                    months = 0;
                    years = todayYear - anniversaryYear;
                }
            } else if (todayMonth < anniversaryMonth) {
                years = todayYear - anniversaryYear - 1;

                if (todayDay < anniversaryDay) {
                    months = 12 - anniversaryMonth + todayMonth - 1;
                } else if (todayDay == anniversaryDay) {
                    months = 12 - anniversaryMonth + todayMonth;
                } else {
                    months = 12 - anniversaryMonth + todayMonth;
                }
            } else {
                years = todayYear - anniversaryYear;

                if (todayDay < anniversaryDay) {
                    months = todayMonth - anniversaryMonth - 1;
                } else if (todayDay == anniversaryDay) {
                    months = todayMonth - anniversaryMonth;
                } else {
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

        if (days < 0 || months < 0 || years < 0) return "ERROR";

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
            //flip from or next flag
            fromOrNext = !fromOrNext;

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

