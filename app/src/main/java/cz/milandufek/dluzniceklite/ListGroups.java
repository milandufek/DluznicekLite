package cz.milandufek.dluzniceklite;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.util.List;

import cz.milandufek.dluzniceklite.models.Group;
import cz.milandufek.dluzniceklite.repository.GroupRepo;
import cz.milandufek.dluzniceklite.utils.GroupRecViewAdapter;

public class ListGroups extends AppCompatActivity {
    private static final String TAG = "ListGroups";
    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_groups);
        Log.d(TAG, "onCreate: started.");

        setupRecyclerView();

        // Floating Button
        FloatingActionButton fab = findViewById(R.id.fab_group_add);
        fab.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent intent = new Intent(ListGroups.this, AddGroup.class);
                 intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                 startActivity(intent);
             }
        });
    }

    /**
     * Setup RecyclerView
     */
    public void setupRecyclerView() {
        Log.d(TAG, "setupRecyclerView: settings up adapter...");

        List<Group> groups = new GroupRepo().getAllGroups();

        GroupRecViewAdapter adapter = new GroupRecViewAdapter(context, groups);

        RecyclerView recyclerView = findViewById(R.id.rv_group_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
    }
}
