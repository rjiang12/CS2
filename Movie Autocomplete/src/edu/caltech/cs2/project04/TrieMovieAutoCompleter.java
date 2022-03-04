package edu.caltech.cs2.project04;

import edu.caltech.cs2.datastructures.ArrayDeque;
import edu.caltech.cs2.datastructures.LinkedDeque;
import edu.caltech.cs2.datastructures.TrieMap;
import edu.caltech.cs2.interfaces.IDeque;
import edu.caltech.cs2.interfaces.ITrieMap;


public class TrieMovieAutoCompleter extends AbstractMovieAutoCompleter {
    private static ITrieMap<String, IDeque<String>, IDeque<String>> titles = new TrieMap<>((IDeque<String> s) -> s);

    public static void populateTitles() {
        for(String movieTitle : ID_MAP.keySet()) {
            IDeque<String> suffixes = new LinkedDeque<>();
            String[] split = movieTitle.toLowerCase().split(" ");
            for(int i = split.length - 1; i >= 0; i--) {
                suffixes.addFront(split[i]);
                IDeque<String> titleDeque = new LinkedDeque<>();
                if(titles.containsKey(suffixes)) {
                    titleDeque.addAll(titles.get(suffixes));
                }
                titleDeque.add(movieTitle);
                titles.put(suffixes, titleDeque);
            }
        }
    }

    public static IDeque<String> complete(String term) {
        IDeque<String> prefixes = new LinkedDeque<>();
        String[] split = term.split(" ");
        for(String word : split) {
            prefixes.addBack(word);
        }
        IDeque<String> completions = new LinkedDeque<>();
        IDeque<IDeque<String>> movieTitles = (IDeque<IDeque<String>>) titles.getCompletions(prefixes);
        for(IDeque<String> movie : movieTitles) {
            for(String title : movie) {
                if(!completions.contains(title)) {
                    completions.add(title);
                }
            }
        }
        return completions;
    }
}

