package cz.milandufek.dluzniceklite;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
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
import cz.milandufek.dluzniceklite.models.Transaction;
import cz.milandufek.dluzniceklite.repository.CurrencyRepo;
import cz.milandufek.dluzniceklite.repository.ExpenseRepo;
import cz.milandufek.dluzniceklite.repository.GroupMemberRepo;
import cz.milandufek.dluzniceklite.repository.TransactionRepo;
import cz.milandufek.dluzniceklite.utils.MySharedPreferences;

public class AddExpense extends AppCompatActivity {
    private static final String TAG = "AddExpense";

    private Context context = this;
    private int whoPaysIdSelected;
    private String whoPaysNameSelected;
    private int currencySelectedId;
    private String currencySelectedName;
    private int totalRatiosToPay;
    private String dateDb, timeDb;

    private ArrayList<Integer> memberIds = new ArrayList<>();
    private ArrayList<String> memberNames = new ArrayList<>();

    private Spinner whoPays;
    private EditText howMuch;
    private Spinner currency;
    private CheckBox forAll;
    private TextView forAllInfo;
    private RadioGroup typeCalculation;
    private RadioButton rbtnRatio, rbtManually;
    private ImageButton ibtnRatioPlus, ibtnRatioMinus;
    private LinearLayout whoPaysContainer;
    private EditText reason;
    private TextView date;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private Button btnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        whoPays = (Spinner)        findViewById(R.id.spinner_payment_whopays);
        howMuch = (EditText)       findViewById(R.id.et_payment_howmuch);
        currency = (Spinner)       findViewById(R.id.spinner_payment_currency);
        forAll = (CheckBox)        findViewById(R.id.chbox_payment_forall);
        forAllInfo = (TextView)    findViewById(R.id.tv_payment_forallinfo);
        typeCalculation = (RadioGroup) findViewById(R.id.rbtng_payment_ratio);
        rbtnRatio = (RadioButton)   findViewById(R.id.rbtn_payment_ratio);
        ibtnRatioPlus =  (ImageButton) findViewById(R.id.ibtn_expense_ratioplus);
        ibtnRatioMinus = (ImageButton) findViewById(R.id.ibtn_expense_ratiominus);
        rbtManually = (RadioButton) findViewById(R.id.rbtn_payment_manually);
        whoPaysContainer = (LinearLayout) findViewById(R.id.ll_payment_container);
        reason = (EditText)        findViewById(R.id.et_payment_reason);
        date = (TextView)          findViewById(R.id.et_payment_date);
        btnAdd = (Button)          findViewById(R.id.btn_payment_add);

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
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
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

        // save button - insert data into database
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String howMuchText = howMuch.getText().toString().trim();
                if (howMuchText.length() <= 0 || howMuchText.equals("0")) {
                    Toast.makeText(context, getString(R.string.howmuch_is_null),
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, getString(R.string.saved),
                            Toast.LENGTH_SHORT).show();

                    Expense expense = new Expense();
                    expense.setId(0);
                    expense.setPayerId(whoPaysIdSelected);
                    expense.setGroupId(new MySharedPreferences(context).getActiveGroupId());
                    expense.setCurrencyId(currencySelectedId);
                    expense.setReason(reason.getText().toString());
                    expense.setDate(dateDb);
                    expense.setTime(null); // TODO timeDb
                    ExpenseRepo expenseRepo = new ExpenseRepo();
                    long newExpenseId = expenseRepo.insertExpense(expense);

                    List<Transaction> transactions = new ArrayList<>();
                    if (forAll.isChecked()) {
                        float expensePerMember = getHowMuchTotal() / getCountMembers();
                        for (int i = 0; i < getCountMembers(); i++) {
                            Transaction transaction = new Transaction();
                            transaction.setId(0);
                            transaction.setDebtor_id(memberIds.get(i));
                            transaction.setAmount(expensePerMember);
                            transaction.setExpense_id((int) newExpenseId);
                            transactions.add(transaction);
                        }
                        TransactionRepo transactionRepo = new TransactionRepo();
                        transactionRepo.insertTransactions(transactions);

                    } else {
                        // TODO save payment for selected members
                        View memberLine;
                        EditText amountPerMemberTv;
                        double expensePerMember;

                        for (int i = 0; i < getCountMembers(); i++) {
                            memberLine = whoPaysContainer.getChildAt(i);
                            amountPerMemberTv = memberLine.findViewById(R.id.et_expense_amount);
                            expensePerMember = Double.valueOf(amountPerMemberTv.getText().toString());
                            // TODO if selected
                            Transaction transaction = new Transaction();
                            transaction.setId(0);
                            transaction.setDebtor_id(memberIds.get(i));
                            transaction.setAmount(expensePerMember);
                            transaction.setExpense_id((int) newExpenseId);
                            transactions.add(transaction);
                        }
                    }
                }
            }
        });
    }

    /**
     *  Select all members from active group and save them into array
     */
    private void selectAllGroupMembers() {
        int groupId = new MySharedPreferences(context).getActiveGroupId();
        Cursor allGroupMembers = new GroupMemberRepo().selectGroupMembers(groupId);
        while (allGroupMembers.moveToNext()) {
            memberIds.add(allGroupMembers.getInt(0));
            memberNames.add(allGroupMembers.getString(2));
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
                whoPaysNameSelected = memberNames.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // nothing...
            }
        });
    }

    /**
     *  Setup spinner with currencies from database
     */
    private void setupSpinnerWithCurrencies() {
        final ArrayList<Integer> currencyIds = new ArrayList<>();
        final ArrayList<String> currencyNames = new ArrayList<>();

        // call DB and get all currencies
        List<Currency> currencies = new CurrencyRepo().getAllCurrency();
        for (int i = 0; currencies.size() > i; i++) {
            currencyIds.add(currencies.get(i).getId());
            currencyNames.add(currencies.get(i).getName());
        }

        final ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,
                currencyNames);
        currency.setAdapter(spinnerAdapter);

        // select active currency for group
        int selectedItem = new MySharedPreferences(context).getActiveGroupCurrency();
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
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // nothing
            }
        });
    }

    /**
     *  Setup linear layout dynamically based on the numer of members and filled data
     */
    private void setupWhoPaysContainer() {
        for (int i = 0; i < getCountMembers(); i++) {
            LayoutInflater layoutInflater =
                    (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert layoutInflater != null;
            @SuppressLint("InflateParams")
            final View addView = layoutInflater.inflate(R.layout.item_expense_member, null);

            final TextView memberName = addView.findViewById(R.id.tv_payment_member);
            memberName.setText(memberNames.get(i));

            final TextView ratioValue = addView.findViewById(R.id.tv_expense_ratio_value);
            ratioValue.setText("1");

            final TextView ratioSeparator = addView.findViewById(R.id.tv_expense_ratio_separator);
            ratioSeparator.setText("/");

            final TextView ratioTotal = addView.findViewById(R.id.tv_expense_ratio_total);
            ratioTotal.setText(String.valueOf(getCountMembers()));

            final ImageButton ratioPlus  = addView.findViewById(R.id.ibtn_expense_ratioplus);
            final ImageButton ratioMinus = addView.findViewById(R.id.ibtn_expense_ratiominus);
            ratioMinus.setVisibility(View.INVISIBLE);

            ratioPlus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int newValue = Integer.valueOf(ratioValue.getText().toString());
                    if (newValue <= 2) ratioMinus.setVisibility(View.VISIBLE);

                    newValue++;
                    totalRatiosToPay++;
                    ratioValue.setText(String.valueOf(newValue));

                    updateMemberLinesByRation();
                }
            });

            ratioMinus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int newValue = Integer.valueOf(ratioValue.getText().toString());
                    if (newValue <= 2) ratioMinus.setVisibility(View.INVISIBLE);

                    newValue--;
                    totalRatiosToPay--;
                    ratioValue.setText(String.valueOf(newValue));

                    updateMemberLinesByRation();
                }
            });

            final EditText memberAmount = addView.findViewById(R.id.et_expense_amount);
            memberAmount.setText("0");
            memberAmount.setTag(memberAmount.getKeyListener());
            memberAmount.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (rbtManually.isChecked()) {
                        float amountTotal = 0;
                        View memberLine;
                        CheckBox willPay;
                        for (int i = 0; i < getCountMembers(); i++) {
                            memberLine = whoPaysContainer.getChildAt(i);
                            willPay = memberLine.findViewById(R.id.chbox_expense_member);
                            if (willPay.isChecked()) {
                                EditText amountPerMember = memberLine.findViewById(R.id.et_expense_amount);
                                amountTotal += Float.valueOf(amountPerMember.getText().toString());
                            }
                        }
                        amountTotal = roundIt(amountTotal);
                        howMuch.setText(String.valueOf(amountTotal));
                    }
                }
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });


            final TextView currency = addView.findViewById(R.id.tv_expense_currency);
            currency.setText("");

            CheckBox willPay = addView.findViewById(R.id.chbox_expense_member);
            willPay.setChecked(true);
            willPay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        int colorIfChecked = getResources().getColor(R.color.colorBlack);
                        memberName.setTextColor(colorIfChecked);

                        ratioValue.setText("1");
                        ratioValue.setTextColor(colorIfChecked);
                        ratioValue.setVisibility(View.VISIBLE);
                        ratioValue.setKeyListener((KeyListener) ratioValue.getTag());

                        ratioSeparator.setVisibility(View.VISIBLE);
                        ratioTotal.setVisibility(View.VISIBLE);
                        //ratioMinus.setVisibility(View.VISIBLE);
                        ratioPlus.setVisibility(View.VISIBLE);

                        memberAmount.setText("0");
                        memberAmount.setTextColor(colorIfChecked);
                        memberAmount.setVisibility(View.VISIBLE);
                        if (rbtManually.isChecked()) {
                            setEditTextFocusable(memberAmount);
                        } else {
                            setEditTextUnfocusable(memberAmount);
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
                }
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
        float oneRatioValue, ratioLineCount, amountPerMemberText;

        for (int i = 0; i < getCountMembers(); i++) {
            memberLine = whoPaysContainer.getChildAt(i);
            willPay = memberLine.findViewById(R.id.chbox_expense_member);
            ratioTotal = memberLine.findViewById(R.id.tv_expense_ratio_total);
            ratioPerMemberLine = memberLine.findViewById(R.id.tv_expense_ratio_value);
            amountPerMemberLine = memberLine.findViewById(R.id.et_expense_amount);

            oneRatioValue = getHowMuchTotal() / totalRatiosToPay;

            if (willPay.isChecked()) {
                ratioLineCount = Float.valueOf(String.valueOf(ratioPerMemberLine.getText()));
                amountPerMemberText = oneRatioValue * ratioLineCount;
                amountPerMemberText = roundIt(amountPerMemberText);

                amountPerMemberLine.setText(String.valueOf(amountPerMemberText));
                ratioTotal.setText(String.valueOf(totalRatiosToPay));
            }
        }
    }

    /**
     * Generate / update the LL whoPaysContainer with members who will pay
     */
    private void updateWhoPaysContainer() {
        float amountPerMember = getHowMuchPerMember();

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

                    setEditTextUnfocusable(childAmount);
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

    private void addOneRatio() {

    }

    private void removeOneRatio() {

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
    private void setEditTextUnfocusable(EditText et) {
        et.setKeyListener(null);
        et.setFocusable(false);
        et.setFocusableInTouchMode(false);
        et.setClickable(false);
    }

    /**
     * Get count of all members in group
     * @return
     */
    private int getCountMembers() {
        return memberNames.size();
    }

    /**
     * Get count of selected member to pay
     * @return
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
    private float getHowMuchTotal() {
        String textAmount = String.valueOf(howMuch.getText());
        float amountTotal;
        if (textAmount.length() > 0) {
            amountTotal = Float.valueOf(textAmount);
            amountTotal = Math.round(amountTotal * 100f) / 100f;
        } else {
            amountTotal = 0;
        }
        return amountTotal;
    }

    /**
     * Get amount how much to pay per member
     * Round the number to 2 decimals
     */
    private float getHowMuchPerMember() {
        float amountTotal = getHowMuchTotal();
        float amountPerMember;
        if (amountTotal > 0) {
            amountPerMember = amountTotal / getCountMembersSelected();
            amountPerMember = Math.round(amountPerMember * 100f) / 100f;
        } else {
            amountPerMember = 0;
        }
        return amountPerMember;
    }

    /**
     * Updating a view with summary how much will be paid
     * @param s
     */
    private void howMuchOnTextChangeListener(CharSequence s) {
        if (!rbtManually.isChecked()) {
            String forAllInfoText = getString(R.string.payment_for_all);
            forAllInfoText += " ( " + String.valueOf(getCountMembers());
            if (s.toString().trim().length() > 0) {
                float amount = Float.valueOf(s.toString()) / getCountMembers();
                amount = Math.round(amount * 100f) / 100f;
                // rewrite items in LL whoPaysContainer
                updateWhoPaysContainer();
                // update summary in TextView
                forAllInfoText += " x ";
                forAllInfoText += String.valueOf(amount) + " " + currencySelectedName;
            }
            forAllInfoText += " )";
            forAllInfo.setText(forAllInfoText);

            if (!forAll.isChecked()) {
                updateWhoPaysContainer();
            }
        }
    }

    /**
     * Round number
     */
    private float roundIt(float number) {
        return Math.round(number * 100f) / 100f;
    }

    /**
     * Setup date picker
     * Set today as a default value
     */
    private void setupDatePicker() {
        // set today as a date
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month = month + 1;
        String monthZero = "";
        String dayZero = "";
        int day = cal.get(Calendar.DAY_OF_MONTH);
        if (month < 10) { monthZero = "0"; }
        if (day < 10)   { dayZero   = "0"; }
        dateDb = year + monthZero + month + "." + dayZero + day + ".";
        String dateShow = dayZero + day + "." + monthZero + month + "." + year;
        date.setText(dateShow);

        // set date from calendar
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        context,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        dateSetListener,
                        year, month, day);
                Objects.requireNonNull(dialog.getWindow())
                        .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = day + "." + month + "." + year;
                AddExpense.this.date.setText(date);
            }
        };
    }

    /**
     *  Setup radio buttons to change ration calculation
     */
    private void setupRationRButtons() {
        typeCalculation.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                hideSoftKeyboard();
                updateWhoPaysContainer();
            }
        });
    }

    /**
     *  Setup payment for all checkbox.
     *  If is checked then list of payers disappear
     *  If not checked then list of payers is visible with other tools to adjust the payments
     */
    private void setupPaymentForAllCheckBox() {
        forAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                hideSoftKeyboard();
                if (isChecked) {
                    String forAllInfoText = getString(R.string.payment_for_all);
                    forAllInfoText += " ( " + String.valueOf(memberNames.size()) + " )";
                    forAllInfo.setText(forAllInfoText);
                    forAllInfo.setVisibility(View.VISIBLE);
                    rbtnRatio.setVisibility(View.GONE);
                    rbtManually.setVisibility(View.GONE);

                    whoPaysContainer.setVisibility(View.GONE);
                } else {
                    forAllInfo.setVisibility(View.GONE);
                    rbtnRatio.setVisibility(View.VISIBLE);
                    rbtManually.setVisibility(View.VISIBLE);

                    whoPaysContainer.setVisibility(View.VISIBLE);
                    updateWhoPaysContainer();
                }
            }
        });
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
}
