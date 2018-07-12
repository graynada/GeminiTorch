package harby.graham.geminitorch;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.RemoteViews;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Implementation of App Widget functionality.
 */
public class TorchWidget extends AppWidgetProvider {

    public static String WIDGET_BUTTON = "harby.graham.geminitorch.WIDGET_BUTTON";
    NotificationManager mNotificationManager;
    Method method;
    String TAG = "Gemini torch widget";
    static int on = 0;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.torch_widget);
        Intent intent = new Intent(WIDGET_BUTTON);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.appwidget_button, pendingIntent );
        views.setImageViewResource(R.id.appwidget_button, R.drawable.off);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.torch_widget);
        if (WIDGET_BUTTON.equals(intent.getAction())) {
            reflect(context);
            if(on == 0){
                on = 1;
                views.setImageViewResource(R.id.appwidget_button, R.drawable.on);
            }
            else {
                views.setImageViewResource(R.id.appwidget_button, R.drawable.off);
                on = 0;
            }
            for(int i = 1; i <= 5; i++){
                onOne(i);
            }
            appWidgetManager.updateAppWidget(new ComponentName(context, TorchWidget.class), views);
        }
    }

    @Override
    public void onEnabled(Context context) {

    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    public void reflect(Context context) {
        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        try {
            method = mNotificationManager.getClass().getMethod("openLed", int.class, int.class, int.class, int.class, int.class);
        } catch (SecurityException e) {
            Log.e(TAG, "Security prevents reflection");
        } catch (NoSuchMethodException e) {
            Log.e(TAG, "No method");
        }

    }

    void onOne(int led){
        try {
            method.invoke(mNotificationManager, led, on, on, on, 0);
        } catch (IllegalAccessException e) {
            Log.e(TAG, "IllegalAccessException");
        } catch (InvocationTargetException e) {
            Log.e(TAG, "InvocationTargetException");
        }
    }

}

