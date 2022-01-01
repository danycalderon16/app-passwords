package com.dacv.app_passwords.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.hardware.biometrics.BiometricManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.dacv.app_passwords.R;
import com.dacv.app_passwords.databinding.ActivityAccountBinding;
import com.dacv.app_passwords.models.Account;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;


import java.util.concurrent.Executor;

import static android.hardware.biometrics.BiometricManager.Authenticators.BIOMETRIC_STRONG;
import static android.hardware.biometrics.BiometricManager.Authenticators.DEVICE_CREDENTIAL;
import static com.dacv.app_passwords.utils.Util.*;
import static com.dacv.app_passwords.utils.Encryptor.*;

public class AccountActivity extends AppCompatActivity {

    private ActivityAccountBinding accountBinding;

    private DocumentReference usersRef;
    private FirebaseAuth auth;
    private FirebaseUser user;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private Account account;
    private String uid;

    private String passwordEncrypted;

    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    private Timer timer;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_account);
        accountBinding = ActivityAccountBinding.inflate(getLayoutInflater());
        setContentView(accountBinding.getRoot());

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            uid = bundle.getString(UID);
            account = (Account) bundle.getSerializable(ACCOUNT);
        }
        setToolbar();

        accountBinding.tvEmail.setText(account.getEmail());

        Drawable logo = getLogo(account.getName().toLowerCase(), this);

        accountBinding.imLogo.setImageDrawable(logo);

        accountBinding.btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String key = accountBinding.keyWord.getEditText().toString();
                if (validateEditText(accountBinding.keyWord))
                    checkKey();
            }
        });

        androidx.biometric.BiometricManager biometricManager = androidx.biometric.BiometricManager.from(this);
        switch (biometricManager.canAuthenticate(BIOMETRIC_STRONG | DEVICE_CREDENTIAL)) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                Log.d("MY_APP_TAG", "App can authenticate using biometrics.");
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Log.e("MY_APP_TAG", "No biometric features available on this device.");
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Log.e("MY_APP_TAG", "Biometric features are currently unavailable.");
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                // Prompts the user to create credentials that your app accepts.
                break;
        }

        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(AccountActivity.this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getApplicationContext(),
                        "Authentication error: " + errString, Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                try {
                    String inputKey = accountBinding.keyWord.getEditText().getText().toString();
                    String passwordDecrypted = decrypt(passwordEncrypted, inputKey);
                    accountBinding.tvPass.setText(passwordDecrypted);
                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            accountBinding.tvPass.setText("");
                        }
                    },3000);
                } catch (Exception e) {
                    Log.d("#############", "Error");
                    e.printStackTrace();
                }
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), "Authentication failed",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Confirmación de autenticación")
                .setSubtitle("Inicie sesión con su credencial biométrica")
                .setNegativeButtonText("Cancelar")
                .build();

    }

    private void setToolbar() {

        setSupportActionBar(accountBinding.toolbarAccount);
        accountBinding.toolbarAccount.setTitle(account.getName());
        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //goMain(this);
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void checkKey() {
        DocumentReference docRef = db.collection(USERS)
                .document(user.getUid())
                .collection(ACCOUNTS)
                .document(uid);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    String key = document.getString(KEY);
                    passwordEncrypted = document.getString(PASSWORD);
                    String inputKey = accountBinding.keyWord.getEditText().getText().toString();
                    //Log.d("#############", inputKey);
                    String keyEncrypted = generateKey(inputKey);
                    //Log.d("#############", keyEncrypted);
                    if (key.equals(keyEncrypted))
                        authentication();
                    else{
                        accountBinding.keyWord.setError("Clave incorrecta");
                    }

                } else {
                    Log.d("#############", "get failed with ", task.getException());
                }
            }
        });
    }

    private void authentication() {
        biometricPrompt.authenticate(promptInfo);
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