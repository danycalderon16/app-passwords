package com.dacv.app_passwords.adapters;

import android.app.Activity;
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
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.dacv.app_passwords.R;
import com.dacv.app_passwords.models.Account;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import org.jetbrains.annotations.NotNull;

public class AdapterAccount extends FirestoreRecyclerAdapter<Account, AdapterAccount.ViewHolder> {

    private Activity activity;

    public AdapterAccount(@NonNull @NotNull FirestoreRecyclerOptions<Account> options,
                          Activity activity) {
        super(options);
        this.activity = activity;
    }

    @Override
    protected void onBindViewHolder(@NonNull @NotNull AdapterAccount.ViewHolder holder, int position, @NonNull @NotNull Account model) {
        holder.imLogo.setImageDrawable(ContextCompat.getDrawable(activity,R.drawable.ic_google));
        holder.tvName.setText(model.getName());
        holder.tvEmail.setText(model.getEmail());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("%%%%%%%%%%%%","setOnClickListener");
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
                Log.i("%%%%%%%%%%%%","OnMenuContext");
                return true;
            }
            return false;
        }
    }

    public interface addClickListener{
        void onItemClick(Account account, int position);
    }
}
