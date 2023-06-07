package de.seanbri.aquaplane.assessment_approaches.fact_checking;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.seanbri.aquaplane.argumentQuality.comparison.Comparison;
import de.seanbri.aquaplane.assessment_approaches.AssessmentApproach;
import de.seanbri.aquaplane.utility.DecisionMaking;
import de.seanbri.aquaplane.utility.JsonUtility;
import de.seanbri.aquaplane.utility.PythonAPI;
import de.seanbri.aquaplane.utility.TextUtility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FactChecking implements AssessmentApproach {

    public static final String FACTCHECKSCORE = "factCheckScore";

    private static final String ENDPOINT = "http://127.0.0.1:8000/api/v1/fact_checking";

    private static final String FACTCHECKING = "FactChecking";

    private static final String TOPIC = "topic";
    private static final String CLAIM = "claim";
    private static final String CHECKWORTHINESS = "checkWorthiness";
    private static final String JUSTIFICATION = "justification";
    private static final String LABEL = "label";

    private static final String MATCHEDCLAIM = "matchedClaim";
    private static final String SEMANTICSIMILARITY = "semantic_similarity";
    private static final String MAPPEDRATING = "mapped_rating";
    private static final String ISNEGATION = "is_negation";
    private static final String CLAIMREVIEW = "claimReview";

    private static final String TEXTUALRATING = "textualRating";
    private static final String PUBLISHER = "publisher";

    private static final String RATHERTRUE = "rather_true";
    private static final String RATHERFALSE = "rather_false";
    private static final String UNCERTAIN = "uncertain";
    private static final String OTHER = "other";


    public record FactCheckingRecord(
            List<FactCheck> factChecks,
            int factCheckScore
    ) {}

    public record FactCheck(
            @JsonProperty(TOPIC) String topic,
            @JsonProperty(CLAIM) String claim,
            @JsonProperty(CHECKWORTHINESS) double checkWorthiness,
            @JsonProperty(JUSTIFICATION) Justification justification,
            @JsonProperty(LABEL) String label
    ) {}

    public record Justification(
            @JsonProperty(MATCHEDCLAIM) String matchedClaim,
            @JsonProperty(SEMANTICSIMILARITY) double semanticSimilarity,
            @JsonProperty(MAPPEDRATING) String mappedRating,
            @JsonProperty(ISNEGATION) String isNegation,
            @JsonProperty(CLAIMREVIEW) ClaimReview claimReview
    ) {}

    public record ClaimReview(
            @JsonProperty(TEXTUALRATING) String textualRating,
            @JsonProperty(PUBLISHER) String publisher
    ) {}


    @Override
    public String getName() {
        return FACTCHECKING;
    }

    @Override
    public FactCheckingRecord computeRecord(String argument, String claim) {
        argument = TextUtility.removeURLs(argument);

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode jsonObject = objectMapper.createObjectNode()
                .put("argument", argument)
                .put("topic", claim);

        String json = PythonAPI.post(ENDPOINT, jsonObject);

        List<FactCheck> factChecks = new ArrayList<>();
        ArrayNode arrayNode = JsonUtility.getObjectFrom(json, ArrayNode.class);

        for (JsonNode jsonNode : arrayNode) {
            FactCheck factCheck = JsonUtility.convertObjectToType(jsonNode, FactCheck.class);
            factChecks.add(factCheck);
        }

        int factCheckScore = computeFactCheckScore(factChecks);

        return new FactCheckingRecord(factChecks, factCheckScore);
    }

    private int computeFactCheckScore(List<FactCheck> factChecks) {
        Map<String, Integer> label_contribution = new HashMap<>();
        label_contribution.put(RATHERTRUE, 1);
        label_contribution.put(RATHERFALSE, -1);
        label_contribution.put(UNCERTAIN, 0);
        label_contribution.put(OTHER, 0);

        int score = 0;
        for (FactCheck factCheck : factChecks) {
            String label = factCheck.label;
            score += label_contribution.get(label);
        }

        return score;
    }

    @Override
    public Comparison compare(Record r1, Record r2) {
        Map<String, Integer> feature_decision = new HashMap<>();

        FactCheckingRecord fr1 = (FactCheckingRecord) r1;
        FactCheckingRecord fr2 = (FactCheckingRecord) r2;

        int decision = DecisionMaking.decisionHigh(
                fr1.factCheckScore(),
                fr2.factCheckScore()
        );
        feature_decision.put(
                FACTCHECKSCORE,
                decision
        );

        return new Comparison(fr1, fr2, decision, feature_decision);
    }

    @Override
    public List<String> explain(Comparison comparison) {
        List<String> explanations = new ArrayList<>();
        if (comparison.getDecision() == 0) {        // Only generate explanations if decision is unequivocal
            return explanations;
        }

        String template = "Argument %d contains more true facts or less false facts.";
        String explanation = String.format(
                template,
                comparison.getDecision()
        );
        explanations.add(explanation);

        FactCheckingRecord factCheckingRecord = (FactCheckingRecord) comparison.getObjectOfDecision();
        for(FactChecking.FactCheck factCheck : factCheckingRecord.factChecks()) {
            template = "The claim '%s' of argument %d was matched with the claim '%s' of the Google FactCheck API, ";
            explanation = String.format(template,
                    factCheck.claim(),
                    comparison.getDecision(),
                    factCheck.justification().matchedClaim()
            );

            if (factCheck.justification().isNegation().equals("true")) {
                explanation += "which is a negation ";
            } else {
                explanation += "which isn't a negation ";
            }

            explanation += String.format("and has been marked as '%s'. ",
                    factCheck.justification().mappedRating().replace("_", " ")
            );

            String conclusion = "Therefore the claim was classified as %s.";
            explanation += String.format(conclusion,
                    factCheck.label().replace("_", " ")
            );

            explanations.add(explanation);
        }

        return explanations;
    }

}
