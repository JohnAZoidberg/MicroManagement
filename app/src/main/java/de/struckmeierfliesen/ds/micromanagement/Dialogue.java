package de.struckmeierfliesen.ds.micromanagement;


import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Dialogue {
    private Dialogue() {}

    public static void alert(Context context, Object msg) {
        alert(context, msg, false);
    }

    public static void alert(Context context, Object msg, boolean longLength) {
        Toast.makeText(context, msg.toString(),
                longLength ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT
        ).show();
    }

    public static void askForConfirmation(@NonNull Context context, @StringRes int titleId, @NonNull final View.OnClickListener onPositiveListener) {
        askForConfirmation(context, titleId, -1, onPositiveListener);
    }

    public static void askForConfirmation(@NonNull Context context, @StringRes int titleId, @StringRes int messageId, @NonNull final View.OnClickListener onPositiveListener) {
        String messageString = (messageId == -1) ? null : context.getString(messageId);
        askForConfirmation(context, context.getString(titleId), messageString, onPositiveListener);
    }

    public static void askForConfirmation(@NonNull Context context, String title, @Nullable String message, @NonNull final View.OnClickListener onPositiveListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(title)
                .setPositiveButton(android.R.string.yes, null)
                .setNegativeButton(android.R.string.no, null);
        if (message != null) builder.setMessage(message);
        final AlertDialog dialog = builder.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface d) {
                Button b = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onPositiveListener.onClick(v);
                        d.dismiss();
                    }
                });
            }
        });
        dialog.show();
    }

    public static void askForInput(@NonNull Context context, @StringRes int titleId, @StringRes int positiveId, @NonNull final OnInputSubmitListener<String> onPositiveListener) {
        askForInput(context, context.getString(titleId), context.getString(positiveId), onPositiveListener);
    }

    public static void askForInput(@NonNull Context context, String title, String positive, @NonNull final OnInputSubmitListener<String> onPositiveListener) {
        askForInput(context, title, positive, InputType.TYPE_CLASS_TEXT, onPositiveListener);
    }

    public static void askForInput(@NonNull Context context, String title, String positive, int inputType, @NonNull final OnInputSubmitListener<String> onPositiveListener) {
        final EditText input = new EditText(context);
        input.setInputType(inputType);
        final AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setView(input, 20, 20, 20, 20)
                .setPositiveButton(positive, null)
                .setNegativeButton(R.string.cancel, null)
                .create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(final DialogInterface d) {
                Button b = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean dismiss = onPositiveListener.onSubmit(v, input.getText().toString().trim());
                        if (dismiss) d.dismiss();
                    }
                });
            }
        });
        dialog.show();
    }

    public static void chooseFromList(@NonNull Context context, String title, String[] choices, @NonNull DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setItems(choices, listener)
                .create()
                .show();
    }

    interface OnInputSubmitListener<T> {
        boolean onSubmit(View v, T input);
    }
}