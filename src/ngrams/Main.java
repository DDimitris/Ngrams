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
        File europarl = new File("europarl.txt");
        Tokenizer tokenizer = new Tokenizer(2, europarl);
        tokenizer.startTokenization();
        for(String s : tokenizer.getCreatedNgrams()){
            System.out.println(s);
        }
    }
}
