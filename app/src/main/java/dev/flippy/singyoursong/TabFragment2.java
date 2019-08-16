package dev.flippy.singyoursong;

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
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.Integer.parseInt;

public class TabFragment2 extends Fragment {

    private String TAG = TabFragment1.class.getSimpleName();
    private ListView lv;
    private MainActivity activity;

    ArrayList<HashMap<String, String>> artistList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        activity = (MainActivity) getActivity();
        View view = inflater.inflate(R.layout.tab_fragment_2, container, false);
        artistList = new ArrayList<>();

        // Load the songs by artist.
        SongDatabase.SongQuery query = new SongDatabase.SongQuery();
        query.setList(activity.getSelectedList());
        query.setSearchText(activity.getSearchText());
        Cursor artistCursor = MainActivity.getSongDatabase().getArtistMatches(query);
        if (artistCursor != null) {
            try {
                while (artistCursor.moveToNext()) {
                    HashMap<String, String> artist_info = new HashMap<>();
                    int index;
                    index = artistCursor.getColumnIndexOrThrow("Artist");
                    String artist = artistCursor.getString(index);
                    artist_info.put("artist", artist);
                    artist_info.put("artistText", (artist.trim().length() > 0 ? artist : "<No Artist>"));
                    index = artistCursor.getColumnIndexOrThrow("SongCount");
                    int count = parseInt(artistCursor.getString(index));
                    artist_info.put("count", count + " song" + (count > 1 ? "s" : ""));

                    // adding contact to contact list
                    artistList.add(artist_info);
                }
            } finally {
                artistCursor.close();
            }
        }

        lv = (ListView) view.findViewById(R.id.list);
        ListAdapter adapter = new SimpleAdapter(
                activity,
                artistList,
                R.layout.list_item_artist,
                new String[]{"artistText", "count"},
                new int[]{R.id.artist, R.id.count});
        lv.setAdapter(adapter);

        // Add a view in case the result is empty.
        TextView emptyText = (TextView)view.findViewById(android.R.id.empty);
        lv.setEmptyView(emptyText);

        // Update the song count.
        TextView songCount = (TextView) view.findViewById(R.id.itemCount);
        songCount.setText(artistList.size() + " artist" + (artistList.size() != 1 ? "s" : "") + " found");

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                HashMap<String, String> artist_info = (HashMap<String, String>) parent.getItemAtPosition(position);
                Intent intent = new Intent(activity, ArtistDetailView.class);
                intent.putExtra("artist", artist_info.get("artist"));
                intent.putExtra("list", activity.getSelectedList());
                intent.putExtra("searchText", activity.getSearchText());
                startActivity(intent);
            }
        });

        return view;
    }
}
