package dev.flippy.singyoursong;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Async task class to get json by making HTTP call
 */
public class FetchSongData extends AsyncTask<Void, Void, Void> {

    public interface TaskListener {
        public void onFinished();
    }

    // This is the reference to the associated listener
    private final TaskListener taskListener;

    // URL to get contacts JSON
    private String url = "https://flippy.dev/sing-your-song-app/sys-songlist-firstpart.json";
    private ProgressDialog pDialog;
    private Activity activity;
    private Context context;
    private String TAG = FetchSongData.class.getSimpleName();

    public FetchSongData(Activity caller, Context context, TaskListener listener) {
        super();
        this.activity = caller;
        this.context = context;
        // The listener reference is passed in through the constructor
        this.taskListener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // Showing progress dialog
        pDialog = new ProgressDialog(context);
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

                ArrayList<HashMap<String, String>> songList = new ArrayList<>();

                // looping through All Contacts
                for (int i = 0; i < contacts.length(); i++) {
                    JSONObject c = contacts.getJSONObject(i);

                    String id = c.getString("number");
                    String artist = c.getString("artist");
                    String title = c.getString("title");
                    String cdtype = c.getString("cdtype");
                    String list = c.getString("list");

                    // tmp hash map for single contact
                    HashMap<String, String> song = new HashMap<>();

                    // adding each child node to HashMap key => value
                    song.put("id", id);
                    song.put("title", title);
                    song.put("artist", artist);
                    song.put("cdtype", cdtype);
                    song.put("list", list);

                    // adding contact to contact list
                    songList.add(song);
                }

                // Add the songs to the database.
                SongDatabase songDatabase = MainActivity.getSongDatabase();
                songDatabase.setSongs(songList);

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
        /**
         * Updating parsed JSON data into ListView
         * */

        if(this.taskListener != null) {
            // And if it is we call the callback function on it.
            this.taskListener.onFinished();
        }
    }

}
