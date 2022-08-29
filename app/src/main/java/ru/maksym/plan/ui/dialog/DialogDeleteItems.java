package ru.maksym.plan.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AlertDialog;

import ru.fazziclay.plan.R;
import ru.maksym.plan.app.App;
import ru.maksym.plan.app.items.item.Item;
import ru.fazziclay.plan.databinding.DialogPreviewDeleteItemsBinding;
import ru.maksym.plan.ui.other.ItemViewHolder;
import ru.maksym.plan.ui.other.item.ItemViewGenerator;
import ru.maksym.plan.util.MinBaseAdapter;

public class DialogDeleteItems {
    private final DialogPreviewDeleteItemsBinding binding;
    private final ItemViewGenerator itemViewGenerator;
    private final Dialog dialog;

    public DialogDeleteItems(Activity activity, Item[] items) {
        if (items.length == 0) {
            throw new RuntimeException("Empty list");
        }
        this.binding = DialogPreviewDeleteItemsBinding.inflate(activity.getLayoutInflater());

        this.dialog = new Dialog(activity, android.R.style.ThemeOverlay_Material);
        this.dialog.setContentView(binding.getRoot());
        this.itemViewGenerator = new ItemViewGenerator(activity, App.get().getItemManager(), null, null, true);

        binding.list.setAdapter(new MinBaseAdapter() {
            @Override
            public int getCount() {
                return items.length;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                Item item = items[position];
                ItemViewHolder itemViewHolder = new ItemViewHolder(activity);
                itemViewHolder.layout.addView(itemViewGenerator.generate(item, parent));
                return itemViewHolder.itemView;
            }
        });

        binding.deleteButton.setOnClickListener(v -> new AlertDialog.Builder(activity)
                .setTitle(activity.getString(R.string.dialog_previewDeleteItems_delete_title, String.valueOf(items.length)))
                .setNegativeButton(R.string.dialog_previewDeleteItems_delete_cancel, null)
                .setPositiveButton(R.string.dialog_previewDeleteItems_delete_apply, ((dialog1, which) -> {
                    for (Item item : items) {
                        item.delete();
                    }
                    dialog.cancel();
                }))
                .show());

        binding.cancelButton.setOnClickListener(v -> dialog.cancel());
    }

    public void show() {
        dialog.show();
    }
}
