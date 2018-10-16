package cz.milandufek.dluzniceklite;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.widget.Toast;

import java.util.List;

import cz.milandufek.dluzniceklite.models.Group;
import cz.milandufek.dluzniceklite.sql.GroupSql;
import cz.milandufek.dluzniceklite.adapters.GroupRVAdapter;

public class ManageGroups extends AppCompatActivity {

    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_groups);

        List<Group> groups = new GroupSql().getAllGroups();
        GroupRVAdapter adapter = new GroupRVAdapter(context, groups);

        RecyclerView recyclerView = findViewById(R.id.rv_group_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        // TODO ItemTouch on swipe remove + undo Snackbar
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                Toast.makeText(context, "Deleting group", Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(recyclerView);

        // Floating Button
        FloatingActionButton fab = findViewById(R.id.fab_group_add);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(ManageGroups.this, AddGroup.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
        });
    }
}
