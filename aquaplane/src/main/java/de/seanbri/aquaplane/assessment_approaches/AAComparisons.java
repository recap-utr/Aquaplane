package de.seanbri.aquaplane.assessment_approaches;

import de.seanbri.aquaplane.argumentQuality.comparison.Comparison;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AAComparisons {
    private Map<AssessmentApproach, Comparison> comparisons;


    public AAComparisons() {
        comparisons = new HashMap<>();
    }

    public Comparison put(AssessmentApproach assessmentApproach, Comparison comparison) {
        return comparisons.put(assessmentApproach, comparison);
    }

    public Comparison get(AssessmentApproach assessmentApproach) {
        return comparisons.get(assessmentApproach);
    }

    public Set<AssessmentApproach> keySet() {
        return comparisons.keySet();
    }
}
