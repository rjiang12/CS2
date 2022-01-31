package edu.caltech.cs2.project02.guessers;

import edu.caltech.cs2.project02.interfaces.IHangmanGuesser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class AIHangmanGuesser implements IHangmanGuesser {
  private static String DICTIONARY = "data/scrabble.txt";


  @Override
  public char getGuess(String pattern, Set<Character> guesses) throws FileNotFoundException {
    SortedSet<String> allWords = getPossible(pattern, guesses);
    return maxLetter(allWords, guesses);
  }

  private char maxLetter (SortedSet<String> words, Set<Character> guesses) {
    int max = 0;
    char maxChar = ' ';
    for(char i = 'a'; i <= 'z'; i++) {
      int letterCount = 0;
      for(String word : words) {
        for(int j = 0; j < word.length(); j++) {
          if (word.charAt(j) == i && (!guesses.contains(word.charAt(j)))) {
            letterCount++;
          }
        }
      }
      if (letterCount > max) {
        max = letterCount;
        maxChar = i;
      }
    }
    return maxChar;
  }

  private SortedSet<String> getPossible(String pattern, Set<Character> guesses) throws FileNotFoundException {
    SortedSet<String> allWords = new TreeSet<>();
    try {
      File file = new File(DICTIONARY);
      Scanner scanner = new Scanner(file);
      while(scanner.hasNextLine()) {
        String check = scanner.nextLine();
        if (fitsPattern(check, pattern, guesses)) {
          allWords.add(check);
        }
      }
      scanner.close();
    }
    catch (FileNotFoundException e) {
      throw new FileNotFoundException("File not found");
    }
    return allWords;
  }

  public boolean fitsPattern(String word, String pattern, Set<Character> guesses) {
    if(word.length() != pattern.length()) {
      return false;
    }
    for(int i = 0; i < pattern.length(); i++) {
      if (pattern.charAt(i) == '-' && guesses.contains(word.charAt(i))) {
        return false;
      }
      if ((pattern.charAt(i) >= 'a' && pattern.charAt(i) <= 'z') && (word.charAt(i) != pattern.charAt(i))) {
        return false;
      }
    }
    return true;
  }
}
