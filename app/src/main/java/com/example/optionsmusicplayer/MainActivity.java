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

public class MainActivity extends AppCompatActivity implements AudioManager.OnAudioFocusChangeListener {

    Button playBtn;
    SeekBar positionBar;
    SeekBar volumeBar;
    TextView elapsedTimeLabel;
    TextView remainingTime;

    MediaPlayer mp;
    int totalTime;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playBtn = (Button) findViewById(R.id.playBtn);
        positionBar = (SeekBar) findViewById(R.id.positionBar);
        volumeBar = (SeekBar) findViewById(R.id.volumeBar);
        elapsedTimeLabel = (TextView) findViewById(R.id.elapsedTimeLabel);
        remainingTime = (TextView) findViewById(R.id.remainingTimeLabel);

        //Button OnClick
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mp.isPlaying() == false) {
                    mp.start();
                    playBtn.setBackgroundResource(android.R.drawable.ic_media_pause);
                } else {
                    mp.pause();
                    playBtn.setBackgroundResource(android.R.drawable.ic_media_play);
                }
            }
        });

        //Media Player
        mp = MediaPlayer.create(this, R.raw.boss_fall);
        mp.setLooping(true);
        mp.seekTo(0); // goes to start
        mp.setVolume(0.5f, 0.5f);
        totalTime = mp.getDuration();
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

            }
        });

        AudioManager am = null;

        // Request focus for music stream and pass AudioManager.OnAudioFocusChangeListener
        // implementation reference
        int result = am.requestAudioFocus(this, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);

        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            // Play
        }


        //position bar
        positionBar.setMax(totalTime);
        positionBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mp.seekTo(progress);
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
                while (mp != null) {
                    try {
                        // UI can only be changed by the thread that created(?) it.
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                updateProgress(mp.getCurrentPosition());
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
                mp.setVolume(volumeNum, volumeNum);
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
