package ru.maksym.plan.app.items.notifications;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import org.json.JSONObject;

import java.util.Calendar;

import ru.fazziclay.plan.R;
import ru.maksym.plan.app.App;
import ru.maksym.plan.app.TickSession;
import ru.maksym.plan.app.items.item.Item;
import ru.maksym.plan.app.receiver.ItemsTickReceiver;

public class DayItemNotification implements ItemNotification {
    public static final ItemNotificationIETool IE_TOOL = new IeTool();
    private static class IeTool extends ItemNotificationIETool {
        @Override
        public JSONObject exportNotification(ItemNotification itemNotification) throws Exception {
            DayItemNotification d = (DayItemNotification) itemNotification;
            return new JSONObject()
                    .put("notificationId", d.notificationId)
                    .put("notifyTitle", d.notifyTitle)
                    .put("notifyText", d.notifyText)
                    .put("latestDayOfYear", d.latestDayOfYear)
                    .put("notifySubText", d.notifySubText)
                    .put("time", d.time);
        }

        @Override
        public ItemNotification importNotification(JSONObject json) {
            DayItemNotification o = new DayItemNotification();
            o.notificationId = json.optInt("notificationId", 543);
            o.notifyTitle = json.optString("notifyTitle", "");
            o.notifyText = json.optString("notifyText", "");
            o.notifySubText = json.optString("notifySubText", "");
            o.latestDayOfYear = json.optInt("latestDayOfYear", 0);
            o.time = json.optInt("time", 0);

            return o;
        }
    }

    private int notificationId = 0;
    private String notifyTitle;
    private String notifyText;
    private String notifySubText;
    private int latestDayOfYear;
    private int time;

    public DayItemNotification() {

    }

    public DayItemNotification(int notificationId, String notifyTitle, String notifyText, String notifySubText, int time) {
        this.notificationId = notificationId;
        this.notifyTitle = notifyTitle;
        this.notifyText = notifyText;
        this.notifySubText = notifySubText;
        this.time = time;
    }

    @Override
    public boolean tick(TickSession tickSession, Item item) {
        int dayofy = tickSession.getGregorianCalendar().get(Calendar.DAY_OF_YEAR);

        if (dayofy != latestDayOfYear) {
            if (tickSession.getDayTime() >= time) {
                sendNotify(tickSession.getContext());
                latestDayOfYear = dayofy;
                tickSession.saveNeeded();

                try {
                    AlarmManager alarmManager = tickSession.getContext().getSystemService(AlarmManager.class);
                    alarmManager.set(AlarmManager.RTC_WAKEUP, tickSession.getNoTimeCalendar().getTimeInMillis() + (24*60*60*1000L) + (this.time * 1000L) - 1000, PendingIntent.getBroadcast(tickSession.getContext(), 0, new Intent(tickSession.getContext(), ItemsTickReceiver.class).putExtra(ItemsTickReceiver.EXTRA_PERSONAL_TICK, new String[]{item.getId().toString()}).putExtra("debugMessage", "dayItemNotification is work :)"), 0));
                } catch (Exception e) {
                    Log.e("DayItemNotification", "AlarmManager in item experiment is not complete", e);
                }

                return true;
            }
        }
        return false;
    }

    public void sendNotify(Context context) {
        context.getSystemService(NotificationManager.class).notify(this.notificationId,
                new NotificationCompat.Builder(context, App.NOTIFICATION_ITEMS_CHANNEL)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(notifyTitle)
                        .setContentText(notifyText)
                        .setSubText(notifySubText)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .build());
    }

    public int getLatestDayOfYear() {
        return latestDayOfYear;
    }

    public void setLatestDayOfYear(int latestDayOfYear) {
        this.latestDayOfYear = latestDayOfYear;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    public String getNotifyTitle() {
        return notifyTitle;
    }

    public void setNotifyTitle(String notifyTitle) {
        this.notifyTitle = notifyTitle;
    }

    public String getNotifyText() {
        return notifyText;
    }

    public void setNotifyText(String notifyText) {
        this.notifyText = notifyText;
    }

    public String getNotifySubText() {
        return notifySubText;
    }

    public void setNotifySubText(String notifySubText) {
        this.notifySubText = notifySubText;
    }
}
