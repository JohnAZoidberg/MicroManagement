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

import de.struckmeierfliesen.ds.calendarpager.DateUtil;
import de.struckmeierfliesen.ds.calendarpager.DayFragment;
import de.struckmeierfliesen.ds.micromanagement.sqlite.DatabaseConnection;

public class FoodDayFragment extends DayFragment {
    private List<Food> items;
    private MyFoodRecyclerViewAdapter entryListAdapter;
    private DatabaseConnection dbConn;

    @NonNull
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_food_list, container, false);
        EmptyRecyclerView recyclerView = (EmptyRecyclerView) rootView.findViewById(R.id.list);
        dbConn = new DatabaseConnection(getContext());
        updateContents();
        // Set the adapter
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        entryListAdapter = new MyFoodRecyclerViewAdapter(items, null);
        recyclerView.setAdapter(entryListAdapter);
        TextView emptyView = (TextView) rootView.findViewById(R.id.empty);
        emptyView.setText(DateUtil.formatDate(getDate()));
        recyclerView.setEmptyView(emptyView);

        return rootView;
    }

    @Override
    public void updateContents() {
        if (items == null) {
            items = new ArrayList<>();
        } else {
            items.clear();
        }
        items.addAll(dbConn.loadFood(getDate()));
        if (entryListAdapter != null) entryListAdapter.notifyDataSetChanged();
    }
}
