package cz.milandufek.dluzniceklite;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
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
import cz.milandufek.dluzniceklite.repository.CurrencyRepo;
import cz.milandufek.dluzniceklite.repository.ExpenseRepo;
import cz.milandufek.dluzniceklite.repository.GroupMemberRepo;
import cz.milandufek.dluzniceklite.utils.MySharedPreferences;

public class AddExpense extends AppCompatActivity {
    private static final String TAG = "AddExpense";

    private Context context = this;
    private int whoPaysIdSelected;
    private String whoPaysNameSelected;
    private int currencySelectedId;
    private String currencySelectedName;

    private ArrayList<Integer> memberIds = new ArrayList<>();
    private ArrayList<String> memberNames = new ArrayList<>();
    private ArrayList<Boolean> memberIsChecked = new ArrayList<>();

    private Spinner whoPays;
    private EditText howMuch;
    private Spinner currency;
    private CheckBox forAll;
    private TextView forAllInfo;
    private RadioGroup typeCalculation;
    private RadioButton rbtnRatio, rbtnPercent;
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
        rbtnRatio = (RadioButton)  findViewById(R.id.rbtn_payment_ratio);
        rbtnPercent = (RadioButton)findViewById(R.id.rbtn_payment_percentage);
        whoPaysContainer = (LinearLayout) findViewById(R.id.ll_payment_container);
        reason = (EditText)        findViewById(R.id.et_payment_reason);
        date = (TextView)          findViewById(R.id.et_payment_date);
        btnAdd = (Button)          findViewById(R.id.btn_payment_add);

        // load all members in active group
        selectAllGroupMembers();

        // how much listener
        howMuch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String forAllInfoText = getString(R.string.payment_for_all);
                forAllInfoText += " ( " + String.valueOf(memberNames.size());
                if (s.toString().trim().length() > 0) {
                    float amount = Float.valueOf(s.toString()) / memberNames.size();
                    amount = Math.round(amount * 100f) / 100f;
                    // rewrite items in LL whoPaysContainer
                    updateWhoPaysContainer();
                    // update summary in TextView
                    forAllInfoText += " x ";
                    forAllInfoText += String.valueOf(amount) + " " + currencySelectedName;
                }
                forAllInfoText += " )";
                forAllInfo.setText(forAllInfoText);

                if (! forAll.isChecked()) {
                    updateWhoPaysContainer();
                }
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

                    if (forAll.isChecked()) {
                        // TODO save payment for all
                        ExpenseRepo expenseRepo = new ExpenseRepo();
                    } else {
                        // TODO save payment for selected members
                        ExpenseRepo expenseRepo = new ExpenseRepo();
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
            memberIsChecked.add(true);
        }
    }

    /**
     *  Setup spinner with group members
     */
    private void setupSpinnerWithGroupMembers() {
        final ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,
                memberNames);
        whoPays.setAdapter(spinnerAdapter);
        whoPays.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                whoPaysIdSelected = memberIds.get(position);
                whoPaysNameSelected = memberNames.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // will not happen, hopefully...
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
        String percentagePerPerson = String.valueOf(getPercentagePerMember());
        for (int i = 0; i < memberNames.size(); i++) {
            LayoutInflater layoutInflater =
                    (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert layoutInflater != null;
            final View addView = layoutInflater.inflate(R.layout.item_expense_member, null);

            final TextView memberName = addView.findViewById(R.id.tv_payment_member);
            memberName.setText(memberNames.get(i));

            final EditText memberRation = addView.findViewById(R.id.et_expense_ratio);
            memberRation.setText(percentagePerPerson);
            memberRation.setTag(memberRation.getKeyListener());
            memberRation.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    // TODO change text listener
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            final TextView ratio = addView.findViewById(R.id.tv_expense_ratio);
            ratio.setText("%");

            final EditText memberAmount = addView.findViewById(R.id.et_expense_amount);
            memberAmount.setText("");
            memberAmount.setTag(memberAmount.getKeyListener());
            memberAmount.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    // TODO change text listener
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

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
                    if (! isChecked) {
                        int colorIfNotChecked = getResources().getColor(R.color.colorGray);
                        memberName.setTextColor(colorIfNotChecked);

                        memberRation.setText("");
                        memberRation.setTextColor(colorIfNotChecked);
                        memberRation.setVisibility(View.GONE);
                        memberRation.setKeyListener(null);

                        ratio.setVisibility(View.GONE);

                        memberAmount.setText("0");
                        memberAmount.setVisibility(View.GONE);
                        memberAmount.setTextColor(colorIfNotChecked);
                        memberAmount.setKeyListener(null);

                        currency.setVisibility(View.GONE);
                    } else {
                        int colorIfChecked = getResources().getColor(R.color.colorBlack);
                        memberName.setTextColor(colorIfChecked);

                        String textRatio;
                        if (rbtnPercent.isChecked()) {
                            textRatio = String.valueOf(getPercentagePerMember(getCountMembersSelected()));
                        } else {
                            textRatio = "1";
                        }
                        memberRation.setText(textRatio);
                        memberRation.setTextColor(colorIfChecked);
                        memberRation.setVisibility(View.VISIBLE);
                        memberRation.setKeyListener((KeyListener) memberRation.getTag());

                        ratio.setVisibility(View.VISIBLE);

                        memberAmount.setText("0"); // TODO
                        memberAmount.setTextColor(colorIfChecked);
                        memberAmount.setVisibility(View.VISIBLE);
                        memberAmount.setKeyListener((KeyListener) memberAmount.getTag());

                        currency.setVisibility(View.VISIBLE);
                    }

                    updateWhoPaysContainer();
                }
            });

            whoPaysContainer.addView(addView);
        }
    }

    /**
     * Generate / update the LL whoPaysContainer with members who will pay
     */
    private void updateWhoPaysContainer() {
        String textAmount = String.valueOf(howMuch.getText());
        float amountPerMember;
        if (textAmount.length() > 0) {
            amountPerMember = Float.valueOf(textAmount);
            amountPerMember = amountPerMember / getCountMembersSelected();
            amountPerMember = Math.round(amountPerMember * 100f) / 100f;
        } else {
            amountPerMember = 0;
        }

        for (int i = 0; i < memberNames.size(); i++) {
            View memberLine = whoPaysContainer.getChildAt(i);

            CheckBox childPayerCheckBox = memberLine.findViewById(R.id.chbox_expense_member);
            if (childPayerCheckBox.isChecked()) {
                EditText childRatioValue  = memberLine.findViewById(R.id.et_expense_ratio);
                TextView childRatio = memberLine.findViewById(R.id.tv_expense_ratio);
                if (rbtnPercent.isChecked()) {
                    childRatioValue.setText(String.valueOf(getPercentagePerMember(getCountMembersSelected())));
                    childRatio.setText("%");
                } else {
                    childRatioValue.setText("1");
                    String ratioText = "/";
                    ratioText += String.valueOf(getCountMembersSelected());
                    childRatio.setText(ratioText);
                }

                EditText childAmount = memberLine.findViewById(R.id.et_expense_amount);
                childAmount.setText(String.valueOf(amountPerMember));
            }
        }
    }

    /**
     *  Get all members in liner layout whoPaysContainer
     */
    private List<String> getAllMembers() {
        List<String> members =  new ArrayList<>();
        int childCount = whoPaysContainer.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View thisChild = whoPaysContainer.getChildAt(i);
            EditText childEditText = thisChild.findViewById(R.id.tv_payment_member);
            members.add(childEditText.getText().toString());
        }
        return members;
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
     * Calculate percentage for each member
     */
    private float getPercentagePerMember() {
        return Math.round((float) 100 / (float) getCountMembers() * 100f) / 100f;
    }
    private float getPercentagePerMember(int membersSelected) {
        return Math.round((float) 100 / (float) membersSelected * 100f) / 100f;
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
        String dateDb = year + monthZero + month + "." + dayZero + day + ".";
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
                if (isChecked) {
                    String forAllInfoText = getString(R.string.payment_for_all);
                    forAllInfoText += " ( " + String.valueOf(memberNames.size()) + " )";
                    forAllInfo.setText(forAllInfoText);
                    forAllInfo.setVisibility(View.VISIBLE);
                    rbtnRatio.setVisibility(View.GONE);
                    rbtnPercent.setVisibility(View.GONE);

                    whoPaysContainer.setVisibility(View.GONE);
                } else {
                    forAllInfo.setVisibility(View.GONE);
                    rbtnRatio.setVisibility(View.VISIBLE);
                    rbtnPercent.setVisibility(View.VISIBLE);

                    whoPaysContainer.setVisibility(View.VISIBLE);
                    updateWhoPaysContainer();
                }
            }
        });
    }
}
