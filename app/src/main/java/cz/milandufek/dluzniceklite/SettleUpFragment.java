package cz.milandufek.dluzniceklite;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cz.milandufek.dluzniceklite.models.MemberBalance;
import cz.milandufek.dluzniceklite.models.Currency;
import cz.milandufek.dluzniceklite.models.SettleUpTransaction;
import cz.milandufek.dluzniceklite.models.SummarySettleUpItem;
import cz.milandufek.dluzniceklite.repository.CurrencyRepo;
import cz.milandufek.dluzniceklite.utils.DebtCalculator;
import cz.milandufek.dluzniceklite.utils.MyPreferences;
import cz.milandufek.dluzniceklite.utils.SettleUpRVAdapter;

public class SettleUpFragment extends Fragment {

    private static final String TAG = SettleUpFragment.class.toString();
    private Context context = getContext();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView: ");
        View view = inflater.inflate(R.layout.tab_main_settleup, container, false);

        context = getActivity();

        setupSummaryView(view);

        RecyclerView recyclerView = view.findViewById(R.id.rv_su_transaction_list);
        SettleUpRVAdapter adapter = new SettleUpRVAdapter(context, recViewDataSet());
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        return view;
    }

    private List<SettleUpTransaction> recViewDataSet() {
        List<SettleUpTransaction> transactions = new ArrayList<>();

        MyPreferences sp = new MyPreferences(context);
        int activeCurrencyId = sp.getActiveGroupCurrencyId();
        int activeGroupId = sp.getActiveGroupId();
        Currency baseCurrency = new CurrencyRepo().getBaseCurrency();
        Currency activeCurrency = new CurrencyRepo().getCurrency(activeCurrencyId);

        List<HashMap<String, Object>> debts = new DebtCalculator()
                .calculateDebts(activeGroupId);

        for (HashMap debt : debts) {
            MemberBalance from = (MemberBalance) debt.get("from");
            MemberBalance to = (MemberBalance) debt.get("to");
            double amount = CurrencyOperation.exchangeAmount((double) debt.get("amount"), baseCurrency.getId(), activeCurrencyId);

            SettleUpTransaction transaction = new SettleUpTransaction();
                transaction.setFrom(from.getMemberName());
                transaction.setFromId(from.getMemberId());
                transaction.setTo(to.getMemberName());
                transaction.setToId(to.getMemberId());
                transaction.setAmount(amount);
                transaction.setCurrency(activeCurrency);
            transactions.add(transaction);
        }

        return transactions;
    }

    private void setupSummaryView(View view) {
        TextView summaryTitle = view.findViewById(R.id.tv_settleup_summary);
        SummarySettleUpItem summaryValues = new Summary().initSummarySettleUp(context);
        String titleText = getString(R.string.remains_to_settleup);
        summaryTitle.setText(titleText);

        TextView summarySum = view.findViewById(R.id.tv_settleup_summary_amount);
        StringBuilder sumSpent = new StringBuilder();
        sumSpent.append(summaryValues.getSumToSettleUp());
        sumSpent.append(" ");
        sumSpent.append(summaryValues.getCurrencyName());
        summarySum.setText(sumSpent);
    }
}
