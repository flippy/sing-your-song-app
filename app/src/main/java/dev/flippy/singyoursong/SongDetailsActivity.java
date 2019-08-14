package dev.flippy.singyoursong;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.HashMap;

public class SongDetailsActivity extends AppCompatActivity {
    HashMap<String, String> song_info;
    private static final String TAG = "SongDetailsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.song_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set the song infos.
        song_info = (HashMap<String, String>) getIntent().getSerializableExtra("song");
        TextView songTitleText = findViewById(R.id.songTitleText);
        songTitleText.setText(song_info.get("title"));
        TextView artistText = findViewById(R.id.artistText);
        artistText.setText(song_info.get("artist"));
        TextView cdTypeText = findViewById(R.id.cdTypeText);
        cdTypeText.setText(song_info.get("cdType"));
        TextView songIdText = findViewById(R.id.songIdText);
        songIdText.setText(song_info.get("id"));
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
