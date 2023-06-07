package de.seanbri.aquaplane.assessment_approaches;

import de.seanbri.aquaplane.argumentQuality.comparison.Comparison;

import java.util.List;

public interface AssessmentApproach {
    String getName();
    Record computeRecord(String argument, String claim);
    Comparison compare(Record r1, Record r2);
    List<String> explain(Comparison comparison);
}
