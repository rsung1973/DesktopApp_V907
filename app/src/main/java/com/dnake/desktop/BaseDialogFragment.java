package com.dnake.desktop;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.dnake.desktop.adapter.AppsGridAdapter;

public class BaseDialogFragment extends DialogFragment {
    protected View inflaterView;
    private Callback mCallback;

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    public interface Callback {
        void doCancel();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mCallback != null)
            mCallback.doCancel();
    }
}
