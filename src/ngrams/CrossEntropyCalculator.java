/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ngrams;

import java.util.Map;

/**
 *
 * @author Dimitris Dedousis <dimitris.dedousis@gmail.com>
 */
public class CrossEntropyCalculator {
    private Map<String, Integer> frequencyDictionary;
    private double logarithmicProbability;

    public CrossEntropyCalculator(Map<String, Integer> frequencyDictionary, double probability) {
        this.frequencyDictionary = frequencyDictionary;
        this.logarithmicProbability = probability;
    }
    public double calculateEntropy(){
        int sumUpTokens = 0;
        for(Map.Entry<String, Integer> map : frequencyDictionary.entrySet()){
            sumUpTokens += map.getValue();
        }
        return - (logarithmicProbability / sumUpTokens);
    }
}
