package ru.maksym.plan.app.items.callback;

import ru.maksym.plan.app.items.item.Item;
import ru.maksym.plan.callback.Callback;
import ru.maksym.plan.callback.Status;

public interface OnItemStorageUpdate extends Callback {
    Status onAdded(Item item);
    Status onDeleted(Item item);
    Status onMoved(Item item, int from);
    Status onUpdated(Item item);
}
