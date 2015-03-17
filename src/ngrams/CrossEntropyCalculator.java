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
    
    /*
     * The frequencyDictionary map contains all the words that were created
     * from the sentences of your choise. Every word is added only once but
     * the value of that word represents the frequency of that word in the 
     * selected part of the corpus that the entropy is about to be calculated. 
     */

    public double calculateEntropy() {
        int sumUpTokens = 0;
        for (Map.Entry<String, Integer> map : frequencyDictionary.entrySet()) {
            sumUpTokens += map.getValue();
        }
        return -(logarithmicProbability / sumUpTokens);
    }
}
