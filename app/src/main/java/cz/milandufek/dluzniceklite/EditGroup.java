package cz.milandufek.dluzniceklite;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cz.milandufek.dluzniceklite.models.Currency;
import cz.milandufek.dluzniceklite.models.Group;
import cz.milandufek.dluzniceklite.models.GroupMember;
import cz.milandufek.dluzniceklite.repository.CurrencyRepo;
import cz.milandufek.dluzniceklite.repository.GroupMemberRepo;
import cz.milandufek.dluzniceklite.repository.GroupRepo;
import cz.milandufek.dluzniceklite.repository.TransactionRepo;

public class EditGroup extends AppCompatActivity {

    private static final String TAG = EditGroup.class.toString();

    private Context context = this;

    private int groupId;

    private EditText groupName, member2add;
    private Spinner selectCurrency;
    private int currencySelectedId;
    private LinearLayout container;

    final ArrayList<Integer> currencyIds = new ArrayList<>();
    final ArrayList<String> currencyNames = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_group);

        groupId = getIntent().getExtras().getInt("GROUP_ID");

        Group group = new GroupRepo().getGroup(groupId);

        groupName = findViewById(R.id.et_group_name2add);
        groupName.setText(group.getName());

        selectCurrency = findViewById(R.id.spinner_group_currency);
        setupSpinnerWithCurrencies();
        selectCurrency.setSelection(currencyIds.indexOf(group.getCurrency()));

        member2add = findViewById(R.id.et_group_member2add);
        ImageButton btnAddMember = findViewById(R.id.btn_group_member2add);
        container = findViewById(R.id.ll_group_container);

        GroupMemberRepo sqlMembers = new GroupMemberRepo();
        List<GroupMember> groupMembers = sqlMembers.getAllGroupMembers(groupId);

        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert layoutInflater != null;

        btnAddMember.setOnClickListener(v -> {
            if (member2add.getText().toString().isEmpty()) {
                showText(R.string.warning_member_empty);
            } else {
                long newMemberId = sqlMembers.insertGroupMember(
                        new GroupMember(0, groupId, member2add.getText().toString(),
                                "","","",false)
                );

                @SuppressLint("InflateParams")
                final View addView = layoutInflater.inflate(R.layout.item_groupmember, null);
                final View.OnClickListener removeMemberListener = remNew -> {
                    sqlMembers.deleteGroupMember((int) newMemberId);
                    ((LinearLayout) addView.getParent()).removeView(addView);
                };

                EditText memberName = addView.findViewById(R.id.et_member_name);
                memberName.setText(member2add.getText().toString());
                member2add.setText("");
                ImageButton btnRemove = addView.findViewById(R.id.btn_member_remove);
                btnRemove.setOnClickListener(removeMemberListener);

                container.addView(addView);
            }
        });

        // add exiting members
        List<Integer> membersWithActivePaymets = new TransactionRepo().getActivePayersAndDebtorIds(groupId);
        for (GroupMember member : groupMembers) {
            @SuppressLint("InflateParams")
            final View addView = layoutInflater.inflate(R.layout.item_groupmember, null);
            EditText memberName = addView.findViewById(R.id.et_member_name);
            memberName.setText(member.getName());

            final View.OnClickListener removeMemberListener = remExist -> {
                sqlMembers.deleteGroupMember(member.getId());
                ((LinearLayout) addView.getParent()).removeView(addView);
            };

            ImageButton btnRemove = addView.findViewById(R.id.btn_member_remove);
            if (membersWithActivePaymets.contains(member.getId())) {
                btnRemove.setVisibility(View.INVISIBLE);
            } else {
                btnRemove.setOnClickListener(removeMemberListener);
            }

            container.addView(addView);
        }

        Button btnSave = findViewById(R.id.btn_group_add);
        btnSave.setOnClickListener(v -> updateGroup(getGroup()));
    }


    private void setupSpinnerWithCurrencies() {
        List<Currency> currencies = new CurrencyRepo().getAllCurrency();
        for (Currency currency : currencies) {
            currencyIds.add(currency.getId());
            currencyNames.add(currency.getName());
        }

        final ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, currencyNames);
        selectCurrency.setAdapter(spinnerAdapter);
        selectCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currencySelectedId = currencyIds.get(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    private Group getGroup() {
        String groupName = EditGroup.this.groupName.getText().toString();
        Group group = null;
        if (groupName.equals("")) {
            showText(R.string.warning_group_name_is_empty);
        } else {
             group = new Group(groupId, groupName, currencySelectedId, null);
        }
        return group;
    }

    private void updateGroup(Group group) {
        GroupRepo sql = new GroupRepo();
        if (group != null && sql.updateGroup(group)) {
            showText(R.string.saved);
            goBackToParentActivity();
        }
    }

    private void goBackToParentActivity() {
        context.startActivity(getParentActivityIntent());
        finish();
    }

    public void showText(int resourceId, int duration) {
        Toast.makeText(context, context.getString(resourceId), duration).show();
    }
    public void showText(int resourceId) {
        showText(resourceId, Toast.LENGTH_SHORT);
    }
    private void showText(String text, int duration) {
        Toast.makeText(this, text, duration).show();
    }
    private void showText(String text) {
        showText(text, Toast.LENGTH_SHORT);
    }
}
