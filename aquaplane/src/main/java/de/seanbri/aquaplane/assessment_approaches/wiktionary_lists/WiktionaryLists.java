package de.seanbri.aquaplane.assessment_approaches.wiktionary_lists;

import de.seanbri.aquaplane.argumentQuality.comparison.Comparison;
import de.seanbri.aquaplane.assessment_approaches.AssessmentApproach;
import de.seanbri.aquaplane.utility.DecisionMaking;
import de.seanbri.aquaplane.utility.TextUtility;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class WiktionaryLists implements AssessmentApproach {

    public record WiktionaryRecord(
            List<String> superlatives,
            List<String> comparatives,
            List<String> modal_adverbs,
            List<String> act_based_adverbs,
            List<String> manner_adverbs,
            double adverbFrequency
    ) {}


    private static final String WIKTIONARYLISTS = "WiktionaryLists";
    private static final String ADVERBFREQUENCY = "adverbFrequency";

    private static final HashSet<String> superlatives;
    private static final HashSet<String> comparatives;
    private static final HashSet<String> modal_adverbs;
    private static final HashSet<String> act_based_adverbs;
    private static final HashSet<String> manner_adverbs;


    static {
        superlatives = readFileToHashSet("src/main/resources/dictionaries/wiktionarylists/superlative_forms.txt");
        comparatives = readFileToHashSet("src/main/resources/dictionaries/wiktionarylists/comparative_forms.txt");
        modal_adverbs = readFileToHashSet("src/main/resources/dictionaries/wiktionarylists/modal_adverbs.txt");
        act_based_adverbs = readFileToHashSet("src/main/resources/dictionaries/wiktionarylists/act_adverbs.txt");
        manner_adverbs = readFileToHashSet("src/main/resources/dictionaries/wiktionarylists/manner_adverbs.txt");
    }


    private static HashSet<String> readFileToHashSet(String path) {
        HashSet<String> phrase = new HashSet<String>();
        try {
            Scanner file = new Scanner(new File(path));

            while (file.hasNext()) {
                String word = file.nextLine()
                        .replace("_"," ")
                        .trim()
                        .toLowerCase();
                phrase.add(word);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return phrase;
    }

    @Override
    public String getName() {
        return WIKTIONARYLISTS;
    }

    @Override
    public WiktionaryRecord computeRecord(String argument, String claim) {
        ArrayList<String> tokenizedWords = TextUtility.lowerCaseTokens(TextUtility.onlyWordTokens(TextUtility.tokenize(argument)));

        ArrayList<String> nGrams = new ArrayList<String>();
        for (int i = 2; i <= 6; i++) {
            nGrams.addAll(wordNGrams(tokenizedWords, i));
        }
        tokenizedWords.addAll(nGrams);

        List<String> superlatives = new ArrayList<>();
        List<String> comparatives = new ArrayList<>();
        List<String> modal_adverbs = new ArrayList<>();
        List<String> act_based_adverbs = new ArrayList<>();
        List<String> manner_adverbs = new ArrayList<>();
        for (String word : tokenizedWords) {
            if (isSuperlative(word)) superlatives.add(word);
            if (isComparative(word)) comparatives.add(word);
            if (isModalAdverb(word)) modal_adverbs.add(word);
            if (isActBasedAdverb(word)) act_based_adverbs.add(word);
            if (isMannerAdverb(word)) manner_adverbs.add(word);
        }

        double numOfAdverbs = superlatives.size() + comparatives.size() + modal_adverbs.size() + act_based_adverbs.size() + manner_adverbs.size();
        double textLengthInWords = TextUtility.onlyWordTokens(TextUtility.tokenize(argument)).size();
        double adverbFrequency = numOfAdverbs / textLengthInWords;

        return new WiktionaryRecord(
                superlatives,
                comparatives,
                modal_adverbs,
                act_based_adverbs,
                manner_adverbs,
                adverbFrequency
        );
    }

    private ArrayList<String> wordNGrams(ArrayList<String> words, int nGram) {
        if (nGram < 2) {
            throw new IllegalArgumentException("Only N-Grams >= 2");
        }

        ArrayList<String> ngrams = new ArrayList<String>();
        for (int i = 0; i < words.size() - nGram + 1 ; i++) {
            ArrayList<String> tmp = new ArrayList<String>();

            for (int j = 0; j < nGram; j++) {
                tmp.add(words.get(i + j));
            }

            ngrams.add(String.join(" ", tmp));
        }
        
        return ngrams;
    }

    private boolean isSuperlative(String word) {
        return superlatives.contains(word);
    }

    private boolean isComparative(String word) {
        return comparatives.contains(word);
    }

    private boolean isModalAdverb(String word) {
        return modal_adverbs.contains(word);
    }

    private boolean isActBasedAdverb(String word) {
        return act_based_adverbs.contains(word);
    }

    private boolean isMannerAdverb(String word) {
        return manner_adverbs.contains(word);
    }

    @Override
    public Comparison compare(Record r1, Record r2) {
        Map<String, Integer> feature_decision = new HashMap<>();

        WiktionaryRecord wr1 = (WiktionaryRecord) r1;
        WiktionaryRecord wr2 = (WiktionaryRecord) r2;

        int decision = DecisionMaking.decisionLow(
                wr1.adverbFrequency,
                wr2.adverbFrequency
        );

        feature_decision.put(
                ADVERBFREQUENCY,
                decision
        );

        return new Comparison(wr1, wr2, decision, feature_decision);
    }

    @Override
    public List<String> explain(Comparison comparison) {
        List<String> explanations = new ArrayList<>();
        if (comparison.getDecision() == 0) {        // Only generate explanations if decision is unequivocal
            return explanations;
        }

        String template =
                "Argument %d uses more dramatic language." +
                "This is due to the increased use of different types of adverbs.";
        String explanation = String.format(template,
                comparison.getInvertedDecision()
        );
        explanations.add(explanation);

        return explanations;
    }

}
