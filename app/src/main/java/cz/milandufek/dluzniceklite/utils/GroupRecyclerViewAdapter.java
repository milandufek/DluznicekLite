package cz.milandufek.dluzniceklite.utils;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cz.milandufek.dluzniceklite.R;
import cz.milandufek.dluzniceklite.models.Group;
import cz.milandufek.dluzniceklite.repository.GroupMemberRepo;
import cz.milandufek.dluzniceklite.repository.GroupRepo;

public class GroupRecyclerViewAdapter
        extends RecyclerView.Adapter<GroupRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "GroupRecyclerViewAdapter";

    private Context context;
    private List<Group> groups;

    public GroupRecyclerViewAdapter(Context context, ArrayList<Group> groups) {
        this.context = context;
        this.groups = groups;
    }

    @NonNull
    @SuppressLint("LongLogTag")
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: ");
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_group, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: ");
        final String groupName = groups.get(position).getName();
        holder.groupName.setText(groupName);

        final int groupId = groups.get(position).getId();
        final int groupCurrencyId = groups.get(position).getCurrency();

        //String groupInfo = "[gID " + groupId + "] [cID " + groupCurrencyId + "] ";
        String groupInfo = "";
        Cursor allGroupMembers = new GroupMemberRepo().selectGroupMembers(groupId);
        groupInfo += "(" + allGroupMembers.getCount() + ") ";
        while (allGroupMembers.moveToNext()) {
            groupInfo += allGroupMembers.getString(2) + ", ";
        }
        groupInfo = groupInfo.substring(0, groupInfo.length() - 2);

        // TODO dependency on the scree resolution
        int maxInfoLength = 55;
        if (groupInfo.length() > maxInfoLength) {
            groupInfo = groupInfo.substring(0, maxInfoLength - 3);
            holder.groupInfo.setText(groupInfo + "...");
        } else {
            holder.groupInfo.setText(groupInfo);
        }

        // set group as active
        holder.parentLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                MySharedPreferences sp = new MySharedPreferences(context);
                sp.setActiveGroupId(groupId);
                sp.setActiveGroupName(groupName);
                sp.setActiveGroupCurrencyId(groupCurrencyId);

                String setAsActive = groupName + " " + context.getString(R.string.group_set_as_active);
                Toast.makeText(context, setAsActive, Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        // TODO open to edit
        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: " + groups.get(position).getName());
                Toast.makeText(context, groups.get(position).getName(), Toast.LENGTH_SHORT).show();
            }
        });

        // delete button
        holder.groupDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: delete ID " + groups.get(position).getId());
                AlertDialog.Builder builder = new AlertDialog.Builder(context)
                        .setTitle(R.string.really_want_to_delete_group)
                        .setMessage(groupName)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                GroupRepo repo = new GroupRepo();
                                repo.deleteGroup(groups.get(position).getId());
                                groups.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, groups.size());
                                changeActiveGroupToFirstAvailable();
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // nothing
                            }
                        });
                builder.show();
            }
        });
    }

    /**
     * Change id and name of active group to first in the list
     */
    private void changeActiveGroupToFirstAvailable() {
        Cursor cursor = new GroupRepo().getAllGroups();
        cursor.moveToFirst();
        int newActiveGroupId = cursor.getInt(0);
        String newActiveGroupName = cursor.getString(1);
        int newActiveGroupCurrency = cursor.getInt(2);

        MySharedPreferences sp = new MySharedPreferences(context);
        sp.setActiveGroupId(newActiveGroupId);
        sp.setActiveGroupName(newActiveGroupName);
        sp.setActiveGroupCurrencyId(newActiveGroupCurrency);
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    /**
     * Recycler View holder
     */
    protected class ViewHolder extends RecyclerView.ViewHolder {
        TextView groupName, groupInfo;
        ImageButton groupDelete;
        RelativeLayout parentLayout;

        private ViewHolder(View itemView) {
            super(itemView);

            parentLayout = itemView.findViewById(R.id.item_group);

            groupName = itemView.findViewById(R.id.tv_group_name);
            groupInfo = itemView.findViewById(R.id.tv_group_info);
            groupDelete = itemView.findViewById(R.id.ibtn_group_remove);
        }
    }
}