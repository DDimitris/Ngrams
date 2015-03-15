/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ngrams;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Dimitris Dedousis <dimitris.dedousis@gmail.com>
 */
public class Main {

    public static void main(String[] args) throws IOException {
        File europarl = new File("europarl.txt");
        File europarlTest = new File("testEuroParl.txt");
        Tokenizer tokenizer = new Tokenizer(3, europarl, 5);
        Tokenizer testTokenizer = new Tokenizer(3, europarlTest, 5);
        tokenizer.startTokenization();
//        tokenizer.printGramMinusOne();
        testTokenizer.startTokenization();
//        tokenizer.printNgramFrequency();
//        tokenizer.printGramMinusOne();
        ProbabilitiesCalculator p = new ProbabilitiesCalculator(testTokenizer.getCreatedNgrams(), 
                tokenizer.getNgramsFrequency(), 
                tokenizer.getGramsMinusOne(), 
                3, tokenizer.getFrequencyDictionary());
    }
}
