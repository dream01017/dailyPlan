package ru.maksym.plan.ui.other.item;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ru.fazziclay.plan.R;
import ru.maksym.plan.app.App;
import ru.maksym.plan.app.items.ItemManager;
import ru.maksym.plan.app.items.ItemStorage;
import ru.maksym.plan.app.items.ItemsRegistry;
import ru.maksym.plan.app.items.Selection;
import ru.maksym.plan.app.items.callback.OnItemStorageUpdate;
import ru.maksym.plan.app.items.callback.OnSelectionChanged;
import ru.maksym.plan.app.items.item.Item;
import ru.maksym.plan.app.items.item.TextItem;
import ru.maksym.plan.callback.CallbackImportance;
import ru.maksym.plan.callback.Status;
import ru.maksym.plan.ui.dialog.DialogItem;
import ru.maksym.plan.ui.dialog.DialogTextItemEditText;
import ru.maksym.plan.ui.other.ItemViewHolder;
import ru.maksym.plan.util.ResUtil;

public class ItemStorageDrawer {
    private final Activity activity;
    private final ItemManager itemManager;
    private final ItemStorage itemStorage;
    private final RecyclerView view;
    private RecyclerView.Adapter<ItemViewHolder> adapter;
    private ItemViewGenerator itemViewGenerator;

    private boolean destroyed = false;
    private boolean created = false;

    private final List<Selection> visibleSelections = new ArrayList<>();
    private final OnItemStorageUpdate onItemStorageUpdate = new DrawerOnItemStorageUpdated();
    private final OnSelectionChanged onSelectionChanged = new OnSelectionChanged() {
        @Override
        public void run(List<Selection> selections) {
            List<Selection> toUpdate = new ArrayList<>();

            for (Selection visibleSelection : visibleSelections) {
                boolean contain = false;
                for (Selection selection : selections) {
                    if (visibleSelection.getItem() == selection.getItem()) {
                        contain = true;
                        break;
                    }
                }
                if (!contain) {
                    toUpdate.add(visibleSelection);
                }
            }

            for (Selection selection : selections) {
                boolean contain = false;
                for (Selection visibleSelection : visibleSelections) {
                    if (visibleSelection.getItem() == selection.getItem()) {
                        contain = true;
                        break;
                    }
                }
                if (!contain) {
                    toUpdate.add(selection);
                }
            }

            for (Selection selection : toUpdate) {
                int pos = itemStorage.getItemPosition(selection.getItem());
                adapter.notifyItemChanged(pos);
            }
            visibleSelections.clear();
            visibleSelections.addAll(selections);
        }
    };

    private final OnItemClick onItemClick;
    private final DialogItem dialogItem;
    private final boolean previewMode;
    private final String path;
    private ItemViewWrapper itemViewWrapper = null;

    // Public
    public ItemStorageDrawer(Activity activity, ItemManager itemManager, ItemStorage itemStorage, String path) {
        this(activity, itemManager, itemStorage, path, null, false);
    }

    public ItemStorageDrawer(Activity activity, ItemManager itemManager, ItemStorage itemStorage, String path, OnItemClick onItemClick, boolean previewMode) {
        this.activity = activity;
        this.itemManager = itemManager;
        this.itemStorage = itemStorage;
        this.path = path;
        this.view = new RecyclerView(activity);
        this.onItemClick = onItemClick;
        this.previewMode = previewMode;
        this.view.setLayoutManager(new LinearLayoutManager(activity));
        this.view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1));
        this.itemManager.getOnSelectionUpdated().addCallback(CallbackImportance.DEFAULT, onSelectionChanged);

        this.dialogItem = new DialogItem(this.activity, this.itemManager);
        this.itemViewGenerator = new ItemViewGenerator(this.activity, this.itemManager, this.path, (item) -> {
            if (this.onItemClick == null) {
                if (!previewMode) actionItem(item, itemManager.getItemOnClickAction());
            } else {
                this.onItemClick.run(item);
            }
        }, previewMode);
    }

    public void create() {
        if (destroyed) {
            throw new RuntimeException("ItemStorageDrawer destroyed!");
        }
        if (created) {
            throw new RuntimeException("ItemStorageDrawer created!");
        }
        this.created = true;
        this.adapter = new DrawerAdapter();
        this.view.setAdapter(adapter);
        this.itemStorage.getOnUpdateCallbacks().addCallback(CallbackImportance.DEFAULT, onItemStorageUpdate);

        if (!previewMode) new ItemTouchHelper(new DrawerTouchCallback()).attachToRecyclerView(view);
    }

    public void destroy() {
        if (!created) {
            throw new RuntimeException("ItemStorageDrawer no created!");
        }
        if (destroyed) {
            throw new RuntimeException("ItemStorageDrawer destroyed!");
        }
        destroyed = true;
        this.itemStorage.getOnUpdateCallbacks().deleteCallback(onItemStorageUpdate);
        this.itemManager.getOnSelectionUpdated().deleteCallback(onSelectionChanged);
        this.view.setAdapter(null);
        this.itemViewGenerator = null;
        this.adapter = null;
    }

    public View getView() {
        return this.view;
    }

    public void setItemViewWrapper(ItemViewWrapper itemViewWrapper) {
        this.itemViewWrapper = itemViewWrapper;
    }

    // Private
    private class DrawerOnItemStorageUpdated implements OnItemStorageUpdate {
        @Override
        public Status onAdded(Item item) {
            adapter.notifyItemInserted(getItemPos(item));
            return Status.NONE;
        }

        @Override
        public Status onDeleted(Item item) {
            adapter.notifyItemRemoved(getItemPos(item));
            return Status.NONE;
        }

        @Override
        public Status onMoved(Item item, int from) {
            adapter.notifyItemMoved(from, getItemPos(item));
            return Status.NONE;
        }

        @Override
        public Status onUpdated(Item item) {
            adapter.notifyItemChanged(getItemPos(item));
            return Status.NONE;
        }

        private int getItemPos(Item item) {
            return ItemStorageDrawer.this.itemStorage.getItemPosition(item);
        }
    }

    private View generateViewForItem(Item item) {
        return itemViewGenerator.generate(item, view);
    }

    private class DrawerAdapter extends RecyclerView.Adapter<ItemViewHolder> {
        @NonNull
        @Override
        public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ItemViewHolder(activity);
        }

        @Override
        public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
            Item item = itemStorage.getAllItems()[position];
            View view = generateViewForItem(item);

            holder.layout.removeAllViews();
            holder.layout.addView((itemViewWrapper != null) ? itemViewWrapper.wrap(item, view) : view);

            if (itemManager.isSelected(item)) {
                holder.layout.setForeground(new ColorDrawable(ResUtil.getAttrColor(activity, R.attr.item_selectionForegroundColor)));
            } else {
                holder.layout.setForeground(null);
            }
        }

        @Override
        public int getItemCount() {
            return itemStorage.getAllItems().length;
        }
    }

    private class DrawerTouchCallback extends ItemTouchHelper.SimpleCallback {
        private static final int DRAG_DIRS = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        private static final int SWIPE_DIRS = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.START | ItemTouchHelper.END;

        public DrawerTouchCallback() {
            super(DRAG_DIRS, SWIPE_DIRS);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            int positionFrom = viewHolder.getAdapterPosition();
            int positionTo = target.getAdapterPosition();

            //! NOTE: Adapter receive notify signal from callbacks!
            //ItemUIDrawer.this.adapter.notifyItemMoved(positionFrom, positionTo);
            ItemStorageDrawer.this.itemStorage.move(positionFrom, positionTo);
            return true;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            if (direction == ItemTouchHelper.LEFT) {
                int positionFrom = viewHolder.getAdapterPosition();
                Item item = ItemStorageDrawer.this.itemStorage.getAllItems()[positionFrom];
                item.visibleChanged();
                actionItem(item, itemManager.getItemOnLeftAction());

            } else if (direction == ItemTouchHelper.RIGHT) {
                int position = viewHolder.getAdapterPosition();
                Item item = ItemStorageDrawer.this.itemStorage.getAllItems()[position];
                item.visibleChanged();
                ItemViewHolder itemViewHolder = (ItemViewHolder) viewHolder;
                showRightMenu(item, itemViewHolder.itemView);
            }
        }
    }

    private void actionItem(Item item, ItemManager.ItemAction action) {
        switch (action) {
            case OPEN_EDIT_DIALOG:
                dialogItem.edit(item);
                break;

            case SELECT_ON:
                itemManager.selectItem(new Selection(itemStorage, item));                item.visibleChanged();
                item.visibleChanged();
                break;

            case SELECT_OFF:
                itemManager.deselectItem(item);
                item.visibleChanged();

                break;

            case MINIMIZE_REVERT:
                item.setMinimize(!item.isMinimize());
                item.visibleChanged();
                item.save();
                break;

            case MINIMIZE_OFF:
                item.setMinimize(false);
                item.visibleChanged();
                item.save();
                break;


            case MINIMIZE_ON:
                item.setMinimize(true);
                item.visibleChanged();
                item.save();
                break;

            case SELECT_REVERT:
                if (itemManager.isSelected(item)) {
                    itemManager.deselectItem(item);
                } else {
                    itemManager.selectItem(new Selection(itemStorage, item));
                }
                item.visibleChanged();
                break;

            case DELETE_REQUEST:
                new AlertDialog.Builder(activity)
                        .setTitle(R.string.dialogItem_delete_title)
                        .setNegativeButton(R.string.dialogItem_delete_cancel, null)
                        .setPositiveButton(R.string.dialogItem_delete_apply, ((dialog1, which) -> item.delete()))
                        .show();

        }
    }

    private void showRightMenu(Item item, View itemView) {
        ItemManager itemManager = App.get(activity).getItemManager();
        PopupMenu menu = new PopupMenu(activity, itemView);
        menu.setForceShowIcon(true);
        menu.inflate(R.menu.menu_item);
        menu.getMenu().findItem(R.id.minimize).setChecked(item.isMinimize());
        menu.getMenu().findItem(R.id.selected).setChecked(itemManager.isSelected(item));
        menu.getMenu().setGroupEnabled(R.id.textItem, item instanceof TextItem);
        if (item instanceof TextItem) {
            TextItem textItem = (TextItem) item;
            menu.getMenu().findItem(R.id.textItem_clickableUrls).setChecked(textItem.isClickableUrls());
        }
        menu.setOnMenuItemClickListener(menuItem -> {
            boolean save = false;
            ItemManager.ItemAction itemAction = null;
            switch (menuItem.getItemId()) {
                case R.id.edit:
                    itemAction = ItemManager.ItemAction.OPEN_EDIT_DIALOG;
                    break;

                case R.id.minimize:
                    itemAction = ItemManager.ItemAction.MINIMIZE_REVERT;
                    break;

                case R.id.selected:
                    itemAction = ItemManager.ItemAction.SELECT_REVERT;
                    break;

                case R.id.copy:
                    int currPos = itemStorage.getItemPosition(item);
                    Item copyItem;
                    try {
                        copyItem = ItemsRegistry.REGISTRY.getItemInfoByClass(item.getClass()).copy(item);
                    } catch (Exception e) {
                        Toast.makeText(activity, activity.getString(R.string.menuItem_copy_exception, e.toString()), Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    DialogItem dialogItem = new DialogItem(activity, itemManager);
                    dialogItem.edit(copyItem);

                    itemStorage.addItem(copyItem);
                    int createPos = itemStorage.getItemPosition(copyItem);
                    itemStorage.move(createPos, currPos + 1);
                    break;

                case R.id.textItem_clickableUrls:
                    if (item instanceof TextItem) {
                        TextItem textItem = (TextItem) item;
                        textItem.setClickableUrls(!textItem.isClickableUrls());
                        save = true;
                    }
                    break;

                case R.id.textItem_editText:
                    if (item instanceof TextItem) {
                        TextItem textItem = (TextItem) item;
                        DialogTextItemEditText d = new DialogTextItemEditText(activity, textItem);
                        d.show();
                    }
                    break;
            }

            if (itemAction != null) actionItem(item, itemAction);
            if (save) item.save();
            item.visibleChanged();
            return true;
        });
        menu.show();
    }

    @FunctionalInterface
    public interface ItemViewWrapper {
        View wrap(Item item, View view);
    }
}
