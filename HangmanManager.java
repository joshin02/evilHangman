// Joshua Shin
// 2.02.2021
// CSE 143 Section BI
// TA: Ashley Liao
// The Hangman Manager class represents the structure of 
// the classic game Hangman with a twist. It keep track of
// letters guessed, guessed left, and potential answers. 

import java.util.*;

public class HangmanManager {
    private Set<String> options;
    private Set<Character> guessedLetters;
    private int left;
    private String reveal;

    // creates the game setting for evil Hangman; takes in all available words, desired 
    // word length for correct answer, and number of guesses allowed. Throws an
    // IllegalArgumentException if the word length is less than 1 or if guesses allowed
    // is less than 0. Using the available words and desired word length, creates a new
    // inventory of possible answers.
    public HangmanManager(Collection<String> dictionary, int length, int max) {
        if (length < 1 || max < 0) {
            throw new IllegalArgumentException();
        }
        options = new TreeSet<String>();
        for (String word : dictionary) {
            if (word.length() == length) {
                options.add(word);
            }
        }
        guessedLetters = new TreeSet<Character>();
        left = max;
        reveal = "-";
        for (int i = 1; i < length; i++) {
            reveal += " -";
        }
    }

    // returns the set of current possible answers 
    public Set<String> words() {
        return options;
    }

    // returns how many guesses user has left
    public int guessesLeft() {
        return left;
    }

    // returns an inventory of all guessed letters
    public Set<Character> guesses() {
        return guessedLetters;
    }

    // throws an IllegalStateException if inventory of possible answers is empty
    // returns current pattern for the game 
    public String pattern() {
        if (options.isEmpty()) {
            throw new IllegalStateException();
        }
        return reveal;
    }

    // If guesses left is less than zero or if inventory of possible answers is empty, 
    // IllegalStateException is thrown. If letter has already been guessed, 
    // IllegalArgumentException is thrown. Takes in letter guessed by user, adds to inventory
    // of guessed letters, finds pattern for all possible answers and adds them to the
    // appropriate word family. From there, a new group of possible answers is chosen.
    // Amount of times guessed letter occurs is returned. If letter doesn't occur in
    // possible answers, guesses left goes down by one. 
    public int record(char guess) {
        if (left < 1 || options.isEmpty()) {
            throw new IllegalStateException();
        } else if (!options.isEmpty() && guessedLetters.contains(guess)) {
            throw new IllegalArgumentException();
        }
        guessedLetters.add(guess);
        Map<String, Set<String>> family = new TreeMap<String, Set<String>>();
        categorize(guess, family); 
        select(family);
        return timesAppeared(guess);
    }

    // takes in given word and guessed letter and finds the pattern for the word
    // returns the pattern 
    private String getPattern(String word, char guess) {
        String pattern = "";
        int space = 0;
        for (int i = 0; i < word.length(); i++) {
            if (word.charAt(i) == guess) {
                if (i == word.length() - 1) {
                    pattern += guess;
                } else {
                    pattern += guess + " ";
                }
            } else {
                if (i == word.length() - 1) {
                    pattern += reveal.substring(space);
                } else {
                    pattern += reveal.substring(space, space + 2);
                }
            }
            space += 2;
        }
        return pattern;
    }

    // takes in guessed letter and storage to find pattern families
    // goes through all the possible answers and put them into groups
    // based on their letter patterns, returns the storage of pattern families
    private Map<String, Set<String>> categorize(char guess, Map<String, Set<String>> family) {
        for (String word : options) {
            String pattern = getPattern(word, guess);
            if (!family.containsKey(pattern)) {
                Set<String> group = new HashSet<String>();
                family.put(pattern, group);
            }
            family.get(pattern).add(word);
        }
        return family;
    }

    // takes in storage of pattern familes, checks to see which
    // pattern has larget set of options/answers
    //returns the new possible set of answers
    private Set<String> select(Map<String, Set<String>> family) {
        int biggest = 0;
        for (String pattern : family.keySet()) {
            Set<String> group = family.get(pattern);
            if (group.size() > biggest) {
                biggest = group.size();
                reveal = pattern;
                options = group;
            }
        }
        return options;
    }

    // takes in guessed letter and checks to see how many times it appears
    // in the answer. If it doesn't appear at all, guesses left decreases by 1
    // returns how many times guessed letter appears
    private int timesAppeared(char guess) {
        int occurences = 0;
        for (int i = 0; i < reveal.length(); i++) {
            if (reveal.charAt(i) == guess) {
                occurences++;
            }
        }
        if (occurences == 0) {
            left--;
        }
        return occurences;
    }
}
