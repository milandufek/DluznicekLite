package cz.milandufek.dluzniceklite;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import cz.milandufek.dluzniceklite.repository.CurrencyRepo;
import cz.milandufek.dluzniceklite.models.Group;
import cz.milandufek.dluzniceklite.models.GroupMember;
import cz.milandufek.dluzniceklite.repository.GroupMemberRepo;
import cz.milandufek.dluzniceklite.repository.GroupRepo;
import cz.milandufek.dluzniceklite.utils.MyPreferences;

public class AddGroup extends AppCompatActivity {
    private static final String TAG = "AddGroup";

    private Context context = this;

    private EditText groupName;
    private Spinner selectCurrency;
    private int currencySelectedId;
    private EditText memberNameIn;
    private LinearLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);

        groupName = findViewById(R.id.et_group_name2add);
        selectCurrency = findViewById(R.id.spinner_group_currency);
        memberNameIn = findViewById(R.id.et_group_member2add);
        container = findViewById(R.id.ll_group_container);

        Button mBtnAdd = findViewById(R.id.btn_group_add);

        // spinner
        setupSpinnerWithCurrencies();

        // generate container with EditTexts for members
        ImageButton btnAddMember = findViewById(R.id.btn_group_member2add);
        btnAddMember.setOnClickListener(v -> {
            Log.d(TAG, "onClick: add member");
            if (memberNameIn.getText().toString().isEmpty()) {
                showText(R.string.warning_member_empty);
            } else {
                LayoutInflater layoutInflater =
                        (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                assert layoutInflater != null;
                final View addView = layoutInflater.inflate(R.layout.item_groupmember, null);

                EditText textOut = addView.findViewById(R.id.et_groupmember_add);
                textOut.setText(memberNameIn.getText().toString());
                memberNameIn.setText("");
                ImageButton btnRemove = addView.findViewById(R.id.btn_groupmember_remove);

                final View.OnClickListener thisListener = v1 -> {
                    Log.d(TAG, "onClick: add parent");
                    ((LinearLayout) addView.getParent()).removeView(addView);
                };

                btnRemove.setOnClickListener(thisListener);
                container.addView(addView);
            }
        });

        // save button
        mBtnAdd.setOnClickListener(v -> saveBtnOnClick());
    }

    /**
     * Save button onclick method
     * Check if the group is not empty
     * Check if the group already exists
     * Save the group & group members
     */
    private void saveBtnOnClick() {
        Log.d(TAG, "onClick: save button");
        String groupName = AddGroup.this.groupName.getText().toString();
        if (groupName.equals("")) {
            showText(R.string.group_name_is_empty);
        } else if (checkIfGroupExists(groupName)) {
            showText(getString(R.string.group) + " " + groupName + " " + getString(R.string.group_already_exists));
        } else {
            // save group
            GroupRepo repo = new GroupRepo();
            Group group = new Group(0, groupName, currencySelectedId,null);
            long newGroupId = repo.insertGroup(group);
            if (newGroupId != -1) {
                group.setId((int) newGroupId);
                showText(R.string.group_saved);
            } else {
                Log.d(TAG, "Cannot insert data...");
                showText(R.string.error);
                // go back to parent activity
                startActivity(getParentActivityIntent());
            }

            // save group members
            GroupMemberRepo groupMemberRepo = new GroupMemberRepo();
            List<String> groupMemberNames = getAllMembers();
            List<GroupMember> groupMembersToInsert = new ArrayList<>();
            for (String member : groupMemberNames) {
                GroupMember groupMember = new GroupMember(0,
                        (int) newGroupId, member,
                    "", "", "",false
                );
                groupMembersToInsert.add(groupMember);
            }
            groupMemberRepo.insertGroupMembers(groupMembersToInsert);

            // set group as active
            setGroupAsActive(group);

            // go back to parent activity
            Intent intent = new Intent(context, MainActivity.class);
            startActivity(intent);
        }
    }

    /**
     * Setup spinner with currencies from database
     */
    private void setupSpinnerWithCurrencies() {
        final ArrayList<Integer> currencyIds = new ArrayList<>();
        final ArrayList<String> currencyNames = new ArrayList<>();

        List<Currency> currencies = new CurrencyRepo().getAllCurrency();
        for (Currency currency : currencies) {
            currencyIds.add(currency.getId());
            currencyNames.add(currency.getName());
        }

        final ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,
                currencyNames);
        selectCurrency.setAdapter(spinnerAdapter);
        selectCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currencySelectedId = currencyIds.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // will not happen, hopefully...
            }
        });
    }

    /**
     * List all group member names filled in add group screen
     * @return
     */
    private ArrayList<String> getAllMembers() {
        ArrayList<String> groupMembers =  new ArrayList<>();
        for (int i = 0; i < container.getChildCount(); i++) {
            View thisChild = container.getChildAt(i);
            EditText childEditText = thisChild.findViewById(R.id.et_groupmember_add);
            groupMembers.add(childEditText.getText().toString());
        }
        return groupMembers;
    }

    /**
     * Check if item_group exists in database
     * @param groupName
     * @return true if yes
     */
    private boolean checkIfGroupExists(String groupName) {
        ArrayList<String> groupsInDb = new ArrayList<>();
        List<Group> groups = new GroupRepo().getAllGroups();
        for (Group group : groups) {
            groupsInDb.add(group.getName());
        }

        return groupsInDb.contains(groupName);
    }

    /**
     * Set the group as active and save key values to Shared Preferences
     * @param group
     */
    private void setGroupAsActive(Group group) {
        MyPreferences sp = new MyPreferences(context);
        sp.setActiveGroupId(group.getId());
        sp.setActiveGroupCurrencyId(group.getCurrency());
        sp.setActiveGroupName(group.getName());
    }

    private void showText(int resourceId, int duration) {
        Toast.makeText(this, getString(resourceId), duration).show();
    }
    private void showText(int resourceId) {
        showText(resourceId, Toast.LENGTH_SHORT);
    }
    private void showText(String text, int duration) {
        Toast.makeText(this, text, duration).show();
    }
    private void showText(String text) {
        showText(text, Toast.LENGTH_SHORT);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
