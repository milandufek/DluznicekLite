package cz.milandufek.dluzniceklite;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.List;

import cz.milandufek.dluzniceklite.models.Currency;
import cz.milandufek.dluzniceklite.models.Expense;
import cz.milandufek.dluzniceklite.models.GroupMember;
import cz.milandufek.dluzniceklite.models.Transaction;
import cz.milandufek.dluzniceklite.repository.CurrencyRepo;
import cz.milandufek.dluzniceklite.repository.ExpenseRepo;
import cz.milandufek.dluzniceklite.repository.GroupMemberRepo;
import cz.milandufek.dluzniceklite.repository.TransactionRepo;
import cz.milandufek.dluzniceklite.utils.MyNumbers;
import cz.milandufek.dluzniceklite.utils.MyPreferences;

public class EditExpense extends AppCompatActivity {

    private static final String TAG = EditExpense.class.toString();

    Expense expense;
    List<GroupMember> members;
    List<Transaction> transactions;
    int groupId;

    private Spinner whoPays, selectCurrency;
    private EditText amount, reason;
    private LinearLayout container;
    private TextView date, time;

    private List<String> memberNames, currencyNames;
    private List<Integer> memberIds, currencyids;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_expense);

        int expenseId = getIntent().getExtras().getInt("EXPENSE_ID");

        groupId = new MyPreferences(this).getActiveGroupId();

        members = new GroupMemberRepo().getAllGroupMembers(groupId);
        expense = new ExpenseRepo().getExpense(expenseId);
        Log.d(TAG, "onCreate: expense ID = " + expenseId);
        transactions = new TransactionRepo().getTransactions(expenseId);

        setupSpinnerWithMembers();
        setupSpinnerWithCurrencies();

        // set amount
        amount = findViewById(R.id.et_payment_edit_howmuch);
        amount.setText(String.valueOf(MyNumbers.roundIt(sumTransactionAmount(transactions),3)));

        // setup container with members who paid
        container = findViewById(R.id.ll_payment_edit_container);
        for (int i = 0; i < members.size(); i++) {
            LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert layoutInflater != null;
            @SuppressLint("InflateParams") final View lineMember = layoutInflater
                    .inflate(R.layout.item_expense_edit_member, null);

            final CheckBox memberName = lineMember.findViewById(R.id.chbox_expense_edit_member);
            memberName.setText(memberNames.get(i));

            final TextView currency = lineMember.findViewById(R.id.tv_expense_edit_currency);
            currency.setText(selectCurrency.getSelectedItem().toString());

            final EditText amount = lineMember.findViewById(R.id.et_expense_edit_amount);
            if (isMemberAlreadyPayer(memberIds.get(i))) {
                amount.setText(String.valueOf(transactions.iterator().next().getAmount()));
                memberName.setChecked(true);
            } else {
                amount.setText("");
                amount.setVisibility(View.GONE);
                currency.setVisibility(View.GONE);
                memberName.setChecked(false);
            }
            container.addView(lineMember);
        }

        // set reason
        reason = findViewById(R.id.et_payment_edit_reason);
        reason.setText(expense.getReason());

        date = findViewById(R.id.et_payment_edit_date);

        time = findViewById(R.id.et_payment_edit_time);

        Button btnSave = findViewById(R.id.btn_payment_edit_save);
        btnSave.setOnClickListener(save -> {
            // TODO
        });
    }

    private boolean isMemberAlreadyPayer(int memberId) {
        boolean isMember = false;
        for (Transaction t : transactions) {
            if (t.getDebtor_id() == memberId) {
                isMember = true;
                break;
            }
        }
        return isMember;
    }

    private void setupSpinnerWithMembers() {
        memberNames = getMemberNames(members);
        memberIds = getMemberIds(members);
        final ArrayAdapter<String> spinnerWithMembers = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                memberNames
        );
        whoPays = findViewById(R.id.spinner_payment_edit_whopays);
        whoPays.setAdapter(spinnerWithMembers);
        whoPays.setSelection(memberIds.indexOf(expense.getPayerId()));
    }

    private void setupSpinnerWithCurrencies() {
        List<Currency> currencies = new CurrencyRepo().getAllCurrency();
        currencyNames = getCurrencyNames(currencies);
        currencyids = getCurrencyIds(currencies);
        final ArrayAdapter<String> spinnerWithCurrencies = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                currencyNames
        );
        selectCurrency = findViewById(R.id.spinner_payment_edit_currency);
        selectCurrency.setAdapter(spinnerWithCurrencies);
        selectCurrency.setSelection(currencyids.indexOf(expense.getCurrencyId()));
    }

    private double sumTransactionAmount(List<Transaction> transactions) {
        double amount = 0;
        for (Transaction transaction : transactions) {
            amount += transaction.getAmount();
        }
        return amount;
    }

    private List<String> getMemberNames(List<GroupMember> members) {
        List<String> memberNames = new ArrayList<>();
        for (GroupMember member : members) {
            memberNames.add(member.getName());
        }
        return memberNames;
    }

    private List<Integer> getMemberIds(List<GroupMember> members) {
        List<Integer> memberIds = new ArrayList<>();
        for (GroupMember member : members) {
            memberIds.add(member.getId());
        }
        return memberIds;
    }

    private List<String> getCurrencyNames(List<Currency> currencies) {
        List<String> currencyNames = new ArrayList<>();
        for (Currency currency : currencies) {
            currencyNames.add(currency.getName());
        }
        return currencyNames;
    }

    private List<Integer> getCurrencyIds(List<Currency> currencies) {
        List<Integer> currencyIds = new ArrayList<>();
        for (Currency currency : currencies) {
            currencyIds.add(currency.getId());
        }
        return currencyIds;
    }
}
