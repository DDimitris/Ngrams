/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ngrams;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Dimitris Dedousis <dimitris.dedousis@gmail.com>
 */
public class ProbabilitiesCalculator {

    private List<String> testNgrams;
    private Map<String, Integer> corpusNgrams;
    private Map<String, Integer> frequencyDictionary;
    private int numberOfNgrams;

    {
        frequencyDictionary = new HashMap<>();
        testNgrams = new ArrayList<>();
        corpusNgrams = new HashMap<>();
    }

    public ProbabilitiesCalculator(List<String> testNgrams, Map<String, Integer> corpusNgrams, Map<String, Integer> frequencyDictionary, int numberOfNgrams) {
        this.testNgrams = testNgrams;
        this.corpusNgrams = corpusNgrams;
        this.frequencyDictionary = frequencyDictionary;
        this.numberOfNgrams = numberOfNgrams;
        for(String t : testNgrams){
            System.out.println(calculateProbabilitiesForNgram(t) + "  " + t);
        }
    }

    

    public double calculateProbabilitiesForNgram(String testNgram) {
        Integer countWantedGram = 0;
        Integer countGivenGram = 0;
        System.out.println(testNgram);
        if (corpusNgrams.containsKey(testNgram)) {
            countWantedGram = corpusNgrams.get(testNgram);
        }
        String[] split = testNgram.split(" ");
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < split.length - 1; i++) {
            builder.append(split[i]);
            builder.append(" ");
        }
        System.out.println(builder.toString());
        System.out.println(countWantedGram);
        if (corpusNgrams.containsKey(builder.toString())) {
            countGivenGram = corpusNgrams.get(builder.toString());
        } 
        return countWantedGram / countGivenGram;
    }
}
