package com.ionexchange.Others;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.ionexchange.Interface.DialogDismissListener;
import com.ionexchange.R;
import com.ionexchange.databinding.DialogFullscreenBinding;


public class DialogFrag extends androidx.fragment.app.DialogFragment {
    private Fragment fragment;
    private DialogFullscreenBinding binding;
    private String title = "";
    private DialogDismissListener listener;

    public DialogFrag(){
//
    }

    public DialogFrag(Fragment fragment, String title, DialogDismissListener listener) {
        this.fragment = fragment;
        this.title = title;
        this.listener = listener;
    }

    public void init(Fragment fragment, String title, DialogDismissListener listener){
        this.fragment = fragment;
        this.title = title;
        this.listener = listener;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        if (listener != null) {
            listener.OnDismiss();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFrag.STYLE_NORMAL, R.style.AppTheme_FullScreenDialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_fullscreen, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.toolbar.setTitle(title);
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFrag.this.dismiss();
            }
        });
        if (fragment != null) {
            FragmentManager manager = getChildFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.full_screen_container, fragment);
            transaction.commit();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }
}
