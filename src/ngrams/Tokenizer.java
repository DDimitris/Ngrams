/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ngrams;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.shingle.ShingleFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

/**
 *
 * @author Dimitris Dedousis <dimitris.dedousis@gmail.com>
 */
public class Tokenizer {

    private int numberOfNgrams;
    private File fileToBeRead;
    private List<String> ngramsList;
    private String europarlString;

    {
        ngramsList = new ArrayList<>();
    }

    public Tokenizer(int numberOfNgrams, File fileToBeRead) {
        this.numberOfNgrams = numberOfNgrams;
        this.fileToBeRead = fileToBeRead;
    }

    public void startTokenization() throws IOException {
        europarlString = new String(Files.readAllBytes(fileToBeRead.toPath()));
        String replace = europarlString.trim().replaceAll("\\s+", " ");
        String replace2 = replace.replace("-", " ");
        String[] split = replace2.split(" ");
        boolean isNewSentence = true;
        for (int i = numberOfNgrams - 1; i < split.length; i++) {
            String token = new String();
            int j = i;
            token = split[i];
                int k = j - numberOfNgrams + 1;
            do  {
                j--;
                token = split[j] + " " + token;
            }while(j - 1 != k - 1);
            if(isNewSentence){
                token = "<s> " + token;
                isNewSentence = false;
            }
            if(token.endsWith(".") || token.endsWith("!") || token.endsWith("?")){
                isNewSentence = true;
                i = i + numberOfNgrams - 1;
            }
            String replace4 = token.replace(".", "");
            String replace5 = replace4.replace(",", "");
            String replace6 = replace5.replace("'", "");
            String replace7 = replace6.replace("\"", "");
            ngramsList.add(replace7);
        }
    }

    public void startTokenizationWithLucene() throws IOException {
        europarlString = new String(Files.readAllBytes(fileToBeRead.toPath()));
        StandardTokenizer tokenizer = new StandardTokenizer();
        Reader reader = new StringReader(europarlString);
        tokenizer.setReader(reader);
        TokenStream stream = tokenizer;
        stream = new ShingleFilter(stream, numberOfNgrams, numberOfNgrams);
        CharTermAttribute charTermAttribute = stream.addAttribute(CharTermAttribute.class);
        stream.reset();
        while (stream.incrementToken()) {
            String token = charTermAttribute.toString();
            String[] split = token.split(" ");
            if (split.length < 2) {
                continue;
            }
            ngramsList.add(token);
        }
    }

    public List<String> getCreatedNgrams() {
        return ngramsList;
    }
}
