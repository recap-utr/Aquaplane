package de.seanbri.aquaplane.argumentQuality.dimension;

import java.util.*;

import static de.seanbri.aquaplane.argumentQuality.dimension.Dimension.*;

public class DimensionMapper {

    public static final String FACTCHECKING = "FactChecking";
    public static final String SPELLCHECKING = "SpellChecking";
    public static final String STYLEEXPLORER_READABILITY = "StyleExplorer_Readability";
    public static final String STYLEEXPLORER_STYLECOMPLEXITY = "StyleExplorer_StyleComplexity";
    public static final String STYLEEXPLORER_VOCABULARY_DIVERSITY= "StyleExplorer_VocabularyDiversity";
    public static final String STYLEEXPLORER_VOCABULARY_RICHNESS= "StyleExplorer_VocabularyRichness";
    public static final String STYLEEXPLORER_ERRORS = "StyleExplorer_Errors";
    public static final String WIKTIONARYLISTS = "WiktionaryLists";
    public static final String SIMPLEWIKI_SCORE = "SimpleWiki_Score";
    public static final String URLSOURCES = "URLSources";
    public static final String ALLCAPS = "AllCaps";
    public static final String PUNCTUATION = "Punctuation";
    public static final String PROFANITY = "Profanity";
    public static final String DEBATEORG_SCORE = "DebateOrg_Score";
    public static final String ADHOMINEM = "AdHominem";


    public static Map<String, Map<String, Set<String>>> getMappings() {
        Map<String, Map<String, Set<String>>> dimensions_subdimensions = new HashMap<>();

        Map<String, Set<String>> logicQuality = new HashMap<>();
        logicQuality.put(LOCALACCEPTABILITY, getLocalAcceptability());
        logicQuality.put(LOCALRELEVANCE, getLocalRelevance());
        logicQuality.put(LOCALSUFFICIENCY, getLocalSufficiency());
        dimensions_subdimensions.put(LOGICQUALITY, logicQuality);

        Map<String, Set<String>> rhetoricQuality = new HashMap<>();
        rhetoricQuality.put(CREDIBILITY, getCredibility());
        rhetoricQuality.put(EMOTIONALAPPEAL, getEmotionalAppeal());
        rhetoricQuality.put(CLARITY, getClarity());
        rhetoricQuality.put(APPROPRIATENESS, getAppropriateness());
        rhetoricQuality.put(ARRANGEMENT, getArrangement());
        dimensions_subdimensions.put(RHETORICQUALITY, rhetoricQuality);

        Map<String, Set<String>> dialecticQuality = new HashMap<>();
        dialecticQuality.put(GLOBALACCEPTABILITY, getGlobalAcceptability());
        dialecticQuality.put(GLOBALRELEVANCE, getGlobalRelevance());
        dialecticQuality.put(GLOBALSUFFICIENCY, getGlobalSufficiency());
        dimensions_subdimensions.put(DIALECTICQUALITY, dialecticQuality);

        return dimensions_subdimensions;
    }

    // Logic Quality
    public static Set<String> getLocalAcceptability() {
        String[] arr = new String[]
                {
                        FACTCHECKING
                };
        return new HashSet<>(Arrays.asList(arr));
    }

    public static Set<String> getLocalRelevance() {
        String[] arr = new String[]
                {
                        SIMPLEWIKI_SCORE
                };
        return new HashSet<>(Arrays.asList(arr));
    }

    public static Set<String> getLocalSufficiency() {
        String[] arr = new String[]
                {
                        // Todo: This dimension has been left out
                };
        return new HashSet<>(Arrays.asList(arr));
    }

    // Rhetoric Quality
    public static Set<String> getCredibility() {
        String[] arr = new String[]
                {
                        URLSOURCES, SPELLCHECKING, PROFANITY, ALLCAPS, PUNCTUATION
                };
        return new HashSet<>(Arrays.asList(arr));
    }

    public static Set<String> getEmotionalAppeal() {
        String[] arr = new String[]
                {
                        WIKTIONARYLISTS, PUNCTUATION
                };
        return new HashSet<>(Arrays.asList(arr));
    }

    public static Set<String> getClarity() {
        String[] arr = new String[]
                {
                        SPELLCHECKING,
                        STYLEEXPLORER_STYLECOMPLEXITY,
                        STYLEEXPLORER_READABILITY,
                        STYLEEXPLORER_VOCABULARY_DIVERSITY,
                        STYLEEXPLORER_VOCABULARY_RICHNESS,
                        STYLEEXPLORER_ERRORS
                };
        return new HashSet<>(Arrays.asList(arr));
    }

    public static Set<String> getAppropriateness() {
        String[] arr = new String[]
                {
                        PROFANITY, ALLCAPS, PUNCTUATION
                };
        return new HashSet<>(Arrays.asList(arr));
    }

    public static Set<String> getArrangement() {
        String[] arr = new String[]
                {
                        // Todo: This dimension has been left out
                };
        return new HashSet<>(Arrays.asList(arr));
    }

    // Dialectic Quality
    public static Set<String> getGlobalAcceptability() {
        String[] arr = new String[]
                {
                        FACTCHECKING, ADHOMINEM
                };
        return new HashSet<>(Arrays.asList(arr));
    }

    public static Set<String> getGlobalRelevance() {
        String[] arr = new String[]
                {
                        DEBATEORG_SCORE, SIMPLEWIKI_SCORE
                };
        return new HashSet<>(Arrays.asList(arr));
    }

    public static Set<String> getGlobalSufficiency() {
        String[] arr = new String[]
                {
                        // Todo: This dimension has been left out
                };
        return new HashSet<>(Arrays.asList(arr));
    }
}
