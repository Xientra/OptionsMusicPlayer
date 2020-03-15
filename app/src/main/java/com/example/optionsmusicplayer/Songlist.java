package com.example.optionsmusicplayer;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Songlist {

    public List<Song> songs;

    public Songlist() {
        songs = new LinkedList<Song>();
    }

    public static Songlist getListFromRaw() {
        Song[] s = new Song[3];

        s[0] = new Song("raw://" + R.raw.boss_fall);
        s[1] = new Song("raw://" + R.raw.calm_dead);
        s[2] = new Song("raw://" + R.raw.calm_outlands);

        Songlist result = new Songlist();
        result.songs = Arrays.asList(s);

        return result;
    }

    public int size() {
        return songs.size();
    }
}
