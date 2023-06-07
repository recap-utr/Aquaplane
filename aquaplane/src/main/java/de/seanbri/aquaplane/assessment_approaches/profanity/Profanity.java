package de.seanbri.aquaplane.assessment_approaches.profanity;

import de.seanbri.aquaplane.argumentQuality.comparison.Comparison;
import de.seanbri.aquaplane.assessment_approaches.AssessmentApproach;
import de.seanbri.aquaplane.utility.DecisionMaking;
import de.seanbri.aquaplane.utility.TextUtility;
import edu.stanford.nlp.util.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Profanity implements AssessmentApproach {

    private static final String PROFANITY = "Profanity";
    private static final String PROFANITYFREQUENCY = "profanityFrequency";

    private static final List<String> badWords;


    static {
        try {
            badWords = Files.readAllLines(
                    Paths.get("src/main/resources/dictionaries/profanity", "list_of_bad_words_2022_05_05.txt")
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public record ProfanityRecord(
            List<String> profaneWords,
            double profanityFrequency
    ) {}


    @Override
    public String getName() {
        return PROFANITY;
    }

    @Override
    public ProfanityRecord computeRecord(String argument, String claim) {
        List<String> usedBadWords = new ArrayList<String>();

        argument = TextUtility.removeURLs(argument);
        argument = argument.toLowerCase();

        int allOccurrences = 0;
        for (String badWord : badWords) {
            if (containsBadWord(argument, badWord)) {
                usedBadWords.add(badWord);
                allOccurrences += countOccurrences(argument, badWord);
            }
        }

        double textLengthInWords = TextUtility.onlyWordTokens(TextUtility.tokenize(argument)).size();
        double profanityFrequency = allOccurrences / textLengthInWords;

        return new ProfanityRecord(usedBadWords, profanityFrequency);
    }

    private static boolean containsBadWord(String text, String badWord) {
        Pattern p = Pattern.compile("\\b" + badWord + "(\\b|[ .,\\?!'\\\";:-]+)");
        Matcher m = p.matcher(text);
        return m.find();
    }

    private static int countOccurrences(String text, String badWord) {
        int count = 0;
        Pattern p = Pattern.compile("\\b" + badWord + "(\\b|[ .,\\?!'\\\";:-]+)");
        Matcher m = p.matcher(text);
        while(m.find()) {
            count++;
        }

        return count;
    }

    @Override
    public Comparison compare(Record r1, Record r2) {
        Map<String, Integer> feature_decision = new HashMap<>();

        ProfanityRecord pr1 = (ProfanityRecord) r1;
        ProfanityRecord pr2 = (ProfanityRecord) r2;

        int decision = DecisionMaking.decisionLow(
                pr1.profanityFrequency,
                pr2.profanityFrequency
        );

        feature_decision.put(
                PROFANITYFREQUENCY,
                decision
        );

        return new Comparison(pr1, pr2, decision, feature_decision);
    }

    @Override
    public List<String> explain(Comparison comparison) {
        List<String> explanations = new ArrayList<>();
        if (comparison.getDecision() == 0) {        // Only generate explanations if decision is unequivocal
            return explanations;
        }

        String template =
                "Argument %d uses more swear words that are mostly blacklisted.\n" +
                "The following words were marked as profanity:%n";
        String explanation = String.format(template,
                comparison.getInvertedDecision()
        );
        ProfanityRecord profanityRecord = (ProfanityRecord) comparison.getObjectOfInvertedDecision();
        String profanityPhrases = StringUtils.join(profanityRecord.profaneWords(), ", ");
        explanation = explanation + profanityPhrases;
        explanations.add(explanation);

        return explanations;
    }
}
