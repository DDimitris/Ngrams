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
        File europarl = new File("trainEuroParlEN.txt");
        File europarlTest = new File("testEuroParlEN.txt");
        Tokenizer tokenizer = new Tokenizer(3, europarl, 5);
        Tokenizer testTokenizer = new Tokenizer(3, europarlTest, 5);
        tokenizer.startTokenization();
        testTokenizer.startTokenization();
        ProbabilitiesCalculator p = new ProbabilitiesCalculator(testTokenizer.getCreatedNgrams(),
                tokenizer.getNgramsFrequency(),
                tokenizer.getGramsMinusOne(),
                tokenizer.getFrequencyDictionary(),
                testTokenizer.getListOfTestSentences());
        p.calculateChainProbability();
        p.printProbabilities();
        p.printCounters();
    }
}
