package ru.maksym.plan.app.items;

import ru.maksym.plan.app.TickSession;
import ru.maksym.plan.app.items.callback.OnItemStorageUpdate;
import ru.maksym.plan.app.items.item.Item;
import ru.maksym.plan.callback.CallbackStorage;

public interface ItemStorage {
    Item[] getAllItems();
    int size();
    void addItem(Item item);
    void deleteItem(Item item);
    void move(int positionFrom, int positionTo);
    void tick(TickSession tickSession);
    void save();
    int getItemPosition(Item item);
    CallbackStorage<OnItemStorageUpdate> getOnUpdateCallbacks();
}
