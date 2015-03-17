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

    public static void main(String[] args) throws IOException {
        File europarl = new File("trainEuroParlEl.txt");
        File europarlTest = new File("testEuroParlEl.txt");
        int numOfGrams = 2;
        int k = 3;
        Tokenizer tokenizer = new Tokenizer(numOfGrams, europarl, k, false);
        Tokenizer testTokenizer = new Tokenizer(numOfGrams, europarlTest, k, true);
        tokenizer.startTokenization();
        testTokenizer.startTokenization();
//        testTokenizer.printTagedList();
        ProbabilitiesCalculator p = new ProbabilitiesCalculator(testTokenizer.getCreatedNgrams(),
                tokenizer.getNgramsFrequency(),
                tokenizer.getGramsMinusOne(),
                tokenizer.getFrequencyDictionary(),
                testTokenizer.getListOfTestSentences(),
                numOfGrams);
        p.calculateChainProbability();
        p.printOnlyProbabilities();
        p.printCounters();
//        p.printTheUniverse();
        double logarithmicProbability = (double) p.getProbabilityList().get(0);
        CrossEntropyCalculator entropy = new CrossEntropyCalculator(testTokenizer.getFrequencyDictionary(), logarithmicProbability);
        System.out.println("The calculated entropy is: " + entropy.calculateEntropy());
    }
}
