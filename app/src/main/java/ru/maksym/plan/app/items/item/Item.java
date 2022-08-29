package ru.maksym.plan.app.items.item;

import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ru.maksym.plan.annotation.Getter;
import ru.maksym.plan.annotation.RequireSave;
import ru.maksym.plan.annotation.SaveKey;
import ru.maksym.plan.annotation.Setter;
import ru.maksym.plan.app.TickSession;
import ru.maksym.plan.app.items.ItemController;
import ru.maksym.plan.app.items.ItemImportExportTool;
import ru.maksym.plan.app.items.notifications.ItemNotification;
import ru.maksym.plan.app.items.notifications.ItemNotificationIETool;
import ru.maksym.plan.app.items.notifications.ItemNotificationsRegistry;

public abstract class Item implements Cloneable {
    private static final String DEFAULT_BACKGROUND_COLOR = "#99999999";

    // START - Save
    public static class ItemIETool extends ItemImportExportTool {
        @NonNull
        @Override
        public JSONObject exportItem(@NonNull Item item) throws Exception {
            return new JSONObject()
                    .put("id", item.id.toString())
                    .put("viewMinHeight", item.viewMinHeight)
                    .put("viewBackgroundColor", item.viewBackgroundColor)
                    .put("viewCustomBackgroundColor", item.viewCustomBackgroundColor)
                    .put("minimize", item.minimize)
                    .put("notifications", exportNotifications(item.notifications));
        }

        private final Item defaultValues = new Item(){};
        @NonNull
        @Override
        public Item importItem(@NonNull JSONObject json, Item item) throws Exception {
            applyId(item, json);
            item.viewMinHeight = json.optInt("viewMinHeight", defaultValues.viewMinHeight);
            item.viewBackgroundColor = json.optInt("viewBackgroundColor", defaultValues.viewBackgroundColor);
            item.viewCustomBackgroundColor = json.optBoolean("viewCustomBackgroundColor", defaultValues.viewCustomBackgroundColor);
            item.minimize = json.optBoolean("minimize", defaultValues.minimize);
            JSONArray jsonArray = json.optJSONArray("notifications");
            item.notifications = importNotifications(jsonArray != null ? jsonArray : new JSONArray());
            return item;
        }

        private void applyId(Item o, JSONObject json) {
            String stringId = json.optString("id", null);
            if (stringId == null) {
                o.id = UUID.randomUUID();
            } else {
                try {
                    o.id = UUID.fromString(stringId);
                } catch (Exception e) {
                    o.id = UUID.randomUUID();
                }
            }
        }

        private JSONArray exportNotifications(List<ItemNotification> notifications) throws Exception {
            final JSONArray jsonArray = new JSONArray();
            for (ItemNotification notification : notifications) {
                ItemNotificationsRegistry.ItemNotificationInfo itemNotificationInfo = ItemNotificationsRegistry.REGISTRY.getByClass(notification.getClass());
                jsonArray.put(itemNotificationInfo.getIeTool().exportNotification(notification)
                        .put("type", itemNotificationInfo.getStringType()));
            }
            return jsonArray;
        }

        private List<ItemNotification> importNotifications(JSONArray json) throws Exception {
            final List<ItemNotification> result = new ArrayList<>();

            int i = 0;
            while (i < json.length()) {
                JSONObject jsonNotification = json.getJSONObject(i);
                String type = jsonNotification.getString("type");
                ItemNotificationIETool ieTool = ItemNotificationsRegistry.REGISTRY.getByStringType(type).getIeTool();
                result.add(ieTool.importNotification(jsonNotification));
                i++;
            }

            return result;
        }
    }
    // END - Save

    @NonNull @RequireSave @SaveKey(key = "id")
    private UUID id;

    @SaveKey(key = "viewMinHeight") @RequireSave
    private int viewMinHeight = 0; // минимальная высота

    @SaveKey(key = "viewBackgroundColor") @RequireSave
    private int viewBackgroundColor = Color.parseColor(DEFAULT_BACKGROUND_COLOR); // фоновый цвет

    @SaveKey(key = "viewCustomBackgroundColor") @RequireSave
    private boolean viewCustomBackgroundColor = false; // юзаем ли фоновый цвет

    @SaveKey(key = "minimize") @RequireSave
    private boolean minimize = false;

    @NonNull @SaveKey(key = "notifications") @RequireSave
    private List<ItemNotification> notifications = new ArrayList<>();

    @Nullable private ItemController controller = null;

    // Copy constructor
    public Item(@Nullable Item copy) {
        if (copy != null) {
            this.id = copy.id != null ? copy.id : UUID.randomUUID();
            this.viewMinHeight = copy.viewMinHeight;
            this.viewBackgroundColor = copy.viewBackgroundColor;
            this.viewCustomBackgroundColor = copy.viewCustomBackgroundColor;
            this.minimize = copy.minimize;
            this.notifications = new ArrayList<>(copy.notifications);
            this.controller = copy.controller;
        } else {
            this.id = UUID.randomUUID();
        }
    }

    public Item() {
        this(null);
    }

    public void delete() {
        if (controller != null) controller.delete(this);
    }

    public void save() {
        if (controller != null) controller.save(this);
    }

    public void visibleChanged() {
        if (controller != null) controller.updateUi(this);
    }

    public void tick(TickSession tickSession) {
        for (ItemNotification notification : notifications) {
            notification.tick(tickSession, this);
        }
    }

    public Item regenerateId() {
        this.id = UUID.randomUUID();
        return this;
    }

    // Getters & Setters
    @Getter @NonNull public UUID getId() { return id; }
    @Setter public void setId(@NonNull UUID id) { this.id = id; }
    @Getter public int getViewMinHeight() { return viewMinHeight; }
    @Setter public void setViewMinHeight(int v) { this.viewMinHeight = v; }
    @Getter public int getViewBackgroundColor() { return viewBackgroundColor; }
    @Setter public void setViewBackgroundColor(int v) { this.viewBackgroundColor = v; }
    @Getter public boolean isViewCustomBackgroundColor() { return viewCustomBackgroundColor; }
    @Setter public void setViewCustomBackgroundColor(boolean v) { this.viewCustomBackgroundColor = v; }
    @Getter public boolean isMinimize() { return minimize; }
    @Setter public void setMinimize(boolean minimize) { this.minimize = minimize; }
    @Setter public void setController(@Nullable ItemController controller) { this.controller = controller; }
    @Getter @NonNull public List<ItemNotification> getNotifications() { return notifications; }
}
