package com.example.optionsmusicplayer;

import android.media.MediaPlayer;
import android.os.Debug;
import android.util.Log;

import java.io.IOException;

public class Player {

    public MediaPlayer mp;
    private Songlist songlist;
    private int currentSong;

    public Player(Songlist songlist) {
        mp = new MediaPlayer();
        try {
            mp.setDataSource("raw://boss_fall.ogg");
            mp.prepare();
        } catch (IOException e) {
            Log.e("myTag", "music File not found");
            e.printStackTrace();
        }

        this.songlist = songlist;
        currentSong = 0;

        mp.seekTo(0); // goes to start
        mp.setVolume(0.5f, 0.5f);

        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                //nextSong();
            }
        });
        /*
                AudioManager am = null;

        // Request focus for music stream and pass AudioManager.OnAudioFocusChangeListener
        // implementation reference
        int result = am.requestAudioFocus(this, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);

        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            // Play
        }
         */
    }

    public void play() {

        mp.start();
    }

    public void pause() {
        mp.pause();
    }

    public void nextSong() {
        int next = currentSong + 1;
        if (next >= songlist.size()) {
            next = 0;
        }

        try {
            mp.stop();
            mp.reset();
            mp.setDataSource(songlist.songs.get(next).getPath());
            mp.prepare(); //TODO should not be called on the ui thread
            mp.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        currentSong = next;
    }

    public void previousSong() {
        int prev = currentSong - 1;
        if (prev <= 0) {
            prev = songlist.size() - 1;
        }
        try {
            mp.stop();
            mp.reset();
            mp.setDataSource(songlist.songs.get(prev).getPath());
            mp.prepare();
            mp.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        currentSong = prev;
    }
}
