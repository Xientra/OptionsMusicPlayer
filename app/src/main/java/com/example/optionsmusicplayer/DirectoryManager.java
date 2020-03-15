package com.example.optionsmusicplayer;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.*;
import java.nio.file.*;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class DirectoryManager {
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static Set<String> searchDirectory(Path directory, String searched[]) {

        final Set<String> results = new HashSet<String>();

        try {
            Files.walk(directory, FileVisitOption.FOLLOW_LINKS) // get all files from directory
                    .filter(new Predicate<Path>() {
                        @Override
                        public boolean test(Path p) {
                            return Files.isRegularFile(p, LinkOption.NOFOLLOW_LINKS);
                        }
                    }) // filter only regular files
                    .map(new Function<Path, Object>() {
                        @Override
                        public Object apply(Path path) {
                            return path.getFileName();
                        }
                    })
                    .forEach(new Consumer<Object>() {
                        @Override
                        public void accept(Object s) {
                            results.add((String) s);
                        }
                    }); // add search results to 'results'

            // hi there internet!
        } catch (IOException e) {
            System.out.println("Given directory does not exist. " + e);
        }

        return results;
    }

}
