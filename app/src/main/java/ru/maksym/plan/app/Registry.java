package ru.maksym.plan.app;

import ru.maksym.plan.app.items.ItemsRegistry;
import ru.maksym.plan.app.items.notifications.ItemNotificationsRegistry;

public class Registry {
    public static final ItemsRegistry ITEMS = ItemsRegistry.REGISTRY;
    public static final ItemNotificationsRegistry ITEM_NOTIFICATIONS = ItemNotificationsRegistry.REGISTRY;
}
