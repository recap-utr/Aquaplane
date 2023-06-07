package de.seanbri.aquaplane.argumentQuality;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.seanbri.aquaplane.assessment_approaches.AAComparisons;
import de.seanbri.aquaplane.assessment_approaches.AARecords;
import de.seanbri.aquaplane.assessment_approaches.AssessmentApproach;
import de.seanbri.aquaplane.argumentQuality.comparison.Comparison;
import de.seanbri.aquaplane.argumentQuality.dimension.DimensionMapper;
import de.seanbri.aquaplane.argumentQuality.dimension.Dimension;
import de.seanbri.aquaplane.argumentQuality.explanation.Explanation;
import de.seanbri.aquaplane.model.ArgumentPair;
import de.seanbri.aquaplane.utility.DecisionMaking;
import de.seanbri.aquaplane.utility.JsonUtility;

import java.util.*;

import static de.seanbri.aquaplane.argumentQuality.dimension.Dimension.*;

public class ArgumentQualityUtility {

    public static final String CLAIM = "claim";
    public static final String A1 = "a1";
    public static final String A2 = "a2";
    public static final String DECISION = "decision";
    public static final String EXPLANATIONS = "explanations";
    public static final String EXPLANATION = "explanation";
    public static final String SUBDIMENSIONS = "subdimensions";
    public static final String APPROACHES = "approaches";
    public static final String ARGUMENTQUALITY = "argumentQuality";


    public static String compare(ArgumentPair argumentPair, AARecords records1, AARecords records2) {
        AAComparisons comparisons = createComparisons(records1, records2);
        Map<String, Integer> dim_decision = determineDecisions(comparisons);
        comparisons = addExplanationsFor(comparisons);
        Map<String, String> dim_explanation = generateExplanationsForDimensions(dim_decision);
        JsonNode jsonNode = buildJson(argumentPair, comparisons, dim_decision, dim_explanation);
        return JsonUtility.prettyPrint(jsonNode);
    }

    private static AAComparisons createComparisons(AARecords records1, AARecords records2) {
        AAComparisons comparisons = new AAComparisons();
        for (AssessmentApproach assApp : records1.keySet()) {
            Comparison comparison = assApp.compare(
                    records1.get(assApp),
                    records2.getByName(assApp.getName())
            );
            comparisons.put(assApp, comparison);
        }
        return comparisons;
    }

    private static Map<String, Integer> determineDecisions(AAComparisons comparisons) {
        Map<String, Integer> dim_decision = new HashMap<>();

        Map<String, Map<String, Set<String>>> dimensions = DimensionMapper.getMappings();
        ArrayList<Integer> dimensionDecisions = new ArrayList<>();

        for (String dimension : dimensions.keySet()) {
            ArrayList<Integer> subdimensionDecisions = new ArrayList<>();
            Map<String, Set<String>> subdimensions = dimensions.get(dimension);

            for (String subdimension : subdimensions.keySet()) {
                Set<String> approachesOfSubdimension = subdimensions.get(subdimension);
                ArrayList<Integer> decisions = collectAllDecisionsOf(approachesOfSubdimension, comparisons);
                int decisionOfSubdimension = DecisionMaking.findMajority(decisions);
                subdimensionDecisions.add(decisionOfSubdimension);
                dim_decision.put(subdimension, decisionOfSubdimension);
            }

            int decisionOfDimension = DecisionMaking.findMajority(subdimensionDecisions);
            dimensionDecisions.add(decisionOfDimension);
            dim_decision.put(dimension, decisionOfDimension);
        }

        int decisionOfAllDimensions = DecisionMaking.findMajority(dimensionDecisions);
        dim_decision.put(OVERALL, decisionOfAllDimensions);

        return dim_decision;
    }

    private static AAComparisons addExplanationsFor(AAComparisons comparisons) {
        for (AssessmentApproach assApp : comparisons.keySet()) {
            Comparison comparison = comparisons.get(assApp);
            List<String> explanations = assApp.explain(comparison);
            comparison.setExplanations(explanations);
            comparisons.put(assApp, comparison);
        }
        return comparisons;
    }

    private static Map<String, String> generateExplanationsForDimensions(
            Map<String, Integer> dim_decision
    ) {
        Map<String, String> dim_explanation = new HashMap<>();

        for(String dim : dim_decision.keySet()) {
            int decision = dim_decision.get(dim);
            String explanation;
            if (Dimension.getAllDimensions().contains(dim)) {
                explanation = Explanation.getExplanationForDimension(decision, dim);
            } else if (Dimension.getAllSubdimensions().contains(dim)) {
                explanation = Explanation.getExplanationForSubdimension(decision, dim);
            } else {
                explanation = Explanation.getOverallExplanation(decision);
            }
            dim_explanation.put(dim, explanation);
        }

        return dim_explanation;
    };

    private static JsonNode buildJson(
            ArgumentPair argumentPair,
            AAComparisons comparisons,
            Map<String, Integer> dim_decision,
            Map<String, String> dim_explanation
    ) {
        Map<String, Comparison> namesOfApproaches = namesOfApproaches(comparisons);

        JsonNodeFactory factory = JsonNodeFactory.instance;
        ObjectNode rootNode = factory.objectNode();
        rootNode.put(CLAIM, argumentPair.getClaim());
        rootNode.put(A1, argumentPair.getArg1());
        rootNode.put(A2, argumentPair.getArg2());

        Map<String, Map<String, Set<String>>> dimensions = DimensionMapper.getMappings();
        ObjectNode argumentQualityNode = factory.objectNode();
        for (String dimension : dimensions.keySet()) {
            ObjectNode dimensionNode = factory.objectNode();

            Map<String, Set<String>> subdimensions = dimensions.get(dimension);
            ObjectNode nodeOfAllSubdimensions = factory.objectNode();
            for (String subdimension : subdimensions.keySet()) {
                Set<String> approachesOfSubdimension = subdimensions.get(subdimension);

                ObjectNode subdimensionNode = factory.objectNode();
                subdimensionNode.put(DECISION, dim_decision.get(subdimension));
                subdimensionNode.put(EXPLANATION, dim_explanation.get(subdimension));
                ObjectNode nodeOfAllApproaches = factory.objectNode();
                for (String approach : approachesOfSubdimension) {
                    ObjectNode approachNode = factory.objectNode();
                    if (namesOfApproaches.get(approach) != null) {
                        Comparison comparison = namesOfApproaches.get(approach);
                        approachNode.set(A1, JsonUtility.convertObjectToType(comparison.getA1(), JsonNode.class));
                        approachNode.set(A2, JsonUtility.convertObjectToType(comparison.getA2(), JsonNode.class));
                        approachNode.set(DECISION, JsonUtility.convertObjectToType(comparison.getDecision(), JsonNode.class));
                        approachNode.set(EXPLANATIONS, JsonUtility.convertObjectToType(comparison.getExplanations(), JsonNode.class));
                        nodeOfAllApproaches.set(approach, approachNode);
                    }
                }

                subdimensionNode.set(APPROACHES, nodeOfAllApproaches);
                nodeOfAllSubdimensions.set(subdimension, subdimensionNode);
            }

            dimensionNode.put(DECISION, dim_decision.get(dimension));
            dimensionNode.put(EXPLANATION, dim_explanation.get(dimension));
            dimensionNode.set(SUBDIMENSIONS, nodeOfAllSubdimensions);
            argumentQualityNode.set(dimension, dimensionNode);
        }
        rootNode.put(DECISION, dim_decision.get(OVERALL));
        rootNode.put(EXPLANATION, dim_explanation.get(OVERALL));
        rootNode.set(ARGUMENTQUALITY, argumentQualityNode);

        return rootNode;
    }

    private static ArrayList<Integer> collectAllDecisionsOf(
            Set<String> approachesOfSubdimension,
            AAComparisons comparisons
    ) {
        Map<String, Comparison> approachesWithNames = namesOfApproaches(comparisons);

        ArrayList<Integer> decisions = new ArrayList<>();
        for (String approach : approachesOfSubdimension) {
            Comparison comparison = approachesWithNames.get(approach);
            int decision = comparison.getDecision();
            decisions.add(decision);
        }
        return decisions;
    }

    private static Map<String, Comparison> namesOfApproaches(AAComparisons comparisons) {
        Map<String, Comparison> approachesWithNames = new HashMap<>();
        for (AssessmentApproach assApp : comparisons.keySet()) {
            approachesWithNames.put(assApp.getName(), comparisons.get(assApp));
        }

        return approachesWithNames;
    }

}
