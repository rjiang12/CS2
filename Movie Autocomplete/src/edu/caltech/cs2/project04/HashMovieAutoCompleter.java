package edu.caltech.cs2.project04;

import edu.caltech.cs2.datastructures.ArrayDeque;
import edu.caltech.cs2.interfaces.IDeque;

import java.util.HashMap;
import java.util.Map;

public class HashMovieAutoCompleter extends AbstractMovieAutoCompleter {
    private static Map<String, IDeque<String>> titles = new HashMap<>();

    public static void populateTitles() {
        for(String movieTitle : ID_MAP.keySet()) {
            String toModify = movieTitle.toLowerCase();
            IDeque<String> suffixes = new ArrayDeque<>();
            suffixes.add(toModify);
            while(toModify.contains(" ")) {
                toModify = toModify.substring(toModify.indexOf(" ") + 1);
                suffixes.add(toModify);
                }
            titles.put(movieTitle, suffixes);
        }
    }

    public static IDeque<String> complete(String term) {
        IDeque containedTerm = new ArrayDeque();
        for(String title : titles.keySet()) {
            IDeque<String> suffixes = titles.get(title);
            for(String suffix : suffixes) {
                if(isPrefix(term, suffix)) {
                    containedTerm.add(title);
                    break;
                }
            }
        }
        return containedTerm;
    }

    public static boolean isPrefix(String term, String suffix) {
        if(term.length() > suffix.length()) {
            return false;
        }
        if(suffix.equals(term)) {
            return true;
        }
        if(suffix.length() >= term.length() + 1) {
            if(suffix.substring(0, term.length() + 1).equals(term + " ")){
                return true;
            }
        }
        return false;
    }
}
