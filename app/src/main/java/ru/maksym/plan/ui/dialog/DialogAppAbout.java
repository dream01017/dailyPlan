package ru.maksym.plan.ui.dialog;

import static ru.maksym.plan.util.InlineUtil.fcu_viewOnClick;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;

import ru.maksym.plan.app.App;
import ru.fazziclay.plan.databinding.DialogAppAboutBinding;
import ru.maksym.plan.ui.activity.OpenSourceLicensesActivity;
import ru.maksym.plan.util.InlineUtil;

public class DialogAppAbout {
    private final DialogAppAboutBinding binding;
    private final Dialog dialog;

    public DialogAppAbout(Activity activity) {
        // View
        binding = DialogAppAboutBinding.inflate(activity.getLayoutInflater());
        binding.textVersion.setText(App.VERSION_NAME);
        binding.textPackage.setText(App.APPLICATION_ID);

        // Dialog
        dialog = new Dialog(activity);

        // Buttons
        InlineUtil.fcu_viewOnClick(binding.licence, () -> activity.startActivity(OpenSourceLicensesActivity.createLaunchIntent(activity)));
        InlineUtil.fcu_viewOnClick(binding.ok, dialog::cancel);
    }

    public View getView() {
        return binding.getRoot();
    }

    public void show() {
        dialog.setContentView(getView());
        dialog.show();
    }
}
