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
        int numOfGrams = 3;
        Tokenizer tokenizer = new Tokenizer(numOfGrams, europarl, 5);
        Tokenizer testTokenizer = new Tokenizer(numOfGrams, europarlTest, 5);
        tokenizer.startTokenization();
        testTokenizer.startTokenization();
        ProbabilitiesCalculator p = new ProbabilitiesCalculator(testTokenizer.getCreatedNgrams(),
                tokenizer.getNgramsFrequency(),
                tokenizer.getGramsMinusOne(),
                tokenizer.getFrequencyDictionary(),
                testTokenizer.getListOfTestSentences(),
                numOfGrams);
        p.calculateChainProbability();
        p.printProbabilities();
        p.printCounters();
    }
}
