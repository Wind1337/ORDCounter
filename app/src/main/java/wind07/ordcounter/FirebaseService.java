package wind07.ordcounter;

import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaseService extends FirebaseMessagingService {
    public FirebaseService() {
    }
    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.d("INFO", "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        //sendRegistrationToServer(token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d("FIREBASE", "From: " + remoteMessage.getFrom());
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d("FIREBASE", "Message data payload: " + remoteMessage.getData());

            //if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
                //scheduleJob();
            //} //else {
                // Handle message within 10 seconds
                //handleNow();
            //}
        }
        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();
            Log.d("FIREBASE", "Message Notification Title: " + title);
            Log.d("FIREBASE", "Message Notification Body: " + body);
            if (title.equals("Monthly ORD Countdown")){
                NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "FCM")
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
                notificationManager.notify(1, builder.build());
            }
        }
    }
}
