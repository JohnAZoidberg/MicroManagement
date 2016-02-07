package de.struckmeierfliesen.ds.micromanagement;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import de.struckmeierfliesen.ds.calendarpager.Calendar;
import de.struckmeierfliesen.ds.micromanagement.sqlite.DatabaseConnection;

public class MainActivity extends AppCompatActivity {
    ViewPager.OnPageChangeListener onPageChangeListener;
    Calendar calendar;
    private DatabaseConnection dbConn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // establish a connection to the database
        dbConn = new DatabaseConnection(this);

        // set up Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) supportActionBar.setDisplayShowTitleEnabled(false);

        // set up ViewPager for the days
        calendar = new Calendar(this, (ViewPager) findViewById(R.id.dayPager), FoodDayFragment.class);
        TextView changeDateButton = (TextView) findViewById(R.id.changeDate);
        calendar.setChangeDateButton(changeDateButton, 0xFFFFFFFF, ContextCompat.getColor(this, R.color.colorAccent));

        // set up FAB
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long dateLong = calendar.getDate().getTime();
                Intent intent = new Intent(MainActivity.this, SelectionActivity.class);
                intent.putExtra(SelectionActivity.DISPLAY_DATE, dateLong);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        calendar.updateContents();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_clear_history) {
            Dialogue.askForConfirmation(this, R.string.clear_history, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dbConn.clearHistory();
                    calendar.updateContents();
                }
            });
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
