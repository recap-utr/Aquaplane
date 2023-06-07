package de.seanbri.aquaplane.assessment_approaches.all_caps;

import de.seanbri.aquaplane.argumentQuality.comparison.Comparison;
import de.seanbri.aquaplane.assessment_approaches.AssessmentApproach;
import de.seanbri.aquaplane.utility.DecisionMaking;
import de.seanbri.aquaplane.utility.TextUtility;
import edu.stanford.nlp.util.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AllCaps implements AssessmentApproach {

    private static final String ALLCAPSREGEX = "[A-Z]{2,}";

    private static final String ALLCAPS = "AllCaps";
    private static final String ALLCAPSFREQUENCY = "allCapsFrequency";


    public record AllCapsRecord(
            List<String> allCapsWords,
            double allCapsFrequency
    ) {}


    @Override
    public String getName() {
        return ALLCAPS;
    }

    @Override
    public AllCapsRecord computeRecord(String argument, String claim) {
        List<String> allCapsWords = new ArrayList<String>();

        Pattern pattern = Pattern.compile(ALLCAPSREGEX);
        Matcher matcher = pattern.matcher(argument);
        while(matcher.find()) {
            allCapsWords.add(matcher.group());
        }

        double textLengthInWords = TextUtility.onlyWordTokens(TextUtility.tokenize(argument)).size();
        double allCapsFrequency = allCapsWords.size() / textLengthInWords;

        return new AllCapsRecord(allCapsWords, allCapsFrequency);
    }

    @Override
    public Comparison compare(Record r1, Record r2) {
        Map<String, Integer> feature_decision = new HashMap<>();

        AllCapsRecord ar1 = (AllCapsRecord) r1;
        AllCapsRecord ar2 = (AllCapsRecord) r2;

        int decision = DecisionMaking.decisionLow(
                ar1.allCapsFrequency,
                ar2.allCapsFrequency
        );

        feature_decision.put(
                ALLCAPSFREQUENCY,
                decision
        );

        return new Comparison(ar1, ar2, decision, feature_decision);
    }

    @Override
    public List<String> explain(Comparison comparison) {
        List<String> explanations = new ArrayList<>();
        if (comparison.getDecision() == 0) {        // Only generate explanations if decision is unequivocal
            return explanations;
        }

        String template =
                "Argument %d uses more words in capital letters and is thus more inappropriate.\n" +
                "The following all caps words were used:%n";
        String explanation = String.format(template,
                comparison.getInvertedDecision()
        );
        AllCapsRecord allCapsRecord = (AllCapsRecord) comparison.getObjectOfInvertedDecision();
        String allCapsWords = StringUtils.join(allCapsRecord.allCapsWords(), ", ");
        explanation = explanation + allCapsWords;
        explanations.add(explanation);

        return explanations;
    }

}
