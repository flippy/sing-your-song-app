package dev.flippy.singyoursong;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private String TAG = MainActivity.class.getSimpleName();
    private static SongDatabase songDatabase;
    private ViewPager viewPager;
    private int selectedList = 0;
    private String searchText = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        songDatabase = new SongDatabase(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("By Song Title"));
        tabLayout.addTab(tabLayout.newTab().setText("By Artist"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        // If we have a saved state then we can restore it now
        if (savedInstanceState != null) {
            searchText = savedInstanceState.getString("searchText", "");
            selectedList = savedInstanceState.getInt("selectedList", 0);
        }

        viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        // Add the list selection.
        Spinner dropdown = findViewById(R.id.listDropdown);
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // If the list really changed update the results.
                if (position != selectedList) {
                    selectedList = position;
                    viewPager.getAdapter().notifyDataSetChanged();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });
        updateListValues();

        // Add the text filter search.
        EditText search_box = findViewById(R.id.searchBox);
        search_box.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String search_string = s.toString();
                // If the search text really changed update the results.
                if (!search_string.equals(searchText)) {
                    searchText = s.toString();
                    viewPager.getAdapter().notifyDataSetChanged();
                }
            }
        });

        // Fetch songs if not yet done.
        if (songDatabase.isEmpty()) {
            Log.e(TAG, "Song database empty --> fetching data online.");

            FetchSongData.TaskListener listener = new FetchSongData.TaskListener() {
                @Override
                public void onFinished() {
                    updateListValues();
                    viewPager.getAdapter().notifyDataSetChanged();
                }
            };

            new FetchSongData(this, MainActivity.this, listener).execute();
        }
    }

    /**
     * Fill the list selection spinner with the latest set of lists.
     */
    private void updateListValues() {
        Spinner dropdown = findViewById(R.id.listDropdown);
        Cursor listsCursor = MainActivity.getSongDatabase().getLists();
        ArrayList<String> lists = new ArrayList<>();
        lists.add("All Songs");
        if (listsCursor != null) {
            try {
                while (listsCursor.moveToNext()) {
                    int index = listsCursor.getColumnIndexOrThrow("Title");
                    lists.add(listsCursor.getString(index));
                }
            } finally {
                listsCursor.close();
            }
        }
        ArrayAdapter<String> dropdown_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, lists);
        dropdown.setAdapter(dropdown_adapter);
        dropdown.setSelection(selectedList);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settings_activity = new Intent(this,SettingsActivity.class);
            startActivityForResult(settings_activity, 1);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Returning back from the settings.
        updateListValues();
        viewPager.getAdapter().notifyDataSetChanged();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // Make sure to call the super method so that the states of our views are saved
        super.onSaveInstanceState(outState);
        // Save our own state now
        outState.putString("searchText", searchText);
        outState.putInt("selectedList", selectedList);
    }

    public static SongDatabase getSongDatabase() {
        return songDatabase;
    }

    public int getSelectedList() {
        return selectedList;
    }

    public String getSearchText() {
        return searchText;
    }
}
