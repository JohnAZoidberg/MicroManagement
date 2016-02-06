package de.struckmeierfliesen.ds.micromanagement.calendar;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

import de.struckmeierfliesen.ds.micromanagement.Dialogue;
import de.struckmeierfliesen.ds.micromanagement.R;
import de.struckmeierfliesen.ds.micromanagement.Util;

public class CalendarUtil {
    private AppCompatActivity activity;
    private ViewPager dayViewPager;
    private DayAdapter dayAdapter;
    ViewPager.OnPageChangeListener onPageChangeListener;

    // Default is today
    private Date date = new Date();

    // Optional TextView to display the date
    private TextView changeDateButton = null;
    // The text colors for the above button in hex format
    private int[] changeDateButtonColors = null;

    public CalendarUtil(final AppCompatActivity activity, ViewPager dayViewPager, Class<? extends DayFragment> fragmentClass) {
        this.activity = activity;
        this.dayViewPager = dayViewPager;
        dayAdapter = new DayAdapter(activity.getSupportFragmentManager(), fragmentClass);
        dayViewPager.setOffscreenPageLimit(1);
        dayViewPager.setAdapter(dayAdapter);
        showDate(date);
        dayViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                DayFragment registeredFragment = dayAdapter.getRegisteredFragment(position);
                if (registeredFragment == null) {
                    Log.d("Testing", "registeredFragment == null in onPageSelected (MainActivity)");
                    return;
                }

                Date selectedDate = registeredFragment.getDate();
                setDate(selectedDate);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void updateContents() {
        int position = dayViewPager.getCurrentItem();
        for (int i = 0; i < 3; i++) {
            DayFragment registeredFragment = dayAdapter.getRegisteredFragment(position - 1 + i);
            if (registeredFragment != null) {
                registeredFragment.updateContents();
            }
        }
        //dayViewPager.setAdapter(dayAdapter); // TODO Make for efficient
    }

    public void setChangeDateButton(TextView changeDateButton, int[] colors) {
        this.changeDateButton = changeDateButton;
        this.changeDateButtonColors = colors;
        this.changeDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dateFragment = new DatePickerFragment();
                Bundle args = new Bundle();
                args.putIntArray("date", CalendarUtil.extractIntsFromDate(CalendarUtil.this.date));
                dateFragment.setArguments(args);
                dateFragment.show(activity.getSupportFragmentManager(), "datePicker");
            }
        });
        updateButton();
    }

    private void setDate(Date date) {
        this.date = date;
        updateButton();
    }

    private void updateButton() {
        if (changeDateButton != null && changeDateButtonColors != null) {
            changeDateButton.setText(new StringBuilder().append(Util.getDayAbbrev(date)).append(" ").append(DateFormat.format("dd.MM.yy", date)).append("  ").toString());
            if (!Util.isSameDay(new Date(), date)) {
                changeDateButton.setTextColor(changeDateButtonColors[0]);
            } else {
                changeDateButton.setTextColor(changeDateButtonColors[1]);
            }
        }
    }

    private void showDate(Date date) {
        setDate(date);

        int dayDifference = Util.getDayDifference(new Date(), date);
        if (Math.abs(dayDifference) < DayAdapter.DAY_FRAGMENTS / 2) {
            dayViewPager.setCurrentItem(DayAdapter.DAY_FRAGMENTS / 2 - dayDifference);
        } else {
            if (Util.isSameDay(date, this.date)) {
                showDate(new Date()); // TODO is this necessary? What for?
            }
            Dialogue.alert(activity,
                    activity.getString(R.string.day_scroll_limit, DayAdapter.DAY_FRAGMENTS / 2));
        }
    }

    @SuppressLint("ValidFragment")
    public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int[] dateArray = getArguments().getIntArray("date");
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year;
            int month;
            int day;
            if (dateArray != null) {
                day = dateArray[0];
                month = dateArray[1];
                year = dateArray[2];
            } else {
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);
            }

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day);
            Date date = calendar.getTime();
            CalendarUtil.this.showDate(date);
        }
    }

    private static int[] extractIntsFromDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH);
        int year = cal.get(Calendar.YEAR);
        return new int[]{day, month, year};
    }

    public Date getDate() {
        return date;
    }
}
