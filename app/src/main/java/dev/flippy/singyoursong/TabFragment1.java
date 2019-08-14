package dev.flippy.singyoursong;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.HashMap;

public class TabFragment1 extends Fragment {

    private String TAG = TabFragment1.class.getSimpleName();
    private ProgressDialog pDialog;
    private ListView lv;
    private MainActivity activity;

    ArrayList<HashMap<String, String>> songList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        activity = (MainActivity) getActivity();
        View view = inflater.inflate(R.layout.tab_fragment_1, container, false);
        songList = new ArrayList<>();
        Log.e(TAG, "Tab Fragment 1 onCreateView");

        // Load the songs.
        SongDatabase.SongQuery query = new SongDatabase.SongQuery();
        query.setList(activity.getSelectedList());
        query.setSearchText(activity.getSearchText());
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
                    index = songsCursor.getColumnIndexOrThrow("CDType");
                    song.put("cdType", songsCursor.getString(index));

                    // adding contact to contact list
                    songList.add(song);
                }
            } finally {
                songsCursor.close();
            }
        }
        Log.e(TAG, "Songs loaded");

        lv = (ListView) view.findViewById(R.id.list);
        ListAdapter adapter = new SimpleAdapter(
                activity,
                songList,
                R.layout.list_item_song,
                new String[]{"title", "artist", "id", "cdType"},
                new int[]{R.id.title, R.id.artist, R.id.id, R.id.cdType});

        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                HashMap<String, String> song_info = (HashMap<String, String>) parent.getItemAtPosition(position);
                Log.e(TAG, song_info.get("title"));
                Intent intent = new Intent(activity, SongDetailsActivity.class);
                intent.putExtra("song", song_info);
                startActivity(intent);
            }
        });

        return view;
    }
}
