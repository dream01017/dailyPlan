package ru.maksym.plan.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.widget.AdapterView;

import androidx.appcompat.app.AppCompatDelegate;

import ru.fazziclay.plan.R;
import ru.maksym.plan.app.App;
import ru.maksym.plan.app.receiver.QuickNoteReceiver;
import ru.maksym.plan.app.settings.SettingsManager;
import ru.fazziclay.plan.databinding.DialogAppSettingsBinding;
import ru.maksym.plan.util.SimpleSpinnerAdapter;

public class DialogAppSettings {
    private final Activity activity;
    private final Dialog dialog;
    private final DialogAppSettingsBinding binding;
    private final SettingsManager settingsManager;

    public DialogAppSettings(Activity activity) {
        this.activity = activity;
        this.dialog = new Dialog(activity, android.R.style.ThemeOverlay_Material);
        this.binding = DialogAppSettingsBinding.inflate(activity.getLayoutInflater());
        this.settingsManager = App.get(activity).getSettingsManager();
        setupThemeSpinner();

        binding.quickNoteCheckbox.setChecked(settingsManager.isQuickNote());
        binding.quickNoteCheckbox.setOnClickListener(v -> {
            settingsManager.setQuickNote(binding.quickNoteCheckbox.isChecked());
            if (settingsManager.isQuickNote()) {
                QuickNoteReceiver.sendQuickNoteNotification(activity);
            } else {
                QuickNoteReceiver.cancelQuickNoteNotification(activity);
            }
            settingsManager.save();
        });
    }

    private void setupThemeSpinner() {
        SimpleSpinnerAdapter<Integer> themeSpinnerAdapter = new SimpleSpinnerAdapter<Integer>(activity)
                .add(activity.getString(R.string.settings_theme_system), AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                .add(activity.getString(R.string.settings_theme_light), AppCompatDelegate.MODE_NIGHT_NO)
                .add(activity.getString(R.string.settings_theme_night), AppCompatDelegate.MODE_NIGHT_YES);

        binding.themeSpinner.setAdapter(themeSpinnerAdapter);
        binding.themeSpinner.setSelection(themeSpinnerAdapter.getValuePosition(settingsManager.getTheme()));
        binding.themeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int t = themeSpinnerAdapter.getItem(position);
                AppCompatDelegate.setDefaultNightMode(t);
                settingsManager.setTheme(t);
                settingsManager.save();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    public View getView() {
        return binding.getRoot();
    }

    public void show() {
        dialog.setContentView(getView());
        dialog.show();
    }
}
