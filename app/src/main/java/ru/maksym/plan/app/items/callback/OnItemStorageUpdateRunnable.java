package ru.maksym.plan.app.items.callback;

import ru.maksym.plan.app.items.item.Item;
import ru.maksym.plan.callback.Status;

public class OnItemStorageUpdateRunnable implements OnItemStorageUpdate {
    private final Runnable runnable;

    public OnItemStorageUpdateRunnable(Runnable runnable) {
        this.runnable = runnable;
    }

    @Override
    public Status onAdded(Item item) {
        runnable.run();
        return Status.NONE;
    }

    @Override
    public Status onDeleted(Item item) {
        runnable.run();
        return Status.NONE;
    }

    @Override
    public Status onMoved(Item item, int from) {
        runnable.run();
        return Status.NONE;
    }

    @Override
    public Status onUpdated(Item item) {
        runnable.run();
        return Status.NONE;
    }
}
