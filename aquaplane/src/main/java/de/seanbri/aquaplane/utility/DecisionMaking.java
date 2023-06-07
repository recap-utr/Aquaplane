package de.seanbri.aquaplane.utility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DecisionMaking {

    /**
	 * The Boyer-Moore Majority Voting Algorithm is used to find the majority element among the given elements.
     * A majority element occurs more than N/2 times among the given elements.
	 */
    public static int findMajority(List<Integer> nums) {
        ArrayList<Integer> arrNums = new ArrayList<Integer>(nums);
        arrNums.removeAll(Collections.singleton(0));       // remove all zeros (no decision possible)
        int candidate = findMajorityCandidate(arrNums);
        return checkMajorityCandidate(arrNums, candidate);
    }

    private static int findMajorityCandidate(ArrayList<Integer> nums) {
        int count = 0;
        int candidate = -1;

        for (Integer num : nums) {
            if (count == 0) {
                candidate = num;
            }

            count += (num == candidate ? 1 : -1);
        }

        return candidate;
    }

    /**
     * Check if majority candidate occurs more than N/2 times.
     */
    private static int checkMajorityCandidate(ArrayList<Integer> nums, int candidate) {
        int count = 0;

        for (Integer num : nums) {
            if (num == candidate) {
                count++;
            }
        }

        if (count > nums.size()/2)
            return candidate;

        return 0;
    }

    public static int decisionHigh(double score1, double score2) {
        return (score1 > score2) ? 1 : (score2 > score1) ? 2 : 0;
    }

    public static int decisionLow(double score1, double score2) {
        return (score1 < score2) ? 1 : (score2 < score1) ? 2 : 0;
    }

}
