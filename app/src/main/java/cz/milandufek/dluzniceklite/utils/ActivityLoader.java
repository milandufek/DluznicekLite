package cz.milandufek.dluzniceklite.utils;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;

public class ActivityLoader extends AsyncTask {

    private AppCompatActivity activity;

    public ActivityLoader(AppCompatActivity activity) {
        Intent newActivity = new Intent(activity, activity.getClass());
        newActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(newActivity);
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        return null;
    }
}
