package edu.caltech.cs2.project01;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class SubstitutionCipher {
    private String ciphertext;
    private Map<Character, Character> key;

    // Use this Random object to generate random numbers in your code,
    // but do not modify this line.
    private static final Random RANDOM = new Random();

    /**
     * Construct a SubstitutionCipher with the given cipher text and key
     *
     * @param ciphertext the cipher text for this substitution cipher
     * @param key        the map from cipher text characters to plaintext characters
     */
    public SubstitutionCipher(String ciphertext, Map<Character, Character> key) {
        this.ciphertext = ciphertext;
        this.key = key;
    }

    /**
     * Construct a SubstitutionCipher with the given cipher text and a randomly
     * initialized key.
     *
     * @param ciphertext the cipher text for this substitution cipher
     */
    public SubstitutionCipher(String ciphertext) {
        this.ciphertext = ciphertext;
        char[] alphabet = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
        Map<Character, Character> k = new HashMap<>();
        for (char c : alphabet) {
            k.put(c, c);
        }
        this.key = k;
        for (int i = 0; i < 10000; i++) {
            this.key = randomSwap().key;
        }
    }

    /**
     * Returns the unedited cipher text that was provided by the user.
     *
     * @return the cipher text for this substitution cipher
     */
    public String getCipherText() {
        return this.ciphertext;
    }

    /**
     * Applies this cipher's key onto this cipher's text.
     * That is, each letter should be replaced with whichever
     * letter it maps to in this cipher's key.
     *
     * @return the resulting plain text after the transformation using the key
     */
    public String getPlainText() {
        String plainText = "";
        for (int i = 0; i < this.ciphertext.length(); i++) {
            plainText += this.key.get(this.ciphertext.charAt(i));
        }
        return plainText;
    }

    /**
     * Returns a new SubstitutionCipher with the same cipher text as this one
     * and a modified key with exactly one random pair of characters exchanged.
     *
     * @return the new SubstitutionCipher
     */
    public SubstitutionCipher randomSwap() {
        int i1 = RANDOM.nextInt(26) + 65;
        int i2 = RANDOM.nextInt(26) + 65;
        if (i1 == i2) {
            while (i1 == i2) {
                i2 = RANDOM.nextInt(26) + 65;
            }
        }
        char c1 = (char) i1;
        char c2 = (char) i2;
        char v1 = this.key.get(c1);
        char v2 = this.key.get(c2);
        Map<Character, Character> cipher = new HashMap<>();
        for (char k : this.key.keySet()) {
            cipher.put(k, this.key.get(k));
        }
        cipher.put(c1, v2);
        cipher.put(c2, v1);
        return new SubstitutionCipher(this.ciphertext, cipher);
    }

    /**
     * Returns the "score" for the "plain text" for this cipher.
     * The score for each individual quadgram is calculated by
     * the provided likelihoods object. The total score for the text is just
     * the sum of these scores.
     *
     * @param likelihoods the object used to find a score for a quadgram
     * @return the score of the plain text as calculated by likelihoods
     */
    public double getScore(QuadGramLikelihoods likelihoods) {
        double score = 0.0;
        String plain_text = this.getPlainText();
        if (plain_text.length() >= 4) {
            for (int i = 0; i < plain_text.length() - 3; i++) {
                score += likelihoods.get(plain_text.substring(i, i + 4));
            }
        }
        return score;
    }

    /**
     * Attempt to solve this substitution cipher through the hill
     * climbing algorithm. The SubstitutionCipher this is called from
     * should not be modified.
     *
     * @param likelihoods the object used to find a score for a quadgram
     * @return a SubstitutionCipher with the same ciphertext and the optimal
     * found through hill climbing
     */
    public SubstitutionCipher getSolution(QuadGramLikelihoods likelihoods) {
        SubstitutionCipher sol = new SubstitutionCipher(this.ciphertext);
        int trial = 0;
        while(trial < 1000) {
            trial++;
            Map<Character, Character> test_key = sol.randomSwap().key;
            SubstitutionCipher test_cipher = new SubstitutionCipher(this.ciphertext, test_key);
            if(test_cipher.getScore(likelihoods) > sol.getScore(likelihoods)) {
                sol.key = test_key;
                trial = 0;
            }
        }
        return sol;
    }

}
