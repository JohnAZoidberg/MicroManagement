package de.struckmeierfliesen.ds.micromanagement.calendar;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Date;

import de.struckmeierfliesen.ds.micromanagement.R;
import de.struckmeierfliesen.ds.micromanagement.Util;

abstract public class DayFragment extends Fragment {
    private Date date;

    @NonNull
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_food_list, container, false);
        Bundle args = getArguments();
        int position = args.getInt("position");
        date = Util.addDays(new Date(), position - DayAdapter.DAY_FRAGMENTS / 2);
        return rootView;
    }

    final public Date getDate() {
        return date;
    }

    abstract public void updateContents();
}
