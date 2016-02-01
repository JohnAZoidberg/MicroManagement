package de.struckmeierfliesen.ds.micromanagement;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import de.struckmeierfliesen.ds.micromanagement.sqlite.DatabaseConnection;

public class MainActivity extends AppCompatActivity
        implements FoodFragment.OnListFragmentInteractionListener, FoodFragment.FoodLoaderActivity {

    DatabaseConnection dbConn;

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
        setContentView(R.layout.activity_main);

        dbConn = new DatabaseConnection(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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
                dbConn.eatFood(new Food(1, "Apfel", Food.FRUITS));
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
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_clear_history) {
            Dialogue.askForConfirmation(this, R.string.clear_history, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dbConn.clearHistory();
                    for (int type : Food.getTypes()) {
                        FoodFragment fragment = getFragmentByType(type);
                        if (fragment != null) fragment.updateList();
                    }
                }
            });
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListFragmentInteraction(Food item) {
        dbConn.eatFood(item);
        FoodFragment fragment = getFragmentByType(item.getType());
        if (fragment != null) {
            fragment.updateList();
        }
    }

    @Override
    public void onListFragmentLongInteraction(final Food item) {
        Dialogue.chooseFromList(this, "Choose action", new String[] {"Undo 'eaten'", "Un-/Hide food", "Cancel"}, new Dialogue.OnChoiceSelectedListener() {
            @Override
            public void onChoiceSelected(int choice) {
                switch (choice) {
                    case 0:
                        dbConn.unEatFood(item.getId());
                        break;
                    case 1:
                        dbConn.hideFood(item.getId());
                        break;
                }
                FoodFragment fragment = getFragmentByType(item.getType());
                if (fragment != null) {
                    fragment.updateList();
                }
            }
        });
    }

    @Override
    public List<Food> loadFood(int type) {
        return dbConn.loadFood(type);
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

    private FoodFragment getFragmentByType(int type) {
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
}
