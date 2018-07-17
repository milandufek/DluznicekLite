package cz.milandufek.dluzniceklite;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SettleUpFragment extends Fragment {
    private static final String TAG = "SettleUpFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_main_settleup, container, false);

        return view;
    }
}
