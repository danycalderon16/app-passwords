package com.dacv.app_passwords.fragments;

import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import static com.dacv.app_passwords.utils.Encryptor.*;
import static com.dacv.app_passwords.utils.Util.*;

public class NewAccountFragment extends DialogFragment implements View.OnClickListener {

    private Callback callback;

    private TextInputLayout name;
    private TextInputLayout email;
    private TextInputLayout pass;
    private TextInputLayout key;

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

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
        name = view.findViewById(R.id.account_name);
        email = view.findViewById(R.id.account_email);
        pass = view.findViewById(R.id.account_pass);
        key = view.findViewById(R.id.key_word);

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
                if(saveNewAccount()){
                    dismiss();
                    Snackbar.make(getActivity().findViewById(R.id.fabAdd),"Cuenta agregada",Snackbar.LENGTH_SHORT).show();
                }
                break;

        }
    }

    private boolean saveNewAccount() {
        if(!validateEditText(name) | !validateEditText(email) | !validateEditText(pass) | !validateEditText(key))
            return false;
        String account_name = name.getEditText().getText().toString();
        String account_email = email.getEditText().getText().toString();
        String account_pass = pass.getEditText().getText().toString();
        String key_word = key.getEditText().getText().toString();

        String key_encrypted = generateKey(key_word);
        String pass_encrypted = "";
        try {
            pass_encrypted = encrypt(account_pass.getBytes(), key_word);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Map<String, Object> mapR = new HashMap<>();
        mapR.put(NAME, account_name);
        mapR.put(EMAIL, account_email);
        mapR.put(PASSWORD, pass_encrypted);
        mapR.put(KEY,key_encrypted);
        db.collection(USERS)
                .document(user.getUid())
                .collection(ACCOUNTS)
                .document().set(mapR)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                       }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                    }
                });


        return true;
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
