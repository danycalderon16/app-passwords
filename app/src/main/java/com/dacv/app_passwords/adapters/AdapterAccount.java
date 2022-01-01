package com.dacv.app_passwords.adapters;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.dacv.app_passwords.R;
import com.dacv.app_passwords.models.Account;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import static com.dacv.app_passwords.utils.Util.ACCOUNTS;
import static com.dacv.app_passwords.utils.Util.USERS;
import static com.dacv.app_passwords.utils.Util.getLogo;

public class AdapterAccount extends FirestoreRecyclerAdapter<Account, AdapterAccount.ViewHolder> {

    private Activity activity;
    private addClickListener listener;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser user = mAuth.getCurrentUser();

    public AdapterAccount(@NonNull @NotNull FirestoreRecyclerOptions<Account> options,
                          Activity activity,
                          addClickListener listener) {
        super(options);
        this.activity = activity;
        this.listener = listener;
    }

    @Override
    protected void onBindViewHolder(@NonNull @NotNull AdapterAccount.ViewHolder holder,
                                    int position, @NonNull @NotNull Account model) {

        Drawable logo = getLogo(model.getName().toLowerCase(),activity);

        holder.imLogo.setImageDrawable(logo);

        holder.tvName.setText(model.getName());
        holder.tvEmail.setText(model.getEmail());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(model, position);
            }
        });
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item, parent, false);
        return new ViewHolder(v);
    }

    public class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnCreateContextMenuListener,
            MenuItem.OnMenuItemClickListener {

        ImageView imLogo;
        TextView tvName;
        TextView tvEmail;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            imLogo = itemView.findViewById(R.id.imgLogo);
            tvName = itemView.findViewById(R.id.tv_name);
            tvEmail = itemView.findViewById(R.id.tv_email);

            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            MenuInflater inflater = activity.getMenuInflater();
            inflater.inflate(R.menu.menu_delete,menu);
            for (int i=0;i<menu.size();i++){
                menu.getItem(i).setOnMenuItemClickListener(this);
            }
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if (item.getItemId() == R.id.delete){
                showConfirmDeleteDiaglog();
                return true;
            }
            return false;
        }

        public void showConfirmDeleteDiaglog(){
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setCancelable(true);
            builder.setMessage("¿Desea borrar cuenta?");
            builder.setPositiveButton("Sí",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteInBD(getBindingAdapterPosition());
                            View v = (activity).findViewById(R.id.fabAdd);
                            Snackbar.make(v,"Cuenta borrada",Snackbar.LENGTH_SHORT).show();
                        }
                    });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    private void deleteInBD(int adapterPosition) {
        /*db.collection(USERS)
                .document(user.getUid())
                .collection(ACCOUNTS)
                .document()*/
        getSnapshots().getSnapshot(adapterPosition)
                .getReference()
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        Toast.makeText(activity,"La elimnacion falló con exito",Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public interface addClickListener{
        void onItemClick(Account account, int position);
    }
}
