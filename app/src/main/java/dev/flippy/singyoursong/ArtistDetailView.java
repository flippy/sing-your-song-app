package dev.flippy.singyoursong;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.HashMap;

public class ArtistDetailView extends AppCompatActivity {
    private ListView lv;
    private String artist;
    ArrayList<HashMap<String, String>> songList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_artist_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        songList = new ArrayList<>();

        this.artist = getIntent().getStringExtra("artist");

        SongDatabase.SongQuery query = new SongDatabase.SongQuery();
        query.setArtist(this.artist);
        query.setList(getIntent().getIntExtra("list", 0));
        query.setSearchText(getIntent().getStringExtra("searchText"));

        // Load the songs.
        Cursor songsCursor = MainActivity.getSongDatabase().getSongMatches(query);
        if (songsCursor != null) {
            try {
                while (songsCursor.moveToNext()) {
                    HashMap<String, String> song = new HashMap<>();
                    int index;

                    index = songsCursor.getColumnIndexOrThrow("ID");
                    song.put("id", songsCursor.getString(index));
                    index = songsCursor.getColumnIndexOrThrow("Title");
                    song.put("title", songsCursor.getString(index));
                    index = songsCursor.getColumnIndexOrThrow("Artist");
                    String artist = songsCursor.getString(index);
                    song.put("artist", (artist.trim().length() > 0 ? artist : "<No Artist>"));

                    // adding contact to contact list
                    songList.add(song);
                }
            } finally {
                songsCursor.close();
            }
        }

        lv = (ListView) findViewById(R.id.list);
        ListAdapter adapter = new SimpleAdapter(
                this, songList,
                R.layout.list_item_song, new String[]{"title", "artist",
                "id"}, new int[]{R.id.title,
                R.id.artist, R.id.id});

        lv.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_back) {
            setResult(Activity.RESULT_OK);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
