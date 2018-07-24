package cz.milandufek.dluzniceklite.utils;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import cz.milandufek.dluzniceklite.MainActivity;
import cz.milandufek.dluzniceklite.R;
import cz.milandufek.dluzniceklite.models.Group;
import cz.milandufek.dluzniceklite.models.GroupMember;
import cz.milandufek.dluzniceklite.repository.GroupMemberRepo;
import cz.milandufek.dluzniceklite.repository.GroupRepo;

public class GroupRVAdapter
        extends RecyclerView.Adapter<GroupRVAdapter.ViewHolder> {

    private static final String TAG = "GroupRVAdapter";

    private Context context;
    private List<Group> groups;

    public GroupRVAdapter(Context context, List<Group> groups) {
        this.context = context;
        this.groups = groups;
    }

    @NonNull
    @SuppressLint("LongLogTag")
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_group, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final String groupName = groups.get(position).getName();
        holder.groupName.setText(groupName);

        final int groupId = groups.get(position).getId();
        final int groupCurrencyId = groups.get(position).getCurrency();

        List<GroupMember> allGroupMembers = new GroupMemberRepo().selectGroupMembers(groupId);
        StringBuilder groupInfo = new StringBuilder();
        groupInfo.append("(");
        groupInfo.append(allGroupMembers.size());
        groupInfo.append(") ");
        String separator = "";
        for (GroupMember member : allGroupMembers) {
            groupInfo.append(separator);
            separator = ", ";
            groupInfo.append(member.getName());
        }

        // TODO dependency on the screen resolution
        int maxInfoLength = 55;
        if (groupInfo.length() > maxInfoLength) {
            groupInfo.setLength(maxInfoLength - 3);
            groupInfo.append("…");
        }
        holder.groupInfo.setText(groupInfo);

        // set group as active
        holder.parentLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                MySharedPreferences sp = new MySharedPreferences(context);
                sp.setActiveGroupId(groupId);
                sp.setActiveGroupName(groupName);
                sp.setActiveGroupCurrencyId(groupCurrencyId);

                String setAsActive = groupName + " " + context.getString(R.string.group_set_as_active);

                Intent newMainActivity = new Intent(context, MainActivity.class);
                newMainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(newMainActivity);

                Toast.makeText(context, setAsActive, Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        // TODO open to edit
        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: " + groups.get(holder.getAdapterPosition()).getName());
                Toast.makeText(context, groups.get(holder.getAdapterPosition()).getName(), Toast.LENGTH_SHORT).show();
            }
        });

        // delete button
        holder.groupDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: delete ID " + groups.get(holder.getAdapterPosition()).getId());
                AlertDialog.Builder builder = new AlertDialog.Builder(context)
                        .setTitle(R.string.really_want_to_delete_group)
                        .setMessage(groupName)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                GroupRepo repo = new GroupRepo();
                                repo.deleteGroup(groups.get(holder.getAdapterPosition()).getId());
                                groups.remove(holder.getAdapterPosition());
                                notifyItemRemoved(holder.getAdapterPosition());
                                notifyItemRangeChanged(holder.getAdapterPosition(), groups.size());
                                changeActiveGroupToFirstAvailable();
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
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
        List<Group> groups = new GroupRepo().getAllGroups();

        MySharedPreferences sp = new MySharedPreferences(context);
        sp.setActiveGroupId(groups.get(0).getId());
        sp.setActiveGroupName(groups.get(0).getName());
        sp.setActiveGroupCurrencyId(groups.get(0).getCurrency());
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
        ConstraintLayout parentLayout;

        private ViewHolder(View itemView) {
            super(itemView);

            parentLayout = itemView.findViewById(R.id.item_group);

            groupName = itemView.findViewById(R.id.tv_group_name);
            groupInfo = itemView.findViewById(R.id.tv_group_info);
            groupDelete = itemView.findViewById(R.id.ibtn_group_remove);
        }
    }
}