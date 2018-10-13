package cz.milandufek.dluzniceklite;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.KeyListener;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import cz.milandufek.dluzniceklite.models.Currency;
import cz.milandufek.dluzniceklite.models.Expense;
import cz.milandufek.dluzniceklite.models.GroupMember;
import cz.milandufek.dluzniceklite.models.Transaction;
import cz.milandufek.dluzniceklite.repository.CurrencyRepo;
import cz.milandufek.dluzniceklite.repository.ExpenseRepo;
import cz.milandufek.dluzniceklite.repository.GroupMemberRepo;
import cz.milandufek.dluzniceklite.repository.TransactionRepo;
import cz.milandufek.dluzniceklite.utils.MyDateTime;
import cz.milandufek.dluzniceklite.utils.MyNumbers;
import cz.milandufek.dluzniceklite.utils.MyPreferences;

public class EditExpense extends AppCompatActivity {

    private static final String TAG = EditExpense.class.toString();

    private Context context = this;

    Expense expense;
    List<GroupMember> members;
    List<Transaction> transactions;
    int groupId;

    private Spinner whoPays, selectCurrency;
    private List<String> memberNames;
    private List<Integer> memberIds, currencyIds;

    private EditText howMuch, reason;

    private LinearLayout whoPaysContainer;

    private String dateDb, timeDb;
    private TextView date, time;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private TimePickerDialog.OnTimeSetListener timeSetListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_expense);

        int expenseId = Objects.requireNonNull(getIntent().getExtras()).getInt("EXPENSE_ID");

        groupId = new MyPreferences(this).getActiveGroupId();

        members = new GroupMemberRepo().getAllGroupMembers(groupId);
        expense = new ExpenseRepo().getExpense(expenseId);
        Log.d(TAG, "onCreate: expense ID = " + expenseId);
        transactions = new TransactionRepo().getTransactions(expenseId);

        setupSpinnerWithMembers();
        setupSpinnerWithCurrencies();

        // set amount
        howMuch = findViewById(R.id.et_payment_edit_howmuch);
        howMuch.setText(String.valueOf(MyNumbers.roundIt(sumTransactionAmount(transactions), 3)));
        unsetEditTextFocusable(howMuch);


        // setup whoPaysContainer with members who paid
        whoPaysContainer = findViewById(R.id.ll_payment_edit_container);
        setupWhoPaysContainer();

        // set reason
        reason = findViewById(R.id.et_payment_edit_reason);
        reason.setText(expense.getReason());

        // date picker
        date = findViewById(R.id.et_payment_edit_date);
        setupDatePicker();

        // time picker
        time = findViewById(R.id.et_payment_edit_time);
        setupTimePicker();

        // update expense
        Button btnSave = findViewById(R.id.btn_payment_edit_save);
        btnSave.setOnClickListener(save -> {
            if (saveUpdatedExpense()) {
                Toast.makeText(this, getString(R.string.saved), Toast.LENGTH_SHORT).show();
                startActivity(getParentActivityIntent());
                finish();
            } else {
                Log.d(TAG, "Cannot insert data...");
                Toast.makeText(this, getString(R.string.error), Toast.LENGTH_SHORT).show();
            }
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
        List<String> currencyNames = getCurrencyNames(currencies);
        currencyIds = getCurrencyIds(currencies);
        final ArrayAdapter<String> spinnerWithCurrencies = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                currencyNames
        );
        selectCurrency = findViewById(R.id.spinner_payment_edit_currency);
        selectCurrency.setAdapter(spinnerWithCurrencies);
        selectCurrency.setSelection(currencyIds.indexOf(expense.getCurrencyId()));
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

    private void setupDatePicker() {
        dateDb = expense.getDate();
        date.setText(MyDateTime.convertDateUsToEu(dateDb));

        // set date from calendar
        date.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(
                    context,
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                    dateSetListener,
                    year,
                    month,
                    day);
            Objects.requireNonNull(dialog.getWindow())
                    .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        });

        dateSetListener = (datePicker, year, month, day) -> {
            month = month + 1;
            String monthZeroPrefix = "";
            String dayZeroPrefix = "";

            if (month < 10)
                monthZeroPrefix = "0";
            if (day < 10)
                dayZeroPrefix = "0";

            dateDb = year + "-" + monthZeroPrefix + month + "-" + dayZeroPrefix + day;
            date.setText(MyDateTime.convertDateUsToEu(dateDb));
        };
    }

    /**
     * Setup time picker
     * Set now as a default value
     */
    private void setupTimePicker() {
        timeDb = expense.getTime();
        time.setText(timeDb);

        time.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            int hours = cal.get(Calendar.HOUR_OF_DAY);
            int minutes = cal.get(Calendar.MINUTE);

            TimePickerDialog dialog = new TimePickerDialog(
                    context,
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                    timeSetListener,
                    hours,
                    minutes,
                    true);
            Objects.requireNonNull(dialog.getWindow())
                    .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        });

        timeSetListener = (view, hourOfDay, minute) -> {
            String hourZeroPrefix = "";
            String minuteZeroPrefix = "";
            if (hourOfDay < 10)
                hourZeroPrefix = "0";
            if (minute < 10)
                minuteZeroPrefix = "0";

            timeDb = hourZeroPrefix + hourOfDay + ":" + minuteZeroPrefix + minute;
            time.setText(timeDb);
        };
    }

    private void setEditTextFocusable(EditText et) {
        et.setKeyListener((KeyListener) et.getTag());
        et.setFocusable(true);
        et.setFocusableInTouchMode(true);
        et.setClickable(true);
    }


    private void unsetEditTextFocusable(EditText et) {
        et.setKeyListener(null);
        et.setFocusable(false);
        et.setFocusableInTouchMode(false);
        et.setClickable(false);
    }

    /**
     * Setup linear layout dynamically based on the number of members and filled data
     */
    private void setupWhoPaysContainer() {
        final int colorIfChecked = getResources().getColor(R.color.colorBlack);
        final int colorIfNotChecked = getResources().getColor(R.color.colorGray);

        for (int i = 0; i < memberNames.size(); i++) {
            LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert layoutInflater != null;
            @SuppressLint("InflateParams") final View memberLine = layoutInflater
                    .inflate(R.layout.item_expense_edit_member, null);

            final TextView currency = memberLine.findViewById(R.id.tv_expense_edit_currency);
            currency.setText(selectCurrency.getSelectedItem().toString());

            final CheckBox memberName = memberLine.findViewById(R.id.chbox_expense_edit_member);
            memberName.setText(memberNames.get(i));
            final EditText memberAmount = memberLine.findViewById(R.id.et_expense_edit_amount);
            memberAmount.setTag(memberAmount.getKeyListener());

            if (isMemberAlreadyPayer(memberIds.get(i))) {
                memberName.setChecked(true);
                memberName.setTextColor(colorIfChecked);
                memberAmount.setText(String.valueOf(transactions.iterator().next().getAmount()));
                memberAmount.setTextColor(colorIfChecked);
                setEditTextFocusable(memberAmount);
            } else {
                memberName.setChecked(false);
                memberName.setTextColor(colorIfNotChecked);
                //memberAmount.setText("0");
                memberAmount.setVisibility(View.GONE);
                memberAmount.setTextColor(colorIfNotChecked);
                currency.setVisibility(View.GONE);
            }

            memberName.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    memberName.setTextColor(colorIfChecked);
                    memberAmount.setTextColor(colorIfChecked);
                    memberAmount.setVisibility(View.VISIBLE);
                    currency.setVisibility(View.VISIBLE);
                    setEditTextFocusable(memberAmount);
                } else {
                    memberName.setTextColor(colorIfNotChecked);
                    memberAmount.setVisibility(View.GONE);
                    memberAmount.setTextColor(colorIfNotChecked);
                    memberAmount.setKeyListener(null);
                    currency.setVisibility(View.GONE);
                }
                // update how much total field
                howMuch.setText(String.valueOf(getHowMuchTotal()));
            });

            memberAmount.setTag(memberAmount.getKeyListener());
            memberAmount.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    double amountTotal = 0;
                    View memberLine;
                    CheckBox memberWillPay;
                    for (int i = 0; i < memberNames.size(); i++) {
                        memberLine = whoPaysContainer.getChildAt(i);
                        memberWillPay = memberLine.findViewById(R.id.chbox_expense_edit_member);
                        if (memberWillPay.isChecked()) {
                            EditText amountPerMember = memberLine.findViewById(R.id.et_expense_edit_amount);
                            String amountTmp = amountPerMember.getText().toString();
                            if (amountTmp.trim().length() > 0) {
                                amountTotal += Float.parseFloat(amountTmp);
                            }
                        }
                    }
                    amountTotal = MyNumbers.roundIt(amountTotal, 2);
                    howMuch.setText(String.valueOf(amountTotal));
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
                @Override
                public void afterTextChanged(Editable s) { }
            });

            whoPaysContainer.addView(memberLine);
        }
    }

    private boolean saveUpdatedExpense() {
        boolean resultExpenseUpdate, resultTransactionsUpdate;
        List<Transaction> newTransactions = new ArrayList<>();
        double amountTotal = 0;

        for (int i = 0; i < whoPaysContainer.getChildCount(); i++) {
            View memberLine = whoPaysContainer.getChildAt(i);
            CheckBox memberWillPay = memberLine.findViewById(R.id.chbox_expense_edit_member);
            if (memberWillPay.isChecked()) {
                EditText memberAmount = memberLine.findViewById(R.id.et_expense_edit_amount);
                amountTotal += getFieldValue(memberAmount);
                if (getFieldValue(memberAmount) > 0) {
                    newTransactions.add(new Transaction(0,
                            memberIds.get(i),
                            expense.getId(),
                            getFieldValue(memberAmount))
                    );
                }
            }
        }

        // payer transaction
        newTransactions.add(new Transaction(0,
                memberIds.get(whoPays.getSelectedItemPosition()),
                expense.getId(),
                amountTotal * -1)
        );

        resultTransactionsUpdate = updateTransactions(newTransactions);

        resultExpenseUpdate = updateExpense(new Expense(
                expense.getId(),
                memberIds.get(whoPays.getSelectedItemPosition()),
                groupId,
                currencyIds.get(selectCurrency.getSelectedItemPosition()),
                getReasonText(),
                dateDb,
                timeDb)

        );

        return resultExpenseUpdate & resultTransactionsUpdate;
    }

    private boolean updateExpense(Expense expense) {
        return new ExpenseRepo().updateExpense(expense);
    }

    private boolean updateTransactions(List<Transaction> transactions) {
        TransactionRepo sql = new TransactionRepo();
        sql.deleteTransactions(expense.getId());
        return sql.insertTransactions(transactions) > 0;
    }

    private String getReasonText() {
        String reasonText;
        reasonText = reason.getText().toString().trim();
        if (reasonText.isEmpty())
            reasonText = getString(R.string.reason_empty);
        return reasonText;
    }

    private double getHowMuchTotal() {
        double amount = 0;
        for (int i = 0; i < whoPaysContainer.getChildCount(); i++) {
            View memberLine = whoPaysContainer.getChildAt(i);
            CheckBox memberWillPay = memberLine.findViewById(R.id.chbox_expense_edit_member);
            if (memberWillPay.isChecked()) {
                EditText amountText = memberLine.findViewById(R.id.et_expense_edit_amount);
                amount += getFieldValue(amountText);
            }
        }
        return amount;
    }

    private double getFieldValue(EditText editText) {
        double value = 0;
        String valueText = editText.getText().toString().trim();
        if (!valueText.isEmpty()) {
            value = Double.parseDouble(valueText);
        }
        return value;
    }
}
