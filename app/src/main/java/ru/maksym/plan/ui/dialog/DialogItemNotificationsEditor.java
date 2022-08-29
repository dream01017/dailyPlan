package ru.maksym.plan.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import ru.fazziclay.plan.R;
import ru.maksym.plan.app.items.item.Item;
import ru.maksym.plan.app.items.notifications.DayItemNotification;
import ru.maksym.plan.app.items.notifications.ItemNotification;
import ru.fazziclay.plan.databinding.DialogItemNotificationBinding;
import ru.fazziclay.plan.databinding.DialogItemNotificationsEditorBinding;
import ru.fazziclay.plan.databinding.ItemNotificationBinding;
import ru.maksym.plan.util.MinBaseAdapter;
import ru.maksym.plan.util.time.ConvertMode;
import ru.maksym.plan.util.time.HumanTimeType;
import ru.maksym.plan.util.time.TimeUtil;

public class DialogItemNotificationsEditor {
    private final Activity activity;
    private final Item item;
    private final Runnable onApply;
    private final Dialog dialog;
    private final DialogItemNotificationsEditorBinding binding;
    private final View view;

    public DialogItemNotificationsEditor(Activity activity, Item item, Runnable o) {

        this.activity = activity;
        this.item = item;
        this.onApply = o;

        dialog = new Dialog(activity, android.R.style.ThemeOverlay_Material);
        dialog.setOnCancelListener(dialog -> onApply.run());

        binding = DialogItemNotificationsEditorBinding.inflate(activity.getLayoutInflater());
        view = binding.getRoot();

        binding.list.setAdapter(new MinBaseAdapter() {
            @Override
            public int getCount() {
                return item.getNotifications().size();
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                ItemNotification itemNotification = item.getNotifications().get(position);

                ItemNotificationBinding b = ItemNotificationBinding.inflate(activity.getLayoutInflater(), parent, false);

                if (itemNotification instanceof DayItemNotification) {
                    DayItemNotification d = (DayItemNotification) itemNotification;
                    b.text.setText(String.format("#%s - %s - %s", d.getNotificationId(), activity.getString(R.string.itemNotification_day), TimeUtil.convertToHumanTime(d.getTime(), ConvertMode.HHMM)));
                }

                b.delete.setOnClickListener(v -> {
                    new AlertDialog.Builder(activity)
                            .setTitle(R.string.dialogItem_delete_title)
                            .setPositiveButton(R.string.dialogItem_delete_apply, (ee, eee) -> {
                                item.getNotifications().remove(itemNotification);
                                item.save();
                                notifyDataSetChanged();
                            })
                            .setNegativeButton(R.string.dialogItem_delete_cancel, null)
                            .show();
                });


                b.getRoot().setOnClickListener(v -> {
                    DialogItemNotificationBinding l = DialogItemNotificationBinding.inflate(activity.getLayoutInflater());

                    DayItemNotification d = (DayItemNotification) itemNotification;

                    l.notificationId.setText(String.valueOf(d.getNotificationId()));
                    l.text.setText(d.getNotifyText());
                    l.title.setText(d.getNotifyTitle());
                    l.notifySubText.setText(d.getNotifySubText());
                    l.test.setOnClickListener(v2132321 -> {

                    });
                    l.time.setText(activity.getString(R.string.dialog_itemNotification_time, TimeUtil.convertToHumanTime(d.getTime(), ConvertMode.HHMM)));
                    l.time.setOnClickListener(v421213 -> new TimePickerDialog(activity, (view, hourOfDay, minute) -> {
                        d.setTime((hourOfDay * 60 * 60) + (minute * 60));
                        d.setLatestDayOfYear(0);
                        item.save();
                        l.time.setText(activity.getString(R.string.dialog_itemNotification_time, TimeUtil.convertToHumanTime(d.getTime(), ConvertMode.HHMM)));
                    }, TimeUtil.getHumanValue(d.getTime(), HumanTimeType.HOUR), TimeUtil.getHumanValue(d.getTime(), HumanTimeType.MINUTE_OF_HOUR), true).show());

                    new AlertDialog.Builder(activity)
                            .setView(l.getRoot())
                            .setPositiveButton(R.string.dialog_itemNotification_apply, (refre, werwer) -> {
                                d.setNotifyTitle(l.title.getText().toString());
                                d.setNotifyText(l.text.getText().toString());
                                d.setNotifySubText(l.notifySubText.getText().toString());
                                try {
                                    int i = Integer.parseInt(l.notificationId.getText().toString());
                                    d.setNotificationId(i);
                                } catch (Exception e) {
                                    Toast.makeText(activity, R.string.dialog_itemNotification_incorrectNotificationId, Toast.LENGTH_SHORT).show();
                                }
                                notifyDataSetChanged();
                                item.save();
                            })
                            .setNegativeButton(R.string.dialog_itemNotification_cancel, null)
                            .show();
                });

                return b.getRoot();
            }
        });

        binding.add.setOnClickListener(v -> {
            item.getNotifications().add(new DayItemNotification());
            item.save();
            ((BaseAdapter) binding.list.getAdapter()).notifyDataSetChanged();
        });
    }

    public void show() {
        dialog.setContentView(view);
        dialog.show();
    }
}
