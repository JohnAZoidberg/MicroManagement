package de.struckmeierfliesen.ds.micromanagement;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.Date;
import java.util.List;

import de.struckmeierfliesen.ds.calendarpager.DateUtil;
import de.struckmeierfliesen.ds.micromanagement.sqlite.DatabaseConnection;

public class SelectionActivity extends AppCompatActivity
        implements FoodFragment.OnListFragmentInteractionListener, FoodFragment.FoodLoaderActivity {

    public static final String DISPLAY_DATE = "display_date";

    private DatabaseConnection dbConn;
    private Date date;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);

        Intent args = getIntent();
        date = new Date(args.getLongExtra(DISPLAY_DATE, new Date().getTime()));

        dbConn = new DatabaseConnection(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //toolbar.setTitle(getString(R.string.selection_on, DateUtil.formatDate(date)));
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) supportActionBar.setTitle(getString(R.string.selection_for, DateUtil.formatDateOrWeekday(date)));
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        //mSectionsPagerAdapter.getItem(mViewPager.getCurrentItem())

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*dbConn.addFood(new Food(-1, "Putenbrust", Food.PROTEINS));
                dbConn.addFood(new Food(-1, "Kartoffeln", Food.CARBS));
                dbConn.addFood(new Food(-1, "Avocado", Food.FATS));
                dbConn.addFood(new Food(-1, "Apfel", Food.FRUITS));
                dbConn.addFood(new Food(-1, "Banane", Food.FRUITS));*/
                dbConn.eatFood(new Food(1, "Apfel", Food.FRUITS), date);
                Snackbar.make(view, "Add Food Item", Snackbar.LENGTH_LONG)
                        .setAction("Action", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(v.getContext(), "Apple eaten!", Toast.LENGTH_SHORT).show();
                            }
                        }).show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_selection, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_confirm:
                finish();
                return true;
            case R.id.action_add_new_food:
                showAddFoodDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListFragmentInteraction(Food item) {
        dbConn.eatFood(item, date);
        FoodFragment fragment = getFragmentForFood(item);
        if (fragment != null) {
            fragment.updateList();
        }
    }

    @Override
    public void onListFragmentLongInteraction(final Food item) {
        Dialogue.chooseFromList(this, "Choose action", new String[] {"Undo 'eaten'", "Un-/Hide food", "Cancel"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int choice) {
                switch (choice) {
                    case 0:
                        dbConn.unEatFood(item.getId());
                        break;
                    case 1:
                        dbConn.hideFood(item.getId());
                        // Because we know the Food was hidden - this undoes it
                        item.setType(-item.getType());
                        FoodFragment fragment = getFragmentForFood(item);
                        if (fragment != null) {
                            fragment.updateList();
                        }
                        break;
                }
                FoodFragment fragment = getFragmentForFood(item);
                if (fragment != null) {
                    fragment.updateList();
                }
            }
        });
    }

    @Override
    public List<Food> loadFood(int type) {
        return dbConn.loadFood(type, date);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private final String[] pages = {"Protein", "Carbs", "Fats", "Veggies", "Fruit", "Hidden"}; // TODO: translate

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).

            // Food types are powers of two and 1 << n raises 2 to the nth power
            return FoodFragment.newInstance(1 << position);
        }

        @Override
        public int getCount() {
            return pages.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return pages[position];
        }
    }

    private FoodFragment getFragmentForFood(Food food) {
        int type = food.isHidden() ? Food.HIDDEN : food.getType();
        List<Fragment> allFragments = getSupportFragmentManager().getFragments();
        if (allFragments != null) {
            for (Fragment fragment : allFragments) {
                FoodFragment f1 = (FoodFragment) fragment;
                if (f1.type == type)
                    return f1;
            }
        }
        return null;
    }

    private void showAddFoodDialog() {
        final int type = 1 << mViewPager.getCurrentItem();
        if (type != Food.HIDDEN) {
            Dialogue.askForInput(this, R.string.add_new_food, R.string.add, new Dialogue.OnInputSubmitListener<String>() {
                @Override
                public boolean onSubmit(View v, String input) {
                    if (!input.isEmpty()) {
                        Food newFood = dbConn.addFood(input, type);
                        FoodFragment fragmentForFood = getFragmentForFood(newFood);
                        if (fragmentForFood != null) {
                            fragmentForFood.updateList();
                        }
                        return true;
                    }
                    return false;
                }
            });
        } else {
            Dialogue.alert(this, getString(R.string.not_addable_to_hidden));
        }
    }
}
