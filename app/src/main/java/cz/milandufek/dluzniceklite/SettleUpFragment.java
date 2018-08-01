package cz.milandufek.dluzniceklite;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import cz.milandufek.dluzniceklite.models.SettleUpTransaction;
import cz.milandufek.dluzniceklite.utils.SettleUpRVAdapter;

public class SettleUpFragment extends Fragment {
    private static final String TAG = "SettleUpFragment";
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_main_settleup, container, false);

        context = getActivity();

        // TODO settle up list info
        RecyclerView recyclerView = view.findViewById(R.id.rv_su_transaction_list);
        SettleUpRVAdapter adapter = new SettleUpRVAdapter(context, recViewDataSet());
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        return view;
    }

    private List<SettleUpTransaction> recViewDataSet() {
        List<SettleUpTransaction> transactions = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            SettleUpTransaction transaction = new SettleUpTransaction();
            transaction.setFrom("From user " + i);
            transaction.setTo("To user " + i);
            transaction.setAmount(i * 100);
            transactions.add(transaction);
        }

        return transactions;
    }
}
