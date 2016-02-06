package de.struckmeierfliesen.ds.micromanagement;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.struckmeierfliesen.ds.micromanagement.calendar.DayFragment;
import de.struckmeierfliesen.ds.micromanagement.sqlite.DatabaseConnection;

public class FoodDayFragment extends DayFragment {
    private List<Food> items;
    private MyFoodRecyclerViewAdapter entryListAdapter;

    @NonNull
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        EmptyRecyclerView recyclerView = (EmptyRecyclerView) rootView.findViewById(R.id.list);
        updateContents();
        // Set the adapter
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        entryListAdapter = new MyFoodRecyclerViewAdapter(items, null);
        recyclerView.setAdapter(entryListAdapter);
        TextView emptyView = (TextView) rootView.findViewById(R.id.empty);
        emptyView.setText(Util.formatDate(getDate()));
        recyclerView.setEmptyView(emptyView);

        return rootView;
    }

    @Override
    public void updateContents() {
        DatabaseConnection dbConn = new DatabaseConnection(getContext());
        if (items == null) {
            items = new ArrayList<>();
        } else {
            items.clear();
        }
        items.addAll(dbConn.loadFood(getDate()));
        if (entryListAdapter != null) entryListAdapter.notifyDataSetChanged();
    }
}
