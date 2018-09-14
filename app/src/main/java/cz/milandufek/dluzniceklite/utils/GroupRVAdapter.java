package cz.milandufek.dluzniceklite.utils;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_group, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final String groupNameText = groups.get(position).getName();
        holder.groupName.setText(groupNameText);

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
        holder.parentLayout.setOnClickListener(v -> {
            MyPreferences sp = new MyPreferences(context.getApplicationContext());
            sp.setActiveGroupId(groupId);
            sp.setActiveGroupName(groupNameText);
            sp.setActiveGroupCurrencyId(groupCurrencyId);

            String setAsActive = groupNameText + " " + context.getString(R.string.group_set_as_active);

            refreshActivity();

            Toast.makeText(context, setAsActive, Toast.LENGTH_SHORT).show();
        });

        // popup menu edit/delete
        holder.parentLayout.setOnLongClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, holder.parentLayout);
            popupMenu.inflate(R.menu.menu_item_onclick);
            popupMenu.setGravity(Gravity.END);
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.action_edit_item:
                        onClickEdit(holder, groupId);
                        Toast.makeText(context, "TODO: Edit item " + groupNameText,
                                Toast.LENGTH_SHORT).show();
                        return true;

                    case R.id.action_delete_item:
                        onClickDelete(holder, groupNameText);
                        return true;

                    default:
                        return false;
                }
            });
            popupMenu.show();

            return true;
        });
    }

    private void onClickEdit(ViewHolder h, int id) {
        // TODO onClickEdit
    }

    private void onClickDelete(final ViewHolder h, String groupNameText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(R.string.really_want_to_delete_group)
                .setMessage(groupNameText)
                .setPositiveButton(R.string.ok, (dialog, which) -> {
                    int groupId = groups.get(h.getAdapterPosition()).getId();
                    GroupRepo repo = new GroupRepo();
                    repo.deleteGroup(groupId);
                    groups.remove(h.getAdapterPosition());

                    notifyItemRemoved(h.getAdapterPosition());
                    notifyItemRangeChanged(h.getAdapterPosition(), groups.size());

                    int activeGroupId = new MyPreferences(context).getActiveGroupId();
                    changeActiveGroupToFirstAvailable();
                    if (groupId == activeGroupId)
                        refreshActivity();
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    /**
     * Change id and name of active group to first in the list
     */
    private void changeActiveGroupToFirstAvailable() {
        List<Group> groups = new GroupRepo().getAllGroups();

        MyPreferences sp = new MyPreferences(context);
        sp.setActiveGroupId(groups.get(0).getId());
        sp.setActiveGroupName(groups.get(0).getName());
        sp.setActiveGroupCurrencyId(groups.get(0).getCurrency());
    }

    private void refreshActivity() {
        Intent newActivity = new Intent(context, MainActivity.class);
        newActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(newActivity);
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
        ConstraintLayout parentLayout;

        private ViewHolder(View itemView) {
            super(itemView);

            parentLayout = itemView.findViewById(R.id.item_group);

            groupName = itemView.findViewById(R.id.tv_group_name);
            groupInfo = itemView.findViewById(R.id.tv_group_info);
        }
    }
}