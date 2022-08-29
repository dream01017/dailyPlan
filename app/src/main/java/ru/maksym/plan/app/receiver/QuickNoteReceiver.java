package ru.maksym.plan.app.receiver;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.core.app.RemoteInput;

import ru.fazziclay.plan.R;
import ru.maksym.plan.app.App;
import ru.maksym.plan.app.items.item.TextItem;

public class QuickNoteReceiver extends BroadcastReceiver {
    public static final String REMOTE_INPUT_KEY = "opentoday_quick_note_remote_input";
    public static final int NOTIFICATION_ID = 10;
    public static final String NOTIFICATION_CHANNEL = "quick_note";

    public static void sendQuickNoteNotification(Context context) {
        NotificationCompat.Action action = new NotificationCompat.Action.Builder(R.drawable.ic_launcher_foreground, context.getString(R.string.quickNote), PendingIntent.getBroadcast(context, 0, new Intent(context, QuickNoteReceiver.class), 0))
                .addRemoteInput(new RemoteInput.Builder(QuickNoteReceiver.REMOTE_INPUT_KEY).setLabel(context.getString(R.string.quickNote)).build())
                .build();

        context.getSystemService(NotificationManager.class).notify(QuickNoteReceiver.NOTIFICATION_ID,
                new NotificationCompat.Builder(context, QuickNoteReceiver.NOTIFICATION_CHANNEL)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setSilent(true)
                        .setSound(null)
                        .setShowWhen(false)
                        .addAction(action)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .build());
    }

    public static void cancelQuickNoteNotification(Context context) {
        context.getSystemService(NotificationManager.class).cancel(QuickNoteReceiver.NOTIFICATION_ID);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        App app = App.get(context);
        Bundle bundle = RemoteInput.getResultsFromIntent(intent);
        if (bundle != null) {
            String s = String.valueOf(bundle.getCharSequence(REMOTE_INPUT_KEY));
            app.getItemManager().addItem(new TextItem(context.getString(R.string.quickNote) + ": " + s));

            sendQuickNoteNotification(context);
        }
    }
}
