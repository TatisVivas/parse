package com.example.parse.boot;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.parse.MainActivity;
import com.example.parse.R;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.livequery.ParseLiveQueryClient;
import com.parse.livequery.SubscriptionHandling;

public class BackgroundBootService extends Service {
    private static final String TAG = "PARSE_APP_BOOT";
    private static final String PARSE_APP_CHANNEL = "PARSE_APP_CHANNEL";

    //suscription to changes
    ParseLiveQueryClient parseLiveQueryClient;
    ParseQuery<ParseObject> parseQuery;
    SubscriptionHandling<ParseObject> subscriptionHandling;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    public void onDestroy() {
        Log.i(TAG, "Parse APP BOOT Service has been stopped");
        Toast.makeText(this, "Parse APP Stopped", Toast.LENGTH_LONG).show();
        if (parseQuery != null && parseLiveQueryClient != null)
            parseLiveQueryClient.unsubscribe(parseQuery);
    }

    private void dataChanged(ParseQuery<ParseObject> query) {
        Log.i(TAG, "An event on the Cloud has happened!! ");
        sendNotification("Parse APP", "An event on the Cloud has happened!! ", R.drawable.ic_baseline_cloud_circle_24, MainActivity.class );
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotificationChannel();

        startForeground(2, buildComplexNotification("ParseApp Started", "Connected to Parse Cloud", R.drawable.ic_baseline_anchor_24, MainActivity.class));
        Toast.makeText(this, "ParseApp Started", Toast.LENGTH_LONG).show();
        parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient();
        parseQuery = ParseQuery.getQuery("SmartUser");
        subscriptionHandling = parseLiveQueryClient.subscribe(parseQuery);
        subscriptionHandling.handleEvents(new SubscriptionHandling.HandleEventsCallback<ParseObject>() {
            @Override
            public void onEvents(ParseQuery<ParseObject> query, SubscriptionHandling.Event event, ParseObject object) {
                dataChanged(query);
            }
        });
        return START_STICKY;
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "channel";
            String description = "channel description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            //IMPORTANCE_MAX MUESTRA LA NOTIFICACIÓN ANIMADA
            NotificationChannel channel = new NotificationChannel(PARSE_APP_CHANNEL, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public Notification buildComplexNotification(String title, String message, int icon, Class target){

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, PARSE_APP_CHANNEL);
        mBuilder.setSmallIcon(icon);
        mBuilder.setContentTitle(title);
        mBuilder.setContentText(message);
        mBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT);

        //Acción asociada a la notificación
        Intent intent = new Intent(this, target);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setAutoCancel(true); //Remueve la notificación cuando se toca

        int notificationId = 001;
        //NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        // notificationId es un entero unico definido para cada notificacion que se lanza
        //notificationManager.notify(notificationId, mBuilder.build());
        return mBuilder.build();
    }

    private void sendNotification(String title, String message, int icon, Class target){
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(7, buildComplexNotification(title, message, icon, target));
    }

}
