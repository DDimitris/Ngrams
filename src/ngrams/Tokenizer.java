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
import java.util.Collections;
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
    private Map<String, Integer> frequencyDictionary;
    private Set<String> dictionary;
    private List<String> corpusWithTags;
    private Map<String, Integer> ngramsFrequencyMap;
    private Map<String, Integer> gramMinusOne;
    private List<String> sentencesFromTest;

    {
        sentencesFromTest = new ArrayList<>();
        gramMinusOne = new HashMap<>();
        ngramsFrequencyMap = new HashMap<>();
        dictionary = new HashSet<>();
        corpusWithTags = new ArrayList<>();
        ngramsList = new ArrayList<>();
        frequencyDictionary = new HashMap<>();
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
        String replace13 = replace2.replaceAll("\\s+", " ");
        String replace3 = replace13.replaceAll("-", "");
        String replace8 = replace3.replace(",", "");
        String replace12 = replace8.replace(":", "");
        String replace10 = replace12.replaceAll("κ.", "κ");
        String replace11 = replace10.replaceAll("εκατ.", "εκατ");
        String replace14 = replace11.replaceAll("εκτ.", "εκτ");
        String[] split1 = replace14.split(" ");
        StringBuilder builder = generateStartTags(split1);
        String tagedCorpus = builder.toString();
        String replace4 = tagedCorpus.replace(".", "");
        String replace5 = replace4.replace(",", "");
        String replace6 = replace5.replace("'", "");
        String replace7 = replace6.replace("\"", "");
        String replace9 = replace7.replaceAll("  *", " ").toLowerCase();
        String[] split = replace9.split(" ");
        createDictionary(split);
        for (int i = numberOfNgrams - 1; i < split.length; i++) {
            int ngramFrequency = 1;
            String token;
            int j = i; //j is used for iterating the n - 1 words from the last to the first.
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
            if (split[ i + 1].equals("<s1>")) {
                i = i + numberOfNgrams - 1;
            }
            ngramsList.add(token);
            if (ngramsFrequencyMap.containsKey(token)) {
                Integer frequency = ngramsFrequencyMap.get(token);
                ngramsFrequencyMap.put(token, frequency + ngramFrequency);
            } else {
                ngramsFrequencyMap.put(token, ngramFrequency);
            }
            String[] split2 = token.split(" ");
            token = "";
            for (int denominator = 0; denominator < numberOfNgrams - 1; denominator++) {
                token = split2[denominator] + " " + token;
            }
            if (gramMinusOne.containsKey(token)) {
                Integer frequency = gramMinusOne.get(token);
                gramMinusOne.put(token, frequency + ngramFrequency);
            } else {
                gramMinusOne.put(token, ngramFrequency);
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
            if (frequencyDictionary.containsKey(word)) {
                wordFrequency = frequencyDictionary.get(word) + 1;
            }
            frequencyDictionary.put(word, wordFrequency);
        }
        int countTheUnknowns = 0;
        for (Map.Entry<String, Integer> map : frequencyDictionary.entrySet()) {
            if (map.getValue() > wordFrequency) {
                dictionary.add(map.getKey());
            } else {
                dictionary.add("*unknown*");
                countTheUnknowns++;
            }
        }
        frequencyDictionary.put("*unknown*", countTheUnknowns);
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
            corpusWithTags.add("<s" + (i + 1) + ">");
        }
        String sentence = "";
        for (String token : file) {
            if (token.equals("")) {
                continue;
            }
            corpusWithTags.add(token);
            sentence = sentence + " " + token;
            if ((token.endsWith(".") || token.endsWith("!") || token.endsWith("?") || token.endsWith(";")) && token.length() > 2) {
                for (int i = 0; i < numberOfNgrams - 1; i++) {
                    corpusWithTags.add("<s" + (i + 1) + ">");
                    sentencesFromTest.add(sentence);
                    sentence = "";
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
    public void printTagedList() {
        for (String word : corpusWithTags) {
            System.out.println(word);
        }
    }

    /**
     * This method prints all the words in the dictionary with their frequency.
     */
    public void printTmpDictionary() {
        for (Map.Entry<String, Integer> d : frequencyDictionary.entrySet()) {
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

    public void printNgrams() {
        for (String s : ngramsList) {
            System.out.println(s);
        }
    }

    public void printNgramFrequency() {
        for (Map.Entry<String, Integer> map : ngramsFrequencyMap.entrySet()) {
            System.out.println("Ngram <<< " + map.getKey() + " >>> has frequency " + map.getValue());
        }
    }

    public Set<String> getDictionary() {
        return dictionary;
    }

    public Map<String, Integer> getFrequencyDictionary() {
        return frequencyDictionary;
    }

    public void printGramMinusOne() {
        for (Map.Entry<String, Integer> map : gramMinusOne.entrySet()) {
            System.out.println("Gram " + numberOfNgrams + " - 1 is <<< " + map.getKey() + " >>> with frequeny " + map.getValue());
        }
    }

    public Map<String, Integer> getGramsMinusOne() {
        return gramMinusOne;
    }

    public List<String> getListOfTestSentences() {
        sentencesFromTest.removeAll(Collections.singleton(""));
        return sentencesFromTest;
    }

    public void printSentences() {
        for (String g : getListOfTestSentences()) {
            System.out.println(g);
        }
    }
}
