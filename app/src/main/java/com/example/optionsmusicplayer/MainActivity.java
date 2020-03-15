package com.example.optionsmusicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements AudioManager.OnAudioFocusChangeListener {

    Button playBtn;
    Button nextSongBtn;
    Button prevSongBtn;
    SeekBar positionBar;
    SeekBar volumeBar;
    TextView elapsedTimeLabel;
    TextView remainingTime;

    Player player;
    int totalTime;
    Songlist songlist;
    int currentSong = 0;

    private Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // create Player
        songlist = Songlist.getListFromRaw();
        player = new Player(songlist);
        totalTime = player.mp.getDuration();

        // get ui references
        playBtn = findViewById(R.id.playBtn);
        nextSongBtn = findViewById(R.id.nextSongBtn);
        prevSongBtn = findViewById(R.id.prevSongBtn);
        positionBar = findViewById(R.id.positionBar);
        volumeBar = findViewById(R.id.volumeBar);
        elapsedTimeLabel = findViewById(R.id.elapsedTimeLabel);
        remainingTime = findViewById(R.id.remainingTimeLabel);

        //Button OnClick
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!player.mp.isPlaying()) {
                    player.play();
                    playBtn.setBackgroundResource(android.R.drawable.ic_media_pause);
                } else {
                    player.pause();
                    playBtn.setBackgroundResource(android.R.drawable.ic_media_play);
                }
            }
        });
        nextSongBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.nextSong();
            }
        });
        prevSongBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.previousSong();
            }
        });

        //position bar
        positionBar.setMax(totalTime);
        positionBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    player.mp.seekTo(progress);
                    positionBar.setProgress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });


        // new Thread to update progressBar
        handler = new Handler(getApplicationContext().getMainLooper());
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (player.mp != null) {
                    try {
                        // UI can only be changed by the thread that created(?) it.
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                updateProgress(player.mp.getCurrentPosition());
                            }
                        });

                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        // volume bar
        volumeBar.setProgress(volumeBar.getMax() / 2);
        volumeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float volumeNum = progress / 100f;
                player.mp.setVolume(volumeNum, volumeNum);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private void updateProgress(int progress) {
        positionBar.setProgress(progress);

        elapsedTimeLabel.setText(createTimeLabel(progress));
        String s = "-" + createTimeLabel(totalTime - progress);
        remainingTime.setText(s);
    }

    private String createTimeLabel(int time) {
        String timeLabel = "";
        int min = time / 1000 / 60;
        int sec = time / 1000 % 60;

        timeLabel = min + ":";
        if (sec < 10)
            timeLabel += "0";
        timeLabel += sec;

        return timeLabel;
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
            // Pause
        } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
            // Resume
        } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
            // Stop or pause depending on your need
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // stop MediaPlayer
    }
}
