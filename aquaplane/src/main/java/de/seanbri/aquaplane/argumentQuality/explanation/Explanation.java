package de.seanbri.aquaplane.argumentQuality.explanation;

import static de.seanbri.aquaplane.argumentQuality.dimension.Dimension.*;

public class Explanation {

    public static String getOverallExplanation(int decision) {
        return switch (decision) {
            case 0 -> "Argument 1 and argument 2 are equally convincing.";
            case 1 -> "Argument 1 is more convincing than argument 2.";
            case 2 -> "Argument 2 is more convincing than argument 1.";
            default -> "";
        };
    }

    public static String getExplanationForDimension(int decision, String dimension) {
        return switch (decision) {
            case 0 -> "Argument 1 and argument 2 have no big difference in their " + dimension + ".";
            case 1 -> switch (dimension) {
                case LOGICQUALITY ->
                        "Argument 1 is more cogent than argument 2, because of its " + dimension + ".";
                case RHETORICQUALITY ->
                        "Argument 1 is more effective than argument 2, because of its " + dimension + ".";
                case DIALECTICQUALITY ->
                        "Argument 1 is more reasonable than argument 2, because of its " + dimension + ".";
                default -> "";
            };
            case 2 -> switch (dimension) {
                case LOGICQUALITY ->
                        "Argument 2 is more cogent than argument 1, because of its " + dimension + ".";
                case RHETORICQUALITY ->
                        "Argument 2 is more effective than argument 1, because of its " + dimension + ".";
                case DIALECTICQUALITY ->
                        "Argument 2 is more reasonable than argument 1, because of its " + dimension + ".";
                default -> "";
            };
            default -> "";
        };
    }

    public static String getExplanationForSubdimension(int decision, String subdimension) {
        return switch (decision) {
            case 0 -> "Argument 1 and Argument 2 are equally convincing in terms of " + subdimension + ".";
            case 1 -> "Argument 1 has a higher " + subdimension + " than argument 2.";
            case 2 -> "Argument 2 has a higher " + subdimension + " than argument 1.";
            default -> "";
        };
    }

}
