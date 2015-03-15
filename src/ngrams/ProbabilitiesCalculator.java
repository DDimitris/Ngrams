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
    private int numberOfNgrams;
    private List<Double> sentencesProbability;
    private float c;
    private float countMinusOneGrams;

    {
        sentencesProbability = new ArrayList<>();
        dictionary = new HashMap<>();
        gramsMinusOne = new HashMap<>();
        testNgrams = new ArrayList<>();
        corpusNgrams = new HashMap<>();
    }

    public ProbabilitiesCalculator(List<String> testNgrams,
            Map<String, Integer> corpusNgrams,
            Map<String, Integer> frequencyDictionary, int numberOfNgrams,
            Map<String, Integer> dictionary) {
        this.testNgrams = testNgrams;
        this.corpusNgrams = corpusNgrams;
        this.gramsMinusOne = frequencyDictionary;
        this.numberOfNgrams = numberOfNgrams;
        this.dictionary = dictionary;
        calculateChainProbability();
    }

    public double calculateProbabilitiesForNgram(String testNgram) {
        c = 0f;
        countMinusOneGrams = 0f;
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
        double answer = ((1 + c) / (countMinusOneGrams + (dictionary.size() - 1)));
        return answer;
    }

    public void calculateChainProbability() {
        double chainProbability = 0;
        boolean isFirst = true;
//        String sentence = "";
        for(String t : testNgrams){
            if(isFirst){
            chainProbability += Math.log(calculateProbabilitiesForNgram(t)) / Math.log(2);
//            sentence += t;
            isFirst = false;
            continue;
            }
            if(t.startsWith("<s1>")){
                sentencesProbability.add(chainProbability);
                chainProbability = 0;
//                System.out.println(sentence);
//                sentence = "";
            }
//            sentence = sentence + " " + t;
            chainProbability += Math.log(calculateProbabilitiesForNgram(t)) / Math.log(2);
        }
        for(Double d : sentencesProbability){
            System.out.println(d);
        }
    }
}
