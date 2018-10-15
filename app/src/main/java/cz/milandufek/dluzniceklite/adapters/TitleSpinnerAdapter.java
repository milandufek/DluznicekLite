package cz.milandufek.dluzniceklite.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import cz.milandufek.dluzniceklite.R;

public class TitleSpinnerAdapter extends ArrayAdapter {

    public static final int NOT_EXPANDED = 0;
    public static final int EXPANDED = 1;

    private Context mContext;
    private List<String> list;

    public TitleSpinnerAdapter(@NonNull Context context, List<String> list) {
        super(context, 0, list);
        this.mContext = context;
        this.list = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = null;
        if (convertView != null) {
            listItem = LayoutInflater
                    .from(mContext).inflate(R.layout.title_main_tollbar, parent, false);
        }

        return listItem;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return super.getDropDownView(position, convertView, parent);
    }
}
