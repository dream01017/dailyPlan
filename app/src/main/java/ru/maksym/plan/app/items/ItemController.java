package ru.maksym.plan.app.items;

import ru.maksym.plan.app.items.item.Item;

public abstract class ItemController {
    public abstract void delete(Item item);
    public abstract void save(Item item);
    public abstract void updateUi(Item item);
}
