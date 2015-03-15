/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ngrams;

import java.math.BigDecimal;
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
    private String token;
    private List<String> sentences;
    private float c;
    private float countMinusOneGrams;

    {
        sentences = new ArrayList<>();
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
        System.out.println(countMinusOneGrams);
        double answer = ((1 + c) / (countMinusOneGrams + (dictionary.size() - 1)));
        return answer;
    }

    public void calculateChainProbability() {
        for (String t : testNgrams) {
            System.out.println(calculateProbabilitiesForNgram(t) + " " + t);
//            calculateProbabilitiesForNgram(t);
//            if(t.startsWith("<s1>")){
//                 token = new String(t);
//                 sentences.add(token);
//            }else{
//                token = token + " " + t;
//            }
//        }
//        for(String t : sentences){
//            System.out.println(t);
        }
    }
}
