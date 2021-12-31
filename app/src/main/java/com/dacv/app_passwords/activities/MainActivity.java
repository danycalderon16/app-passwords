package com.dacv.app_passwords.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.dacv.app_passwords.R;
import com.dacv.app_passwords.adapters.AdapterAccount;
import com.dacv.app_passwords.databinding.ActivityMainBinding;
import com.dacv.app_passwords.fragments.NewAccountFragment;
import com.dacv.app_passwords.models.Account;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import static com.dacv.app_passwords.utils.Util.*;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding mainBinding;
    private GoogleSignInAccount account;

    private AdapterAccount adapterAccount;
    private RecyclerView recyclerView;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference usersRef;
    private FirebaseAuth auth;
    private FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());


        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        usersRef = db.collection(USERS).document(user.getUid());

        account = GoogleSignIn.getLastSignedInAccount(this);

        setSupportActionBar(mainBinding.toolbar);
        mainBinding.fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialog = NewAccountFragment.newInstance();
                ((NewAccountFragment) dialog).setCallback(new NewAccountFragment.Callback() {
                    @Override
                    public void onActionClick(String name) {
                        Toast.makeText(MainActivity.this, name, Toast.LENGTH_SHORT).show();
                    }
                });
                dialog.show(getSupportFragmentManager(), "tag");
            }
        });

        setRecyclerView();
    }

    private void setRecyclerView() {
        final CollectionReference ref = usersRef.collection(ACCOUNTS);
        Query query = ref.orderBy(NAME, Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Account> options = new FirestoreRecyclerOptions
                .Builder<Account>()
                .setQuery(query, Account.class)
                .build();
        adapterAccount = new AdapterAccount(options, this);


        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapterAccount);

    }

    @Override
    protected void onStart() {
        super.onStart();
        adapterAccount.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapterAccount.stopListening();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_optios, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_logout){
            logOut();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showInfoDialog() {
    }

    private void logOut(){
        GoogleSignInClient mGoogleSignInClient;
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        auth.signOut();
        mGoogleSignInClient.signOut();
        mGoogleSignInClient.revokeAccess();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}