package cz.milandufek.dluzniceklite;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cz.milandufek.dluzniceklite.models.Currency;
import cz.milandufek.dluzniceklite.repository.CurrencyRepo;
import cz.milandufek.dluzniceklite.models.Group;
import cz.milandufek.dluzniceklite.models.GroupMember;
import cz.milandufek.dluzniceklite.repository.GroupMemberRepo;
import cz.milandufek.dluzniceklite.repository.GroupRepo;
import cz.milandufek.dluzniceklite.utils.MySharedPreferences;

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

        groupName = (EditText) findViewById(R.id.et_group_name2add);
        selectCurrency = (Spinner) findViewById(R.id.spinner_group_currency);
        memberNameIn = (EditText) findViewById(R.id.et_group_member2add);
        container = (LinearLayout) findViewById(R.id.ll_group_container);

        Button mBtnAdd = findViewById(R.id.btn_group_add);

        // spinner
        setupSpinnerWithCurrencies();

        // generate container with EditTexts for members
        ImageButton btnAddMember = findViewById(R.id.btn_group_member2add);
        btnAddMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: add member");
                if (memberNameIn.getText().toString().isEmpty()) {
                    Toast.makeText(context,getString(R.string.warrning_member_empty), Toast.LENGTH_SHORT).show();
                } else {
                    LayoutInflater layoutInflater =
                            (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    assert layoutInflater != null;
                    final View addView = layoutInflater.inflate(R.layout.item_groupmember, null);

                    EditText textOut = addView.findViewById(R.id.et_groupmember_add);
                    textOut.setText(memberNameIn.getText().toString());
                    memberNameIn.setText("");
                    ImageButton btnRemove = addView.findViewById(R.id.btn_groupmember_remove);

                    final View.OnClickListener thisListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d(TAG, "onClick: add parent");
                            ((LinearLayout) addView.getParent()).removeView(addView);
                        }
                    };

                    btnRemove.setOnClickListener(thisListener);
                    container.addView(addView);
                }
            }
        });

        // save button
        mBtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveBtnOnClick();
            }
        });
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
            Log.d(TAG, "onClick: item_group name empty " + groupName);
            Toast.makeText(context, "Název skupiny je prázdný...",
                    Toast.LENGTH_SHORT).show();
        } else if (checkIfGroupExists(groupName)) {
            Log.d(TAG, "onClick: item_group " + groupName + " already exists");
            Toast.makeText(context, "Skupina " + groupName + " již existuje...",
                    Toast.LENGTH_SHORT).show();
        } else {
            // save group
            GroupRepo repo = new GroupRepo();
            Group group = new Group(0, groupName, currencySelectedId,"description");
            long newGroupId = repo.insertGroup(group);
            if (newGroupId != -1) {
                Toast.makeText(context,getString(R.string.group_saved), Toast.LENGTH_SHORT).show();
            } else {
                Log.d(TAG, "Cannot insert data...");
                Toast.makeText(context, getString(R.string.error), Toast.LENGTH_SHORT).show();

                // go back to parent activity
                startActivity(getParentActivityIntent());
            }

            // save group members
            GroupMemberRepo groupMemberRepo = new GroupMemberRepo();
            List<String> groupMemberNames = getAllMembers();
            List<GroupMember> groupMembersToInsert = new ArrayList<>();
            for (int i = 0; i < groupMemberNames.size(); i++) {
                GroupMember groupMembers = new GroupMember(0, (int)newGroupId,
                        groupMemberNames.get(i),"","","",0);
                groupMembersToInsert.add(groupMembers);
            }
            groupMemberRepo.insertGroupMembers(groupMembersToInsert);

            // set group as active
            setGroupAsActive(group);

            // go back to parent activity
            startActivity(getParentActivityIntent());
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
        MySharedPreferences sp = new MySharedPreferences(context);
        sp.setActiveGroupId(group.getId());
        sp.setActiveGroupCurrencyId(group.getCurrency());
        sp.setActiveGroupName(group.getName());
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
