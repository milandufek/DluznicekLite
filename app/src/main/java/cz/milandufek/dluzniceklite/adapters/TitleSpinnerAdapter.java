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

public class TitleSpinnerAdapter extends ArrayAdapter<String> {

    private static final int CLOSED = 0;
    private static final int EXPANDED = 1;

    public TitleSpinnerAdapter(@NonNull Context context, List<String> groupList) {
        super(context, 0, groupList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent, CLOSED);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent, EXPANDED);
    }

    private View initView(int position, @Nullable View convertView, @NonNull ViewGroup parent, int state) {
        if (convertView == null) {
             convertView = LayoutInflater.from(getContext()).inflate(
                     R.layout.title_main_tollbar, parent, false);
        }

        TextView toolbarTitle = convertView.findViewById(R.id.tv_main_toolbar_title);

        String currentItem = getItem(position);

        if (currentItem != null) { toolbarTitle.setText(currentItem); }

        if (state == EXPANDED) {
            toolbarTitle.setTextColor(getContext().getResources().getColor(R.color.colorBlack));
        } else {
            toolbarTitle.setTextColor(getContext().getResources().getColor(R.color.colorWhite));
        }

        return convertView;
    }
}
