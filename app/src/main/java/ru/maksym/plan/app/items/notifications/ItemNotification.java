package ru.maksym.plan.app.items.notifications;

import ru.maksym.plan.app.TickSession;
import ru.maksym.plan.app.items.item.Item;

public interface ItemNotification {
    boolean tick(TickSession tickSession, Item item);
}
