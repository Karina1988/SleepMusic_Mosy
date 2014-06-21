package com.example.sleepmusic2;
import java.util.ArrayList;
import java.util.HashMap;
 
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
 
public class PlayListActivity extends ListActivity {
    // Songs list
    public ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playlist);
 
        ArrayList<HashMap<String, String>> songsListData = new ArrayList<HashMap<String, String>>();
 
        SongsManager plm = new SongsManager();
        // get all songs from sdcard
        this.songsList = plm.getPlayList();
 
        // looping through playlist
        for (int i = 0; i < songsList.size(); i++) {
            // creating new HashMap
            HashMap<String, String> song = songsList.get(i);
 
            // adding HashList to ArrayList
            songsListData.add(song);
        }
 
        // Adding menuItems to ListView
        ListAdapter adapter = new SimpleAdapter(this, songsListData,R.layout.playlist_item, new String[] { "songTitle" }, new int[] {R.id.songTitle });
 
        setListAdapter(adapter);
 
        // selecting single ListView item
        ListView lv = getListView();
        // listening to single listitem click
        lv.setOnItemClickListener(new OnItemClickListener() {
 
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                // getting listitem index
                int songIndex = position;
                String go = "go";
                String songTitle = songsList.get(songIndex).get("songTitle");
                String songPath = songsList.get(songIndex).get("songPath");
                
                // Starting new intent
                Intent in = new Intent(getApplicationContext(),
                        SleepMusic2.class);
                // Sending songIndex to PlayerActivity
                in.putExtra("songPath", songPath);
                in.putExtra("songTitle", songTitle);
                in.putExtra("index", songIndex);
                in.putExtra("go", go);
                setResult(100, in);
                // Closing PlayListView
               // finish();
                startActivity(in);
            }
        });
    }
}
