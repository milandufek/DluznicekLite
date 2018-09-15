package cz.milandufek.dluzniceklite;

import android.content.Context;

import cz.milandufek.dluzniceklite.models.SummaryItem;

public interface Summarable {

    public void initSummary(Context context, SummaryItem summaryItem);
}
