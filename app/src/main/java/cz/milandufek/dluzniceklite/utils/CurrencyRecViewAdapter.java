package cz.milandufek.dluzniceklite.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import cz.milandufek.dluzniceklite.R;
import cz.milandufek.dluzniceklite.repository.CurrencyRepo;
import cz.milandufek.dluzniceklite.models.Currency;

public class CurrencyRecViewAdapter
        extends RecyclerView.Adapter<CurrencyRecViewAdapter.ViewHolder> {
    private static final String TAG = "CurrencyRecViewAdapter";

    private Context context;
    private List<Currency> currency;

    public CurrencyRecViewAdapter(Context context, List<Currency> currency) {
        this.context = context;
        this.currency = currency;
    }

    @SuppressLint("LongLogTag")
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: Creating...");
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_currency, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: called...");

        holder.parentLayout.setId(currency.get(position).getId());
        holder.currencyName.setText(currency.get(position).getName());
        holder.currencyValue.setText(currency.get(position).getCurrencyInfo());
        holder.currencyDelete.setId(currency.get(position).getId());

        // item click Toast notification
        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: item ID " + currency.get(holder.getAdapterPosition()).getName());

                Toast.makeText(context,currency.get(holder.getAdapterPosition()).getName()
                                + " ID(" + currency.get(holder.getAdapterPosition()).getId() + ")",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // hide delete button if there is only last item_currency
        if (getItemCount() <= 1) {
            holder.currencyDelete.setVisibility(View.GONE);
        }

        // delete button
        holder.currencyDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int currencyId = currency.get(holder.getAdapterPosition()).getId();
                Log.d(TAG, "onClick: delete ID " + currencyId);
                AlertDialog.Builder builder = new AlertDialog.Builder(context)
                        .setTitle(R.string.really_want_to_delete_currency)
                        .setMessage(currency.get(holder.getAdapterPosition()).getName())
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                CurrencyRepo db = new CurrencyRepo();
                                db.deleteCurrency(currencyId);
                                currency.remove(holder.getAdapterPosition());
                                notifyItemRemoved(holder.getAdapterPosition());
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
        });
    }

    @Override
    public int getItemCount() {
        return currency.size();
    }

    /**
     *  Recycler View Holder
     */
    protected class ViewHolder extends RecyclerView.ViewHolder {
    //protected class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView currencyName;
        TextView currencyValue;
        ImageButton currencyDelete;
        RelativeLayout parentLayout;

        private ViewHolder(View itemView) {
            super(itemView);

            parentLayout = itemView.findViewById(R.id.itemCurrency);
            currencyName = itemView.findViewById(R.id.tv_currency_name);
            currencyValue = itemView.findViewById(R.id.tv_currency_value);
            currencyDelete = itemView.findViewById(R.id.ibtn_currency_remove);
        }
    }
}