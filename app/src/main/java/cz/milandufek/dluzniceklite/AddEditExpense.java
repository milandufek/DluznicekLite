package cz.milandufek.dluzniceklite;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import cz.milandufek.dluzniceklite.sql.CurrencySql;
import cz.milandufek.dluzniceklite.sql.ExpenseSql;
import cz.milandufek.dluzniceklite.sql.GroupMemberSql;
import cz.milandufek.dluzniceklite.sql.TransactionSql;
import cz.milandufek.dluzniceklite.utils.MyDateTime;
import cz.milandufek.dluzniceklite.utils.MyNumbers;
import cz.milandufek.dluzniceklite.utils.MyPreferences;

public class AddEditExpense extends AppCompatActivity {

    private static final String TAG = "AddEditExpense";

    public static final int ACTION_ADD  = 1;
    public static final int ACTION_EDIT = 2;

    private Context context = this;
    private int action;

    private int whoPaysIdSelected;
    private int currencySelectedId;
    private String currencySelectedName;
    private int totalRatiosToPay;
    private String dateDb, timeDb;

    private List<Integer> memberIds = new ArrayList<>();
    private List<String> memberNames = new ArrayList<>();

    private Spinner whoPays;
    private EditText howMuch;
    private Spinner currency;
    private CheckBox forAll;
    private TextView forAllInfo;
    private RadioGroup typeCalculation;
    private RadioButton rbtnRatio, rbtnManually;
    private LinearLayout whoPaysContainer;
    private EditText reason;
    private TextView date, time;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private TimePickerDialog.OnTimeSetListener timeSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        whoPays = findViewById(R.id.spinner_payment_whopays);
        howMuch = findViewById(R.id.et_payment_howmuch);
        currency = findViewById(R.id.spinner_payment_currency);
        forAll = findViewById(R.id.chbox_payment_forall);
        forAllInfo = findViewById(R.id.tv_payment_forallinfo);
        typeCalculation = findViewById(R.id.rbtng_payment_ratio);
        rbtnRatio = findViewById(R.id.rbtn_payment_ratio);
        rbtnManually = findViewById(R.id.rbtn_payment_manually);
        whoPaysContainer = findViewById(R.id.ll_payment_container);
        reason = findViewById(R.id.et_payment_reason);
        date = findViewById(R.id.et_payment_date);
        time = findViewById(R.id.et_payment_time);
        Button btnAdd = findViewById(R.id.btn_payment_add);

        totalRatiosToPay = getCountMembers();

        // load all members in active group
        selectAllGroupMembers();

        // how much listener
        howMuch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                howMuchOnTextChangeListener(s);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void afterTextChanged(Editable s) { }
        });

        // spinner with payers (who pays)
        setupSpinnerWithGroupMembers();

        // spinner with available currencies
        setupSpinnerWithCurrencies();

        // setup payment for all checkbox
        setupPaymentForAllCheckBox();
        forAll.setChecked(true);

        // change ratio / percentage calculation of payment split
        setupRationRButtons();

        // setup whoPaysContainer with group members and payments
        setupWhoPaysContainer();

        // setup date text view & date picker
        setupDatePicker();

        // setup time text view & time picker
        setupTimePicker();

        // save button - insert data into database
        btnAdd.setOnClickListener(v -> {
            String howMuchText = howMuch.getText().toString().trim();
            if (howMuchText.length() <= 0 || howMuchText.equals("0")) {
                Toast.makeText(context, getString(R.string.howmuch_is_null),
                        Toast.LENGTH_SHORT).show();
            } else {
                if (saveExpense()) {
                    Toast.makeText(context, getString(R.string.saved),
                            Toast.LENGTH_SHORT).show();
                    startActivity(getParentActivityIntent());
                    finish();
                } else {
                    Log.d(TAG, "Cannot insert data...");
                    Toast.makeText(context, getString(R.string.error),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (action == ACTION_EDIT) {
            // TODO...load data
        }
    }

    /**
     *  Select all members from active group and save them into array
     */
    private void selectAllGroupMembers() {
        int groupId = new MyPreferences(context).getActiveGroupId();
        List<GroupMember> allGroupMembers = new GroupMemberSql().getAllGroupMembers(groupId);
        for (GroupMember groupMember : allGroupMembers) {
            memberIds.add(groupMember.getId());
            memberNames.add(groupMember.getName());
        }
    }

    /**
     *  Setup spinner with group members
     */
    private void setupSpinnerWithGroupMembers() {
        final ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, memberNames);
        whoPays.setAdapter(spinnerAdapter);
        whoPays.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                whoPaysIdSelected = memberIds.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    /**
     *  Setup spinner with currencies from database
     */
    private void setupSpinnerWithCurrencies() {
        final List<Integer> currencyIds = new ArrayList<>();
        final List<String> currencyNames = new ArrayList<>();

        List<Currency> currencies = new CurrencySql().getAllCurrencies();
        for (Currency currency : currencies) {
            currencyIds.add(currency.getId());
            currencyNames.add(currency.getName());
        }

        final ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                context,
                android.R.layout.simple_spinner_dropdown_item,
                currencyNames);
        currency.setAdapter(spinnerAdapter);

        // select active currency for group
        int selectedItem = new MyPreferences(context).getActiveGroupCurrencyId();
        currencySelectedId = currencyIds.indexOf(selectedItem);
        currency.setSelection(currencySelectedId);
        currency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currencySelectedId = currencyIds.get(position);
                currencySelectedName = currencyNames.get(position);
                for (int i = 0; i < whoPaysContainer.getChildCount(); i++) {
                    View thisChild = whoPaysContainer.getChildAt(i);
                    TextView currency = thisChild.findViewById(R.id.tv_expense_currency);
                    currency.setText(currencyNames.get(position));
                    refreshSummaryLine();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    /**
     *  Setup linear layout dynamically based on the number of members and filled data
     */
    private void setupWhoPaysContainer() {
        for (int i = 0; i < getCountMembers(); i++) {
            LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert layoutInflater != null;
            @SuppressLint("InflateParams")
            final View addView = layoutInflater.inflate(R.layout.item_expense_member, null);

            final TextView memberName = addView.findViewById(R.id.chbox_expense_member);
            memberName.setText(memberNames.get(i));
            final TextView ratioValue = addView.findViewById(R.id.tv_expense_ratio_value);
            ratioValue.setText("1");
            final TextView ratioSeparator = addView.findViewById(R.id.tv_expense_ratio_separator);
            ratioSeparator.setText("/");
            final TextView ratioTotal = addView.findViewById(R.id.tv_expense_ratio_total);
            ratioTotal.setText(String.valueOf(getCountMembers()));
            final ImageButton ratioMinus = addView.findViewById(R.id.ibtn_expense_ratiominus);
            ratioMinus.setVisibility(View.INVISIBLE);
            final ImageButton ratioPlus  = addView.findViewById(R.id.ibtn_expense_ratioplus);

            ratioPlus.setOnClickListener(v -> {
                int newValue = Integer.parseInt(ratioValue.getText().toString());
                if (newValue <= 2)
                    ratioMinus.setVisibility(View.VISIBLE);

                newValue++;
                totalRatiosToPay++;
                ratioValue.setText(String.valueOf(newValue));
                updateMemberLinesByRation();
            });

            ratioMinus.setOnClickListener(v -> {
                int newValue = Integer.parseInt(ratioValue.getText().toString());
                if (newValue <= 2)
                    ratioMinus.setVisibility(View.INVISIBLE);

                newValue--;
                totalRatiosToPay--;
                ratioValue.setText(String.valueOf(newValue));
                updateMemberLinesByRation();
            });

            final EditText memberAmount = addView.findViewById(R.id.et_expense_amount);
            memberAmount.setText("0");
            memberAmount.setTag(memberAmount.getKeyListener());
            memberAmount.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (rbtnManually.isChecked()) {
                        double amountTotal = 0;
                        View memberLine;
                        CheckBox memberWillPay;
                        for (int i = 0; i < getCountMembers(); i++) {
                            memberLine = whoPaysContainer.getChildAt(i);
                            memberWillPay = memberLine.findViewById(R.id.chbox_expense_member);
                            if (memberWillPay.isChecked()) {
                                EditText amountPerMember = memberLine.findViewById(R.id.et_expense_amount);
                                String amountTmp = amountPerMember.getText().toString();
                                if (amountTmp.trim().length() > 0) {
                                    amountTotal += Float.parseFloat(amountTmp);
                                }
                            }
                        }
                        amountTotal = MyNumbers.roundIt(amountTotal, 2);
                        howMuch.setText(String.valueOf(amountTotal));
                    }
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
                @Override
                public void afterTextChanged(Editable s) { }
            });

            final TextView currency = addView.findViewById(R.id.tv_expense_currency);
            currency.setText("");

            CheckBox willPay = addView.findViewById(R.id.chbox_expense_member);
            willPay.setChecked(true);
            willPay.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    int colorIfChecked = getResources().getColor(R.color.colorBlack);
                    memberName.setTextColor(colorIfChecked);

                    ratioValue.setText("1");
                    ratioValue.setTextColor(colorIfChecked);
                    ratioValue.setVisibility(View.VISIBLE);
                    ratioValue.setKeyListener((KeyListener) ratioValue.getTag());

                    ratioSeparator.setVisibility(View.VISIBLE);
                    ratioTotal.setVisibility(View.VISIBLE);
                    ratioPlus.setVisibility(View.VISIBLE);

                    memberAmount.setText("0");
                    memberAmount.setTextColor(colorIfChecked);
                    memberAmount.setVisibility(View.VISIBLE);
                    if (rbtnManually.isChecked()) {
                        setEditTextFocusable(memberAmount);
                    } else {
                        unsetEditTextFocusable(memberAmount);
                    }

                    currency.setVisibility(View.VISIBLE);
                } else {
                    int colorIfNotChecked = getResources().getColor(R.color.colorGray);
                    memberName.setTextColor(colorIfNotChecked);

                    ratioValue.setText("");
                    ratioValue.setTextColor(colorIfNotChecked);
                    ratioValue.setVisibility(View.GONE);
                    ratioValue.setKeyListener(null);

                    ratioSeparator.setVisibility(View.GONE);
                    ratioTotal.setVisibility(View.GONE);
                    ratioMinus.setVisibility(View.GONE);
                    ratioPlus.setVisibility(View.GONE);

                    memberAmount.setText("0");
                    memberAmount.setVisibility(View.GONE);
                    memberAmount.setTextColor(colorIfNotChecked);
                    memberAmount.setKeyListener(null);

                    currency.setVisibility(View.GONE);
                }

                updateWhoPaysContainer();
            });

            whoPaysContainer.addView(addView);
        }
    }

    /**
     * Update all line in LL container based on the adjusted ration
     */
    private void updateMemberLinesByRation() {
        View memberLine;
        CheckBox willPay;
        TextView ratioTotal, ratioPerMemberLine;
        EditText amountPerMemberLine;
        Double oneRatioValue, ratioLineCount, amountPerMemberText;

        for (int i = 0; i < getCountMembers(); i++) {
            memberLine = whoPaysContainer.getChildAt(i);
            willPay = memberLine.findViewById(R.id.chbox_expense_member);
            ratioTotal = memberLine.findViewById(R.id.tv_expense_ratio_total);
            ratioPerMemberLine = memberLine.findViewById(R.id.tv_expense_ratio_value);
            amountPerMemberLine = memberLine.findViewById(R.id.et_expense_amount);

            oneRatioValue = getHowMuchTotal() / totalRatiosToPay;

            if (willPay.isChecked()) {
                ratioLineCount = Double.valueOf(String.valueOf(ratioPerMemberLine.getText()));
                amountPerMemberText = oneRatioValue * ratioLineCount;
                amountPerMemberText = MyNumbers.roundIt(amountPerMemberText, 2);

                amountPerMemberLine.setText(String.valueOf(amountPerMemberText));
                ratioTotal.setText(String.valueOf(totalRatiosToPay));
            }
        }
    }

    /**
     * Generate / update the LL whoPaysContainer with members who will pay
     */
    private void updateWhoPaysContainer() {
        double amountPerMember = getHowMuchPerMember();

        for (int i = 0; i < getCountMembers(); i++) {
            View memberLine = whoPaysContainer.getChildAt(i);
            CheckBox childPayerCheckBox = memberLine.findViewById(R.id.chbox_expense_member);
            if (childPayerCheckBox.isChecked()) {
                TextView childRatioValue  = memberLine.findViewById(R.id.tv_expense_ratio_value);
                TextView childRatio = memberLine.findViewById(R.id.tv_expense_ratio_total);
                TextView childRatioSeparator = memberLine.findViewById(R.id.tv_expense_ratio_separator);
                ImageButton childIbtnPlus  = memberLine.findViewById(R.id.ibtn_expense_ratioplus);
                ImageButton childIbtnMinus = memberLine.findViewById(R.id.ibtn_expense_ratiominus);
                EditText childAmount = memberLine.findViewById(R.id.et_expense_amount);
                childAmount.setText(String.valueOf(amountPerMember));

                if (rbtnRatio.isChecked()) {
                    childRatioValue.setVisibility(View.VISIBLE);
                    childRatio.setVisibility(View.VISIBLE);
                    childRatioSeparator.setVisibility(View.VISIBLE);
                    childIbtnPlus.setVisibility(View.VISIBLE);
                    childIbtnMinus.setVisibility(View.INVISIBLE);

                    childRatioValue.setText("1");
                    String ratioText = String.valueOf(getCountMembersSelected());
                    childRatio.setText(ratioText);
                    totalRatiosToPay = getCountMembersSelected();

                    unsetEditTextFocusable(childAmount);
                } else {
                    childRatioValue.setVisibility(View.GONE);
                    childRatio.setVisibility(View.GONE);
                    childRatioSeparator.setVisibility(View.GONE);
                    childIbtnPlus.setVisibility(View.GONE);
                    childIbtnMinus.setVisibility(View.GONE);

                    setEditTextFocusable(childAmount);
                }
            }
        }
    }

    /**
     * Set EditText element as focusable
     * @param et
     *      EditText element
     */
    private void setEditTextFocusable(EditText et) {
        et.setKeyListener((KeyListener) et.getTag());
        et.setFocusable(true);
        et.setFocusableInTouchMode(true);
        et.setClickable(true);
    }

    /**
     * Unset EditText element as focusable
     * @param et
     *      EditText element
     */
    private void unsetEditTextFocusable(EditText et) {
        et.setKeyListener(null);
        et.setFocusable(false);
        et.setFocusableInTouchMode(false);
        et.setClickable(false);
    }

    /**
     * Get count of all members in group
     * @return int
     */
    private int getCountMembers() {
        return memberNames.size();
    }

    /**
     * Get count of selected member to pay
     * @return int
     */
    private int getCountMembersSelected() {
        int payersSelected = 0;
        for (int i = 0; i < whoPaysContainer.getChildCount(); i++) {
            View child = whoPaysContainer.getChildAt(i);
            CheckBox checkBox = child.findViewById(R.id.chbox_expense_member);
            if (checkBox.isChecked()) {
                payersSelected++;
            }
        }
        return payersSelected;
    }

    /**
     * Get amount how much to pay in total
     * Round the number to 2 decimals
     */
    private double getHowMuchTotal() {
        String textAmount = String.valueOf(howMuch.getText());
        double amountTotal;
        if (textAmount.length() > 0) {
            amountTotal = Double.valueOf(textAmount);
            amountTotal = MyNumbers.roundIt(amountTotal, 2);
        } else {
            amountTotal = 0;
        }
        return amountTotal;
    }

    /**
     * Get amount how much to pay per member
     * Round the number to 2 decimals
     */
    private double getHowMuchPerMember() {
        double amountTotal = getHowMuchTotal();
        double amountPerMember;
        if (amountTotal > 0) {
            amountPerMember = amountTotal / getCountMembersSelected();
            amountPerMember = MyNumbers.roundIt(amountPerMember, 2);
        } else {
            amountPerMember = 0;
        }
        return amountPerMember;
    }

    /**
     * Updating a view with summary how much will be paid
     * @param s text
     */
    private void howMuchOnTextChangeListener(CharSequence s) {
        if (!rbtnManually.isChecked()) {
            StringBuilder forAllInfoText = new StringBuilder(getString(R.string.payment_for_all));
            forAllInfoText.append(" ( ");
            forAllInfoText.append(getCountMembers());
            if (s.toString().trim().length() > 0) {
                double amount = Double.valueOf(s.toString()) / getCountMembers();
                amount = MyNumbers.roundIt(amount, 2);
                // update summary in TextView
                forAllInfoText.append(" x ");
                forAllInfoText.append(amount);
                forAllInfoText.append(" ");
                forAllInfoText.append(currencySelectedName);
                // rewrite items in LL whoPaysContainer
                updateWhoPaysContainer();
            }
            forAllInfoText.append(" )");
            forAllInfo.setText(forAllInfoText);

            if (!forAll.isChecked()) {
                updateWhoPaysContainer();
            }
        }
    }

    /**
     * Setup date picker
     * Set today as a default value
     */
    private void setupDatePicker() {
        dateDb = MyDateTime.getDateToday();
        date.setText(getString(R.string.today));

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
        timeDb = MyDateTime.getTimeNow();
        time.setText(getString(R.string.now));

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

    /**
     *  Setup radio buttons to change ration calculation
     */
    private void setupRationRButtons() {
        typeCalculation.setOnCheckedChangeListener((group, checkedId) -> {
            hideSoftKeyboard();
            updateWhoPaysContainer();
        });
    }

    /**
     *  Setup payment for all checkbox.
     *  If is checked then list of payers disappear
     *  If not checked then list of payers is visible with other tools to adjust the payments
     */
    private void setupPaymentForAllCheckBox() {
        forAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            hideSoftKeyboard();
            if (isChecked) {
                refreshSummaryLine();
                forAllInfo.setVisibility(View.VISIBLE);
                rbtnRatio.setVisibility(View.GONE);
                rbtnManually.setVisibility(View.GONE);

                whoPaysContainer.setVisibility(View.GONE);
            } else {
                forAllInfo.setVisibility(View.GONE);
                rbtnRatio.setVisibility(View.VISIBLE);
                rbtnManually.setVisibility(View.VISIBLE);

                whoPaysContainer.setVisibility(View.VISIBLE);
                updateWhoPaysContainer();
            }
        });
    }

    /**
     * Refresh summary line
     */
    private void refreshSummaryLine() {
        StringBuilder forAllInfoText = new StringBuilder(getString(R.string.payment_for_all));
        forAllInfoText.append(" ( ");
        forAllInfoText.append(memberNames.size());
        String howMuchText = howMuch.getText().toString().trim();
        if (! howMuchText.equals("")) {
            if (Double.parseDouble(howMuchText) > 0) {
                forAllInfoText.append(" x ");
                forAllInfoText.append(MyNumbers
                        .roundIt(Double.parseDouble(howMuchText) / memberNames.size(), 2));
                forAllInfoText.append(" ");
                forAllInfoText.append(currencySelectedName);
            }
        }
        forAllInfoText.append(" )");
        forAllInfo.setText(forAllInfoText);
    }

    /**
     * Hide software keyboard if is opened
     */
    private void hideSoftKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * Insert expense and all related transaction into database
     * @return true if success
     */
    private boolean saveExpense() {
        String reasonText = reason.getText().toString();
        if (reasonText.isEmpty())
            reasonText = getString(R.string.reason_empty);

        Expense expense = new Expense(
                0,
                whoPaysIdSelected,
                new MyPreferences(context).getActiveGroupId(),
                currencySelectedId,
                reasonText,
                dateDb,
                timeDb);

        ExpenseSql expenseSql = new ExpenseSql();
        long newExpenseId = expenseSql.insertExpense(expense);
        List<Transaction> transactions = new ArrayList<>();
        double expensePerMember;
        CheckBox willPay;

        for (int i = 0; i < getCountMembers(); i++) {
            if (forAll.isChecked()) {
                expensePerMember = getHowMuchTotal() / getCountMembers();
                willPay = forAll;
            } else {
                View memberLine;
                EditText amountPerMemberTv;
                memberLine = whoPaysContainer.getChildAt(i);
                willPay = memberLine.findViewById(R.id.chbox_expense_member);
                amountPerMemberTv = memberLine.findViewById(R.id.et_expense_amount);
                if (amountPerMemberTv.getText().toString().trim().length() > 0) {
                    expensePerMember = Double.parseDouble(amountPerMemberTv.getText().toString().trim());
                } else {
                    expensePerMember = 0;
                }
            }

            if (willPay.isChecked()) {
                Transaction transaction = new Transaction(0,
                        memberIds.get(i),
                        (int) newExpenseId,
                        expensePerMember
                );
                transactions.add(transaction);
            }
        }

        // add also positive expense for payer
        Transaction transaction = new Transaction(
                0,
                whoPaysIdSelected,
                ((int) newExpenseId),
        getHowMuchTotal() * -1
        );

        transactions.add(transaction);

        TransactionSql transactionSql = new TransactionSql();
        long resultInsertTransaction = transactionSql.insertTransactions(transactions);

        return resultInsertTransaction > -1;
    }
}
