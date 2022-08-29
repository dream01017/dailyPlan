package ru.maksym.plan.ui.other.item;

import ru.maksym.plan.app.items.item.Item;

@FunctionalInterface
public interface OnItemClick {
    void run(Item item);
}
