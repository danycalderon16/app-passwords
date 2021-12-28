package com.dacv.app_passwords.fragments;

import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.dacv.app_passwords.R;
import com.dacv.app_passwords.activities.MainActivity;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;




public class NewAccountFragment extends DialogFragment implements View.OnClickListener {

    private Callback callback;


    private FirebaseAuth mAuth;
    private FirebaseUser user;

    public static final int FULLSCREEN_DIALOG = 1000275;
    public static final int FULLSCREEN_DIALOG_ACTION = 1000004;

    public static NewAccountFragment newInstance() {
        return new NewAccountFragment();
    }

    public void setCallback( Callback callback) {
        this.callback = callback;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullscreenDialogTheme);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_new_account, container, false);


        ImageButton close = view.findViewById(R.id.fullscreen_dialog_close);
        TextView action = view.findViewById(R.id.fullscreen_dialog_action);

        close.setOnClickListener(this);
        action.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.fullscreen_dialog_close:
                dismiss();
                break;
            case R.id.fullscreen_dialog_action:
                break;

        }
    }

    public interface Callback {
        void onActionClick(String name);
    }

    private boolean validateEditText(TextInputLayout textInputLayout) {
        String text = textInputLayout.getEditText().getText().toString().trim();
        if (text.isEmpty()) {
            textInputLayout.setError("Campo requerido");
            return false;
        } else {
            textInputLayout.setError(null);
            return true;
        }
    }
}
