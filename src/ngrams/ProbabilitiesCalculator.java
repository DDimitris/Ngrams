/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ngrams;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Dimitris Dedousis <dimitris.dedousis@gmail.com>
 */
public class ProbabilitiesCalculator {

    private List<String> testNgrams;
    private Map<String, Integer> corpusNgrams;
    private Map<String, Integer> gramsMinusOne;
    private Map<String, Integer> dictionary;
    private List<Double> sentencesProbability;
    private double c;
    private double countMinusOneGrams;
    private List<String> testSentences;
    private int numOfGrams;
    {
        testSentences = new ArrayList<>();
        sentencesProbability = new ArrayList<>();
        dictionary = new HashMap<>();
        gramsMinusOne = new HashMap<>();
        testNgrams = new ArrayList<>();
        corpusNgrams = new HashMap<>();
    }

    public ProbabilitiesCalculator(List<String> testNgrams,
            Map<String, Integer> corpusNgrams,
            Map<String, Integer> gramsMinusOne,
            Map<String, Integer> dictionary,
            List<String> testSentences,
            int numOfGrams) {
        this.testNgrams = testNgrams;
        this.corpusNgrams = corpusNgrams;
        this.gramsMinusOne = gramsMinusOne;
        this.dictionary = dictionary;
        this.testSentences = testSentences;
        this.numOfGrams = numOfGrams;
    }

    public double calculateProbabilitiesForNgram(String testNgram) {
        c = 0;
        countMinusOneGrams = 0;
        if (corpusNgrams.containsKey(testNgram)) {
            c = corpusNgrams.get(testNgram);
        }
        String[] split = testNgram.split(" ");
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < split.length - 1; i++) {
            builder.append(split[i]);
            builder.append(" ");
        }
        if (gramsMinusOne.containsKey(builder.toString())) {
            countMinusOneGrams = gramsMinusOne.get(builder.toString());
        }
        double answer = ((1 + c) / (countMinusOneGrams + (dictionary.size() - (numOfGrams - 1))));
        return answer;
    }

    public void calculateChainProbability() {
        double chainProbability = 0;
        boolean isFirst = true;
        for (String t : testNgrams) {
            if (isFirst) {
                chainProbability += Math.log(calculateProbabilitiesForNgram(t)) / Math.log(2);
                isFirst = false;
                continue;
            }
            if (t.startsWith("<s1>")) {
                sentencesProbability.add(chainProbability);
                chainProbability = 0;
            }
            chainProbability += Math.log(calculateProbabilitiesForNgram(t)) / Math.log(2);
        }
        sentencesProbability.add(chainProbability);
    }

    public void printProbabilities() {
        for (int i = 0; i <= testSentences.size() - 1; i++) {
            System.out.println("Log prob for \"" + testSentences.get(i) + "\" : " + sentencesProbability.get(i));
        }
    }

    public void printCounters() {
        System.out.println("Test Ngrams size " + testNgrams.size());
        System.out.println("Corpus Ngrams size " + corpusNgrams.size());
        System.out.println("Grams minus one size " + gramsMinusOne.size());
        System.out.println("Dictionary size " + (dictionary.size() - (numOfGrams - 1)));
        System.out.println("Probability list size " + sentencesProbability.size());
        System.out.println("Sentences list size " + testSentences.size());
    }

    public List<Double> getProbabilityList() {
        return sentencesProbability;
    }
}
