package dev.flippy.singyoursong;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.HashMap;

public class TabFragment1 extends Fragment {

    private String TAG = TabFragment1.class.getSimpleName();
    private ListView lv;
    private MainActivity activity;

    ArrayList<HashMap<String, String>> songList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        activity = (MainActivity) getActivity();
        View view = inflater.inflate(R.layout.tab_fragment_1, container, false);
        songList = new ArrayList<>();

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

        lv = (ListView) view.findViewById(R.id.list);
        ListAdapter adapter = new IndexableListAdapter(
                activity,
                songList,
                R.layout.list_item_song,
                new String[]{"title", "artist", "id", "cdType"},
                new int[]{R.id.title, R.id.artist, R.id.id, R.id.cdType},
                "title");
        lv.setAdapter(adapter);

        // Add a view in case the result is empty.
        TextView emptyText = (TextView)view.findViewById(android.R.id.empty);
        /*if (songList.size() <= 0) {
            emptyText.setVisibility(View.VISIBLE);
        }*/
        lv.setEmptyView(emptyText);

        // Update the song count.
        TextView songCount = (TextView) view.findViewById(R.id.itemCount);
        songCount.setText(songList.size() + " song" + (songList.size() != 1 ? "s" : "") + " found");

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                HashMap<String, String> song_info = (HashMap<String, String>) parent.getItemAtPosition(position);
                Intent intent = new Intent(activity, SongDetailsActivity.class);
                intent.putExtra("song", song_info);
                startActivity(intent);
            }
        });

        SideSelector sideSelector = (SideSelector) view.findViewById(R.id.side_selector);
        sideSelector.setListView(lv);

        return view;
    }
}
