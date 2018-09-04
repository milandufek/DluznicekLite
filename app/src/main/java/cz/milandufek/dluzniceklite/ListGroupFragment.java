package cz.milandufek.dluzniceklite;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.List;

import cz.milandufek.dluzniceklite.models.Group;
import cz.milandufek.dluzniceklite.repository.GroupRepo;
import cz.milandufek.dluzniceklite.utils.GroupRVAdapter;

@Deprecated
public class ListGroupFragment extends Fragment {

    private static final String TAG = "ListGroupsFragment";

    private Context context;
    private GroupRVAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        context = getActivity();

        View view = inflater.inflate(R.layout.deprecated_tab_main_groups, container, false);

        Button addGroupBtn = view.findViewById(R.id.btn_group_list_add);
        addGroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickAddGroup();
            }
        });

        List<Group> groups = new GroupRepo().getAllGroups();
        adapter = new GroupRVAdapter(context, groups);

        RecyclerView recyclerView = view.findViewById(R.id.rv_main_group_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        return view;
    }

    /**
     * Start add group activity
     */
    private void onClickAddGroup() {
        Intent intent = new Intent(context, AddGroup.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
