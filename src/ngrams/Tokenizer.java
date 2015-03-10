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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private int wordFrequency;
    private Map<String, Integer> dictionary;
    {
        ngramsList = new ArrayList<>();
        dictionary = new HashMap<>();
    }
    /**
     * Number of grams is used to create the associated grams. For example 
     * if you use the number (3) you create a trigram or if you use the number (2)
     * you create a bigram etc. File to be read is the training file but also 
     * it is used to extract words for the dictionary. Word frequency is used 
     * for the dictionary creation. No word is accepted in the dictionary if it 
     * has frequency lower than the given frequency, in the corpus.
     * @param numberOfNgrams
     * @param fileToBeRead
     * @param wordFrequency 
     */
    public Tokenizer(int numberOfNgrams, File fileToBeRead, int wordFrequency) {
        this.numberOfNgrams = numberOfNgrams;
        this.fileToBeRead = fileToBeRead;
        this.wordFrequency = wordFrequency;
    }

    public void startTokenization() throws IOException {
        europarlString = new String(Files.readAllBytes(fileToBeRead.toPath()));
        String replace = europarlString.replace("\n", " ");
        String replace1 = replace.replace("\r", " ");
        String replace2 = replace1.replace("\n\r", " ");
        String replace3 = replace2.replaceAll("-", " ");
        String replace8 = replace3.replace(",", "");
        String[] split = replace8.split(" ");
        createDictionary(split);
        printDictionary();
        boolean isNewSentence = true;
        for (int i = numberOfNgrams - 1; i < split.length; i++) {
            String token = new String();
            int j = i;
            if(dictionary.containsKey(split[i]) && dictionary.get(split[i]) > wordFrequency){
            token = split[i];
            }else{
                token = "*unknown*";
            }
                int k = j - numberOfNgrams + 1;
            do  {
                j--;
                if(dictionary.containsKey(split[j]) && dictionary.get(split[j]) > wordFrequency){
                token = split[j] + " " + token;
                }else{
                token = "*unknown*" + " " + token;    
                }
            }while(j - 1 != k - 1);
            if(isNewSentence){
                token = "<s>" + token;
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
    
    private void createDictionary(String[] splitedFile){
        for(String word : splitedFile){
            int wordFrequency = 1;
            if(dictionary.containsKey(word)){
             wordFrequency = dictionary.get(word) + 1;
            }
            dictionary.put(word, wordFrequency);
        }
    }

    private void printDictionary(){
        for(Map.Entry<String, Integer> d : dictionary.entrySet()){
            System.out.println("Word " + d.getKey() + " has frequency " + d.getValue());
        }
    }
    
    /**
     * This method does the same thing as the method startTokenization but it
     * has the difference that it cant add the tag <s> at the beginning of every 
     * new sentence.
     * @throws IOException 
     */
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
