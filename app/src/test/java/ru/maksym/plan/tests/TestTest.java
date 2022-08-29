package ru.maksym.plan.tests;

import org.junit.Test;

import java.io.File;

import ru.maksym.plan.app.items.ItemManager;
import ru.maksym.plan.app.items.item.TextItem;
import ru.maksym.plan.util.DebugUtil;

public class TestTest {
    @Test
    public void ddd() {
        ItemManager itemManager = new ItemManager(new File("./test/items.json"));
        itemManager.addItem(new TextItem("owo"));
        itemManager.save();
        DebugUtil.sleep(1000);
    }
}
