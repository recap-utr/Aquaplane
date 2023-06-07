package de.seanbri.aquaplane.assessment_approaches.excessive_punctuation;

import de.seanbri.aquaplane.argumentQuality.comparison.Comparison;
import de.seanbri.aquaplane.assessment_approaches.AssessmentApproach;
import de.seanbri.aquaplane.utility.DecisionMaking;
import de.seanbri.aquaplane.utility.TextUtility;
import edu.stanford.nlp.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExcessivePunctuation implements AssessmentApproach {

    private static final String EXCESSIVEPUNCTUATIONREGEX = "[\\p{Punct}]{3,}";

    public static final String PUNCTUATION = "Punctuation";
    private static final String MORETHAN2MARKS = "moreThan2Marks";
    private static final String MAXREPETITIONS = "maxRepetitions";


    public record ExcessivePunctuationRecord(
            List<String> markPhrases,
            int moreThan2Marks,
            int maxRepetitions
    ) {}


    @Override
    public String getName() {
        return PUNCTUATION;
    }

    @Override
    public ExcessivePunctuationRecord computeRecord(String argument, String claim) {
        List<String> markPhrases = new ArrayList<String>();

        argument = TextUtility.removeURLs(argument);    // "://" could be matched as excessive punctuation

        Pattern pattern = Pattern.compile(EXCESSIVEPUNCTUATIONREGEX, Pattern.UNICODE_CHARACTER_CLASS);
        Matcher matcher = pattern.matcher(argument);
        while(matcher.find()) {
            markPhrases.add(matcher.group());
        }

        int maxRepetitions = markPhrases.stream()
                .map(String::length)
                .max(Integer::compareTo)
                .orElse(0);

        return new ExcessivePunctuationRecord(markPhrases, markPhrases.size(), maxRepetitions);
    }

    @Override
    public Comparison compare(Record r1, Record r2) {
        Map<String, Integer> feature_decision = new HashMap<>();

        ExcessivePunctuationRecord epr1 = (ExcessivePunctuationRecord) r1;
        ExcessivePunctuationRecord epr2 = (ExcessivePunctuationRecord) r2;

        feature_decision.put(
                MORETHAN2MARKS,
                DecisionMaking.decisionLow(
                    epr1.moreThan2Marks,
                    epr2.moreThan2Marks
                )
        );
        feature_decision.put(
                MAXREPETITIONS,
                DecisionMaking.decisionLow(
                        epr1.maxRepetitions,
                        epr2.maxRepetitions
                )
        );

        int decision = DecisionMaking.findMajority(feature_decision.values().stream().toList());

        return new Comparison(epr1, epr2, decision, feature_decision);
    }

    @Override
    public List<String> explain(Comparison comparison) {
        List<String> explanations = new ArrayList<>();
        if (comparison.getDecision() == 0) {        // Only generate explanations if decision is unequivocal
            return explanations;
        }

        String template =
                "Argument %d has excessive punctuation, which makes an author appear angry and thus unprofessional. " +
                "The following sequences of punctuation were considered excessive:%n";
        String explanation = String.format(template,
                comparison.getInvertedDecision()
        );
        ExcessivePunctuationRecord XPunctuationRecord = (ExcessivePunctuationRecord) comparison.getObjectOfInvertedDecision();
        String XPunctuationSequences = StringUtils.join(XPunctuationRecord.markPhrases(), ", ");
        explanation = explanation + XPunctuationSequences;
        explanations.add(explanation);

        return explanations;
    }

}
