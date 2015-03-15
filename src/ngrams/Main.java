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
        Tokenizer tokenizer = new Tokenizer(3, europarl, 5);
        tokenizer.startTokenization();
//        tokenizer.printNgrams();
        List<String> l = new ArrayList<>();
//        l.add("<s1> <s2> Geia");
//        l.add("<s2> Geia sou");
//        l.add("Geia sou ti");
        l.add("Although as you");
        l.add("as you will");
        l.add("you will have");
        tokenizer.printGramMinusOne();
        ProbabilitiesCalculator p = new ProbabilitiesCalculator(l, tokenizer.getNgramsFrequency(), tokenizer.getFrequencyDictionary(), 3);
    }
}
