/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ngrams;

import java.io.File;
import java.io.IOException;

/**
 *
 * @author Dimitris Dedousis <dimitris.dedousis@gmail.com>
 */
public class Main {

    private static final int grams = 3;
    private static final int k = 3;
    private File trainSet;
    private File testSet;
    private Tokenizer trainTokenizer;
    private Tokenizer testTokenizer;
    private ProbabilitiesCalculator probabilities;
    private CrossEntropyCalculator crossEntropyCalculator;
    private double logarithmicProbability;
    private boolean calculateEntropy = false;

    public Main() throws IOException {
        trainSet = new File("trainEuroParlEl.txt");
        testSet = new File("testEuroParlEl.txt");
        trainTokenizer = new Tokenizer(grams, trainSet, k, false); //set it to false to turn of the entropy calculation
        testTokenizer = new Tokenizer(grams, testSet, k, calculateEntropy);
        run();
    }

    public static void main(String[] args) throws IOException {
        new Main();
    }

    private void run() throws IOException {
        trainTokenizer.startTokenization();
        testTokenizer.startTokenization();
        probabilities = new ProbabilitiesCalculator(testTokenizer.getCreatedNgrams(),
                trainTokenizer.getNgramsFrequency(),
                trainTokenizer.getGramsMinusOne(),
                trainTokenizer.getFrequencyDictionary(),
                testTokenizer.getListOfTestSentences(),
                grams);
        probabilities.calculateChainProbability();
        if (calculateEntropy) {
            probabilities.printOnlyProbabilities();
            logarithmicProbability = (double) probabilities.getProbabilityList().get(0);
            crossEntropyCalculator = new CrossEntropyCalculator(testTokenizer.getFrequencyDictionary(), logarithmicProbability);
            System.out.println("The calculated entropy for " + grams + "-gram model is: " + crossEntropyCalculator.calculateEntropy());
        } else {
            probabilities.printProbabilitiesWithSentences();
        }
        probabilities.printCounters();
    }
}
