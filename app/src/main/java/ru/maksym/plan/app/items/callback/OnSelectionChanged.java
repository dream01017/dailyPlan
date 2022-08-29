package ru.maksym.plan.app.items.callback;

import java.util.List;

import ru.maksym.plan.app.items.Selection;
import ru.maksym.plan.callback.Callback;

@FunctionalInterface
public interface OnSelectionChanged extends Callback {
    void run(List<Selection> selections);
}
