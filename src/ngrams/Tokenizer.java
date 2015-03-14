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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
    private Map<String, Integer> tmpDictionary;
    private Set<String> dictionary;
    private List<String> corpusWithTags;
    private Map<String, Integer> ngramsFrequencyMap;

    {
        ngramsFrequencyMap = new HashMap<>();
        dictionary = new HashSet<>();
        corpusWithTags = new ArrayList<>();
        ngramsList = new ArrayList<>();
        tmpDictionary = new HashMap<>();
    }

    /**
     * Number of grams is used to create the associated grams. For example if
     * you use the number (3) you create a trigram or if you use the number (2)
     * you create a bigram etc. File to be read is the training file but also it
     * is used to extract words for the dictionary. Word frequency is used for
     * the dictionary creation. No word is accepted in the dictionary if it has
     * frequency lower than the given frequency, in the corpus.
     *
     * @param numberOfNgrams
     * @param fileToBeRead
     * @param wordFrequency
     */
    public Tokenizer(int numberOfNgrams, File fileToBeRead, int wordFrequency) {
        this.numberOfNgrams = numberOfNgrams;
        this.fileToBeRead = fileToBeRead;
        this.wordFrequency = wordFrequency;
    }

    /**
     * I have no idea why this method is working! yet!
     *
     * @throws IOException
     */
    public void startTokenization() throws IOException {
        europarlString = new String(Files.readAllBytes(fileToBeRead.toPath()));
        String replace = europarlString.replace("\n", " ");
        String replace1 = replace.replace("\r", " ");
        String replace2 = replace1.replace("\n\r", " ");
        String replace3 = replace2.replaceAll("-", "");
        String replace8 = replace3.replace(",", "");
        String[] split1 = replace8.split(" ");
        StringBuilder builder = generateStartTags(split1);
        String tagedCorpus = builder.toString();
        String replace4 = tagedCorpus.replace(".", "");
        String replace5 = replace4.replace(",", "");
        String replace6 = replace5.replace("'", "");
        String replace7 = replace6.replace("\"", "");
        String replace9 = replace7.replaceAll("  *", " ");
        String[] split = replace9.split(" ");
        createDictionary(split);
//        printDictionary();
//        printTagedList();
        for (int i = numberOfNgrams - 1; i < split.length; i++) {
            int ngramFrequency = 1;
            String token;
            int j = i;
            if (dictionary.contains(split[i])) {
                token = (String) split[i];
            } else {
                token = "*unknown*";
            }
            int k = j - numberOfNgrams + 1;
            do {
                j--;
                if (dictionary.contains(split[j])) {
                    token = split[j] + " " + token;
                } else {
                    token = "*unknown*" + " " + token;
                }
            } while (j - 1 != k - 1);
            if (split[ i + 1].equals("<s>")) {
                i = i + numberOfNgrams - 1;
            }
            ngramsList.add(token);
            if (ngramsFrequencyMap.containsKey(token)) {
                Integer frequency = ngramsFrequencyMap.get(token);
                ngramsFrequencyMap.put(token, frequency + ngramFrequency);
            } else {
                ngramsFrequencyMap.put(token, ngramFrequency);
            }
        }
    }

    /**
     * This method creates a dictionary of words from the corpus and stores the
     * frequency of every word in the corpus.
     *
     * @param splitedFile
     */
    private void createDictionary(String[] splitedFile) {
        for (String word : splitedFile) {
            int wordFrequency = 1;
            if (tmpDictionary.containsKey(word)) {
                wordFrequency = tmpDictionary.get(word) + 1;
            }
            tmpDictionary.put(word, wordFrequency);
        }
        for (Map.Entry<String, Integer> map : tmpDictionary.entrySet()) {
            if (map.getValue() > wordFrequency) {
                dictionary.add(map.getKey());
            } else {
                dictionary.add("*unknown*");
            }
        }

    }

    /**
     * This method creates the <s> tags after the end of every sentence. The
     * number of tags that is added to the start of every sentence is n-1 were n
     * is the number of the grams that are to be created.
     *
     * @param file
     * @return
     */
    private StringBuilder generateStartTags(String[] file) {
        for (int i = 0; i < numberOfNgrams - 1; i++) {
            corpusWithTags.add("<s>");
        }
        for (String token : file) {
            corpusWithTags.add(token);
            if (token.endsWith(".") || token.endsWith("!") || token.endsWith("?")) {
                for (int i = 0; i < numberOfNgrams - 1; i++) {
                    corpusWithTags.add("<s>");
                }
            }
        }
        StringBuilder b = new StringBuilder();
        for (String s : corpusWithTags) {
            b.append(s);
            b.append(" ");
        }
        return b;
    }

    /**
     * This method prints all the tags that were added.
     */
    private void printTagedList() {
        for (String word : corpusWithTags) {
            System.out.println(word);
        }
    }

    /**
     * This method prints all the words in the dictionary with their frequency.
     */
    private void printDictionary() {
        for (Map.Entry<String, Integer> d : tmpDictionary.entrySet()) {
            System.out.println("Word " + d.getKey() + " has frequency " + d.getValue());
        }
    }

    /**
     * This method does the same thing as the method startTokenization but it
     * has the difference that it cant add the tag <s> at the beginning of every
     * new sentence.
     *
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

    public Map<String, Integer> getNgramsFrequency() {
        return ngramsFrequencyMap;
    }

    public void prinNgrams() {
        for (String s : ngramsList) {
            System.out.println(s);
        }
    }

    public void printNgramFrequency() {
        for (Map.Entry<String, Integer> map : ngramsFrequencyMap.entrySet()) {
            System.out.println("Ngram <<< " + map.getKey() + " >>> has frequency " + map.getValue());
        }
    }
}
