package edu.caltech.cs2.project02.choosers;

import com.sun.source.tree.Tree;
import edu.caltech.cs2.project02.interfaces.IHangmanChooser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class EvilHangmanChooser implements IHangmanChooser {
  private int guessesLeft;
  private SortedSet guessed;
  private String currentPattern;
  private TreeSet<String> possibilites;


  public EvilHangmanChooser(int wordLength, int maxGuesses) throws FileNotFoundException {
    if((wordLength < 1) || (maxGuesses < 1)) {
      throw new IllegalArgumentException("wordLength and maxGuess both must be greater than 1");
    }
    this.guessesLeft = maxGuesses;
    this.possibilites = new TreeSet<>();
    try {
      File file = new File("data/scrabble.txt");
      Scanner scanner = new Scanner(file);
      this.guessed = new TreeSet<>();
      while(scanner.hasNextLine()) {
        String p = scanner.nextLine();
        if (p.length() == wordLength) {
          this.possibilites.add(p);
        }
      }
      if(this.possibilites.size() < 1) {
        throw new IllegalStateException("No words of length wordLength");
      }
      scanner.close();
    }
    catch (FileNotFoundException e) {
      throw new FileNotFoundException("File not found");
    }
    String pattern = "";
    for(int i = 0; i < wordLength; i++) {
      pattern += "-";
    }
    this.currentPattern = pattern;
  }

  @Override
  public int makeGuess(char letter) {
    if (letter > 122 || letter < 97){
      throw new IllegalArgumentException("Lowercase guesses only.");
    }
    if (this.guessesLeft < 1) {
      throw new IllegalStateException("L. You're outta guesses.");
    }
    if (getGuesses().contains(letter)) {
      throw new IllegalArgumentException("You've guessed that already");
    }
    this.guessed.add(letter);
    Map<String, TreeSet<String>> families = familyGenerator(letter);
    if (families.size() != 0) {
      reField(families);
    }
    return matches(letter);
  }

  private void reField(Map<String, TreeSet<String>> families) {
    int max = 0;
    for(String key : families.keySet()) {
      if(families.get(key).size() > max) {
        this.possibilites.clear();
        this.possibilites.addAll(families.get(key));
        this.currentPattern = key;
        max = families.get(key).size();
      }
    }
  }

  private Map<String, TreeSet<String>> familyGenerator(char letter) {
    Map<String, TreeSet<String>> families = new TreeMap<>();
    for(String word : this.possibilites) {
      String currPattern = makePattern(letter, word);
      TreeSet<String> currFamily = new TreeSet<>();
      if (!families.containsKey(currPattern)) {
        families.put(currPattern, currFamily);
      }
      families.get(currPattern).add(word);
    }
    return families;
  }

  private String makePattern(char letter, String word) {
    String pattern = "";
    for (int i = 0; i < word.length(); i++) {
      if (word.charAt(i) != letter) {
        pattern += this.currentPattern.charAt(i);
      }
      else {
        pattern += letter;
      }
    }
    return pattern;
  }

  private int matches(char letter) {
    int match = 0;
    for (int i = 0; i < this.currentPattern.length(); i++) {
      if (this.currentPattern.charAt(i) == letter) {
        match++;
      }
    }
    if (match < 1) {
      this.guessesLeft--;
    }
    return match;
  }

  @Override
  public boolean isGameOver() {
    if (getGuessesRemaining() < 1 || !getPattern().contains("-")) {
      return true;
    }
    return false;
  }

  @Override
  public String getPattern() {
    return this.currentPattern;
  }

  @Override
  public SortedSet<Character> getGuesses() {
    return this.guessed;
  }

  @Override
  public int getGuessesRemaining() {
    return this.guessesLeft;
  }

  @Override
  public String getWord() {
    this.guessesLeft = 0;
    this.currentPattern = this.possibilites.first();
    return this.possibilites.first();
  }
}