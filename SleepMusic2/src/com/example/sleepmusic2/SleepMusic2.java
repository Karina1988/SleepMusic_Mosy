package com.example.sleepmusic2;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.media.MediaPlayer.OnCompletionListener;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

public class SleepMusic2 extends Activity implements OnCompletionListener, SensorEventListener{

    private ProgressBar mProgress;
    private Handler mHandler = new Handler(); 
    private ImageButton btnPlay;
    private ImageButton btnPause;
    private ImageButton btnForward;
    private ImageButton btnBack;
    private ImageButton btnPlaylist;
    private ImageButton btnNext;
    private ImageButton btnPrev;
    private TextView songtitel;
    private SensorManager manager;
    private SensorEventListener listener;
    private Sensor sensorLight;
    private SongsManager songManager;
    private ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();
    private MediaPlayer mp;
    private int songindex; 
  
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sleep_music2);
		
		//MediaPlayer
		mp = new MediaPlayer();
		songManager = new SongsManager();
		
		btnPlay = (ImageButton) findViewById(R.id.btnPlay);
		btnPause = (ImageButton) findViewById(R.id.btnPause);
        btnForward = (ImageButton) findViewById(R.id.btnForward);
        btnBack = (ImageButton) findViewById(R.id.btnBack);
        btnPlaylist = (ImageButton) findViewById(R.id.btnPlaylist);
        btnNext = (ImageButton) findViewById(R.id.btnNext);
        btnPrev = (ImageButton) findViewById(R.id.btnPrev);
        mProgress = (ProgressBar)findViewById(R.id.progress);        
		songtitel = (TextView)findViewById(R.id.songtitel);
        songsList = songManager.getPlayList();
		       
		Intent iin= getIntent();
        Bundle b = iin.getExtras();
        if(b!=null){
            songindex = b.getInt("index", 1);
            try {
            	mp.setDataSource((String) b.getString("songPath", "test"));
				mp.prepare();	
				
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }     		
		
       //Progressbar updaten
       new Thread(new Runnable() {
           public void run() {
        	   int currentPosition= 0;
               int total = mp.getDuration();
               while (mp!=null && currentPosition<total) {
                   try {
                       Thread.sleep(1000);
                       currentPosition= mp.getCurrentPosition();
                   } catch (InterruptedException e) {
                       return;
                   } catch (Exception e) {
                       return;
                   }            
                   mProgress.setProgress(currentPosition);
               }
           }      
           }).start();

	//Lichtsensor
		manager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
		sensorLight = manager.getDefaultSensor(Sensor.TYPE_LIGHT);
		listener = new SensorEventListener(){
		float maxlicht = sensorLight.getMaximumRange();
				@Override
				public void onAccuracyChanged(Sensor sensor,int accuracy) {}
							
				@Override
				public void onSensorChanged(SensorEvent event) {
					//Einstellung der Lautstärke
					float licht = event.values[0];
					float volume = (licht/maxlicht)*100;
				
					if (volume < 0.1f && mp.isPlaying()== true){
						mp.pause();
					}
					else if (volume>0.1f){
						mp.start();
						mp.setVolume(volume, volume);
					}
										
					float [] values =event.values;
					float x =(values[0]/100);
					
					//Hintergrundfarbe ändern   
					View someView = findViewById(R.id.hgfarbe);

					View root = someView.getRootView();					   
					
						if (x > 0.9f){
							//gelb
							root.setBackgroundColor(0XFFFFF79A);
						} else if (x <0.89f && x>0.8f){
							//orange
							root.setBackgroundColor(0XFFA0410D);
						} else if(x < 0.79f && x >0.7f){
							//rot
							root.setBackgroundColor(0XFF9E0039);
						} else if(x <0.69f && x>0.6f){
							//pink
							root.setBackgroundColor(0XFF9E005D);
						} else if(x  <0.59f && x>0.5f){
							//lila
							root.setBackgroundColor(0XFF630460);
						} else if(x < 0.49f && x> 0.4f){
							//grün
							root.setBackgroundColor(0XFF007236);	
						} else if(x < 0.39f && x>0.3f){
							//türkis
							root.setBackgroundColor(0XFF00746B);
						} else if(x < 0.29f && x>0.2f){
							//dunkeltürkis
							root.setBackgroundColor(0XFF0076A3);
						} else if(x < 0.19f && x> 0.1f){
							//blau
							root.setBackgroundColor(0XFF004B80);
						} else if(x <0.1f){
							//schwarz
							root.setBackgroundColor(0XFF000000);
						}
				}
			};
			
	//onClickListenener für Button		
			
			//Button Song auswählen
			btnPlaylist.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					mp.pause();
					manager.unregisterListener(listener);
					//mp.release();
					playlister();					
				}
			});
	
			//play-Button
			btnPlay.setOnClickListener(new View.OnClickListener() {
				 
	            @Override
	            public void onClick(View arg0) {
	                // wenn Lied auf Pause war
	                if(!mp.isPlaying()){
	                    if(mp.getCurrentPosition()>0){	                              
	                        mp.start();	 
	                        manager.registerListener(listener, sensorLight, SensorManager.SENSOR_DELAY_NORMAL);}
	                    //wenn neues Lied
	                    else{
	                    	playSong(songindex);
	                    }
	                }	 
	            }
	        });
			
			//pause-Button
			btnPause.setOnClickListener(new View.OnClickListener() {
				 
	            @Override
	            public void onClick(View arg0) {	               
	                if(mp.isPlaying()){
	                    if(mp!=null){	                     
	                        mp.pause();	 
	                        manager.unregisterListener(listener);
	                    }
	                }
	            }
	        });
			
			//Back-Button
			btnBack.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					mp.seekTo(0);				
				}
			});
			
			//Forward-Button
			btnForward.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					mp.seekTo((mp.getCurrentPosition() + 10000));					
				}
			});
			
			//Next-Button
		    btnNext.setOnClickListener(new View.OnClickListener() {
		    	 
		         @Override
		         public void onClick(View arg0) {
		        	 if(songindex < (songsList.size() - 1)){
		        		 playSong(songindex + 1);
		                 songindex = songindex + 1;
		             }else{
		                 playSong(0);
		                 songindex = 0;
		             }		 
		         }
		    });
		     
		     //Prev-Button
		     btnPrev.setOnClickListener(new View.OnClickListener() {
		    	 
		            @Override
		            public void onClick(View arg0) {
		                if(songindex > 0){
		                    playSong(songindex - 1);
		                    songindex = songindex - 1;
		                }else{
		                    // play last song
		                    playSong(songsList.size() - 1);
		                    songindex = songsList.size() - 1;
		                }
		            }
		        });
		     
	}
	
	
	public void  playSong(int songIndex){    
        try {
            mp.reset();
            mp.setDataSource(songsList.get(songIndex).get("songPath"));
            mp.prepare();
            mp.start();   
            manager.registerListener(listener, sensorLight, SensorManager.SENSOR_DELAY_NORMAL);
            String songTitle = songsList.get(songIndex).get("songTitle");
            songtitel.setText(songTitle);
            mProgress.setProgress(0);
            mProgress.setMax(mp.getDuration());
            updateTimestamp();
            mp.setOnCompletionListener(this);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
	
	//wenn Lied zuende, beim nächsten weiterspielen
	public void onCompletion(MediaPlayer arg0) {	 
            if(songindex < (songsList.size() - 1)){
                playSong(songindex + 1);
                songindex = songindex + 1;
            }else{
                // spiele ersten Song
                playSong(0);
                songindex = 0;
            }		 
    }
    
	
	//Zeitanzeige
	public void updateTimestamp() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    } 
	
	private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {                                         
            TextView min = (TextView)findViewById(R.id.minute);
        	TextView sek = (TextView)findViewById(R.id.sekunde);
    		int position = (mp.getCurrentPosition()/1000);
    		int minute = position/60;              
    		min.setText(Integer.toString(minute) + ":");
    		if(position < 10){
    			sek.setText("0" + Integer.toString(position));
    		}else{
    		sek.setText(Integer.toString(position));
    		}
    		while(position > 59){             
    			position = position - 59; 
    			if(position < 10){
    				sek.setText("0" + Integer.toString(position));
    			}else{
    				sek.setText(Integer.toString(position));	
    			}
    		}
            mHandler.postDelayed(this, 100);
     }};            
     
     
	private void playlister(){
		Intent intent = new Intent(this, PlayListActivity.class);
		startActivity(intent);	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sleep_music2, menu);
		return true;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
    public void onDestroy(){
    super.onDestroy();
       mp.release();
    }
}

