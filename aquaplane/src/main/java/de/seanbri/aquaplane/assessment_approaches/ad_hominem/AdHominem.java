package de.seanbri.aquaplane.assessment_approaches.ad_hominem;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.seanbri.aquaplane.assessment_approaches.AssessmentApproach;
import de.seanbri.aquaplane.argumentQuality.comparison.Comparison;
import de.seanbri.aquaplane.utility.DecisionMaking;
import de.seanbri.aquaplane.utility.JsonUtility;
import de.seanbri.aquaplane.utility.PythonAPI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AdHominem implements AssessmentApproach {

    private static final String ENDPOINT = "http://127.0.0.1:8000/api/v1/ad_hominem";

    private static final String ADHOMINEM = "AdHominem";
    private static final String ISADHOMINEM = "is_ad_hominem";


    public record AdHominemRecord(
            @JsonProperty(ISADHOMINEM) double isAdHominem
    ) {}


    @Override
    public String getName() {
        return ADHOMINEM;
    }

    @Override
    public AdHominemRecord computeRecord(String argument, String claim) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode jsonObject = objectMapper.createObjectNode()
                .put("text", argument);

        String json = PythonAPI.post(ENDPOINT, jsonObject);

        return JsonUtility.getObjectFrom(json, AdHominemRecord.class);
    }

    @Override
    public Comparison compare(Record r1, Record r2) {
        Map<String, Integer> feature_decision = new HashMap<>();

        AdHominemRecord ar1 = (AdHominemRecord) r1;
        AdHominemRecord ar2 = (AdHominemRecord) r2;

        int decision = DecisionMaking.decisionLow(
                ar1.isAdHominem,
                ar2.isAdHominem
        );
        feature_decision.put(ISADHOMINEM, decision);

        return new Comparison(ar1, ar2, decision, feature_decision);
    }

    @Override
    public List<String> explain(Comparison comparison) {
        List<String> explanations = new ArrayList<>();
        if (comparison.getDecision() == 0) {        // Only generate explanations if decision is unequivocal
            return explanations;
        }

        String template =
                "Argument %d is an ad hominem argument and is thus attacking or abusive towards a person.\n" +
                "This was detected with the ALBERT Ad Hominem Classifier.";
        String explanation = String.format(template,
                comparison.getInvertedDecision(),
                comparison.getInvertedDecision()
        );
        explanations.add(explanation);

        return explanations;
    }

}
