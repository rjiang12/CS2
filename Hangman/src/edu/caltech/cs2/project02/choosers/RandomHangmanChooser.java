package edu.caltech.cs2.project02.choosers;

import edu.caltech.cs2.project02.interfaces.IHangmanChooser;

import java.io.*;
import java.io.File;
import java.util.*;
import java.util.Random;
import java.util.Scanner;

public class RandomHangmanChooser implements IHangmanChooser {

  private static final Random RANDOM = new Random();
  private SortedSet<Character> guessed;
  private int guessesLeft;
  private final String CHOSEN;

  public RandomHangmanChooser(int wordLength, int maxGuesses) throws FileNotFoundException {
      if((wordLength < 1) || (maxGuesses < 1)) {
        throw new IllegalArgumentException("wordLength and maxGuess both must be greater than 1");
      }
      this.guessesLeft = maxGuesses;
    SortedSet<String> lines = new TreeSet<>();
      try {
        File file = new File("data/scrabble.txt");
        Scanner scanner = new Scanner(file);
        this.guessed = new TreeSet<>();
        while(scanner.hasNextLine()) {
          String p = scanner.nextLine();
          if (p.length() == wordLength) {
            lines.add(p);
          }
        }
        if(lines.size() < 1) {
            throw new IllegalStateException("No words of length wordLength");
        }
        scanner.close();
      }
      catch (FileNotFoundException e) {
        throw new FileNotFoundException("File not found");
      }
      Iterator<String> iterator = lines.iterator();
      int rand = RANDOM.nextInt(lines.size());
      int count = 0;
      String c = "";
      while(iterator.hasNext() && count <= rand) {
        c = iterator.next();
        count++;
      }
      CHOSEN = c;
  }

  @Override
  public int makeGuess(char letter) {
    if (letter > 122 || letter < 97){
      throw new IllegalArgumentException("Lowercase guesses only.");
    }
    if (guessesLeft < 1) {
      throw new IllegalStateException("L. You're outta guesses.");
    }
    if (getGuesses().contains(letter)) {
      throw new IllegalArgumentException("You've guessed that already");
    }
    this.guessed.add(letter);
    int count = 0;
    for(int i = 0; i < CHOSEN.length(); i++) {
      if(CHOSEN.charAt(i) == letter) {
        count++;
      }
    }
    if(count < 1) {
      this.guessesLeft--;
    }
    return count;
  }

  @Override
  public boolean isGameOver() {
    if((guessesLeft < 1) || getPattern().equals(CHOSEN)) {
      return true;
    }
    return false;
  }

  @Override
  public String getPattern() {
    String pattern = "";
    for(int i = 0; i < this.CHOSEN.length(); i++) {
      if(getGuesses().contains(this.CHOSEN.charAt(i))) {
        pattern += this.CHOSEN.charAt(i);
      }
      else {
        pattern += "-";
      }
    }
    return pattern;
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
    return this.CHOSEN;
  }
}