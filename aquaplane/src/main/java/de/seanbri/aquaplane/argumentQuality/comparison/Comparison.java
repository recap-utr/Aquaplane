package de.seanbri.aquaplane.argumentQuality.comparison;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Comparison {

    private Object a1;
    private Object a2;
    private int decision;
    private Map<String, Integer> feature_decision;
    private List<String> explanations;


    public Comparison(Object a1, Object a2, int decision, Map<String, Integer> feature_decision) {
        this.a1 = a1;
        this.a2 = a2;
        this.decision = decision;
        this.feature_decision = feature_decision;
        this.explanations = new ArrayList<>();
    }

    public Object getA1() {
        return a1;
    }

    public void setA1(Object a1) {
        this.a1 = a1;
    }

    public Object getA2() {
        return a2;
    }

    public void setA2(Object a2) {
        this.a2 = a2;
    }

    public int getDecision() {
        return decision;
    }

    public void setDecision(int decision) {
        this.decision = decision;
    }

    public List<String> getExplanations() {
        return explanations;
    }

    public void setExplanations(List<String> explanations) {
        this.explanations = explanations;
    }

    public Map<String, Integer> getFeature_decision() {
        return feature_decision;
    }


    public int getInvertedDecision() {
        return switch(decision) {
            case 1 -> 2;
            case 2 -> 1;
            default -> 0;
        };
    }

    public Object getObjectOfDecision() {
        return switch(decision) {
            case 1 -> a1;
            case 2 -> a2;
            default -> null;
        };
    }

    public Object getObjectOfInvertedDecision() {
        return switch(decision) {
            case 1 -> a2;
            case 2 -> a1;
            default -> null;
        };
    }
}
