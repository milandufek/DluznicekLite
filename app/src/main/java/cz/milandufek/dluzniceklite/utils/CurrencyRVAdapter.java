package cz.milandufek.dluzniceklite.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import cz.milandufek.dluzniceklite.R;
import cz.milandufek.dluzniceklite.repository.CurrencyRepo;
import cz.milandufek.dluzniceklite.models.Currency;

public class CurrencyRVAdapter extends RecyclerView.Adapter<CurrencyRVAdapter.ViewHolder> {

    private Context context;
    private List<Currency> currency;

    public CurrencyRVAdapter(Context context, List<Currency> currency) {
        this.context = context;
        this.currency = currency;
    }

    @SuppressLint("LongLogTag")
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_currency, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        holder.parentLayout.setId(currency.get(position).getId());
        holder.currencyName.setText(currency.get(position).getName());
        holder.currencyValue.setText(currency.get(position).getCurrencyInfo());

        // delete button
        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int currencyId = currency.get(holder.getAdapterPosition()).getId();

                PopupMenu popupMenu = new PopupMenu(context, holder.parentLayout);
                popupMenu.inflate(R.menu.menu_item_onclick);
                popupMenu.setGravity(Gravity.END);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_edit_item:
                                onClickEdit(holder, currencyId);
                                return true;

                            case R.id.action_delete_item:
                                onClickDelete(holder, currencyId);
                                return true;

                            default:
                                return false;
                        }
                    }
                });
                popupMenu.show();
            }
        });
    }

    private void onClickEdit(ViewHolder h, int id) {
        // TODO onClickEdit
        Toast.makeText(context,"TODO: Edit item " + id, Toast.LENGTH_SHORT).show();
    }

    private void onClickDelete(final ViewHolder h, final int currencyId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(R.string.really_want_to_delete_currency)
                .setMessage(currency.get(h.getAdapterPosition()).getName())
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CurrencyRepo db = new CurrencyRepo();
                        db.deleteCurrency(currencyId);
                        currency.remove(h.getAdapterPosition());
                        notifyItemRemoved(h.getAdapterPosition());
                        notifyItemRangeChanged(0, currency.size());
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

        builder.show();
    }

    @Override
    public int getItemCount() {
        return currency.size();
    }

    /**
     *  Recycler View Holder
     */
    protected class ViewHolder extends RecyclerView.ViewHolder {
        TextView currencyName;
        TextView currencyValue;
        ConstraintLayout parentLayout;

        private ViewHolder(View itemView) {
            super(itemView);

            parentLayout = itemView.findViewById(R.id.itemCurrency);
            currencyName = itemView.findViewById(R.id.tv_currency_name);
            currencyValue = itemView.findViewById(R.id.tv_currency_value);
        }
    }
}