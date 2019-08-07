package dev.flippy.singyoursong;

import android.app.Activity;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class TabFragment1 extends Fragment {

    private String TAG = TabFragment1.class.getSimpleName();
    private ProgressDialog pDialog;
    private ListView lv;
    private Activity activity;

    // URL to get contacts JSON
    private static String url = "https://flippy.dev/sing-your-song-app/sys-songlist-firstpart.json";

    ArrayList<HashMap<String, String>> songList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        activity = (MainActivity) getActivity();
        View view = inflater.inflate(R.layout.tab_fragment_1, container, false);
        songList = new ArrayList<>();
        Log.e(TAG, "Tab Fragment 1 onCreateView");

        // Load the songs.
        Cursor songsCursor = MainActivity.getSongDatabase().getSongMatches("", null);
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
                    song.put("artist", songsCursor.getString(index));

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
                activity, songList,
                R.layout.list_item, new String[]{"title", "artist",
                "id"}, new int[]{R.id.title,
                R.id.artist, R.id.id});

        lv.setAdapter(adapter);

        //new TabFragment1.GetSongs().execute();

        return view;
    }

    /**
     * Async task class to get json by making HTTP call
     */
    /*private class GetSongs extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(activity);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray contacts = jsonObj.getJSONArray("songs");

                    // looping through All Contacts
                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);

                        String id = c.getString("number");
                        String artist = c.getString("artist");
                        String title = c.getString("title");

                        // Phone node is JSON Object
                        //JSONObject phone = c.getJSONObject("phone");
                        //String mobile = c.getString("list");

                        // tmp hash map for single contact
                        HashMap<String, String> song = new HashMap<>();

                        // adding each child node to HashMap key => value
                        song.put("id", id);
                        song.put("title", title);
                        song.put("artist", artist);

                        // adding contact to contact list
                        songList.add(song);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(activity.getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity.getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            // Updating parsed JSON data into ListView

            ((BaseAdapter) lv.getAdapter()).notifyDataSetChanged();
        }

    }*/
}
