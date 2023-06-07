package de.seanbri.aquaplane.argumentQuality.dimension;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Dimension {

    public static final String OVERALL = "overall";
    public static final String LOGICQUALITY = "Logic Quality";
    public static final String LOCALACCEPTABILITY = "Local Acceptability";
    public static final String LOCALRELEVANCE= "Local Relevance";
    public static final String LOCALSUFFICIENCY = "Local Sufficiency";

    public static final String RHETORICQUALITY = "Rhetoric Quality";
    public static final String CREDIBILITY = "Credibility";
    public static final String EMOTIONALAPPEAL= "Emotional Appeal";
    public static final String CLARITY = "Clarity";
    public static final String APPROPRIATENESS = "Appropriateness";
    public static final String ARRANGEMENT = "Arrangement";

    public static final String DIALECTICQUALITY = "Dialectic Quality";
    public static final String GLOBALACCEPTABILITY = "Global Acceptability";
    public static final String GLOBALRELEVANCE= "Global Relevance";
    public static final String GLOBALSUFFICIENCY = "Global Sufficiency";


    public static Set<String> getAllDimensions() {
        String[] arr = new String[]
                {
                        LOGICQUALITY, RHETORICQUALITY, DIALECTICQUALITY
                };
        return new HashSet<>(Arrays.asList(arr));
    }

    public static Set<String> getAllSubdimensions() {
        String[] arr = new String[]
                {
                        LOCALACCEPTABILITY, LOCALRELEVANCE, LOCALSUFFICIENCY,
                        CREDIBILITY, EMOTIONALAPPEAL, CLARITY, APPROPRIATENESS, ARRANGEMENT,
                        GLOBALACCEPTABILITY, GLOBALRELEVANCE, GLOBALSUFFICIENCY
                };
        return new HashSet<>(Arrays.asList(arr));
    }

}
