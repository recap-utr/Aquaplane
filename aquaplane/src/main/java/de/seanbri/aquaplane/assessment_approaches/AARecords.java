package de.seanbri.aquaplane.assessment_approaches;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AARecords {
    private Map<AssessmentApproach, Record> records;


    public AARecords() {
        records = new HashMap<>();
    }

    public Record put(AssessmentApproach assessmentApproach, Record record) {
        return records.put(assessmentApproach, record);
    }

    public Record get(AssessmentApproach assessmentApproach) {
        return records.get(assessmentApproach);
    }

    public Record getByName(String name) {
        for (AssessmentApproach assApp : records.keySet()) {
            if (assApp.getName().equals(name)) {
                return records.get(assApp);
            }
        }
        return null;
    }

    public Set<AssessmentApproach> keySet() {
        return records.keySet();
    }
}
