package de.seanbri.aquaplane.assessment_approaches.spell_checking;

import de.seanbri.aquaplane.argumentQuality.comparison.Comparison;
import de.seanbri.aquaplane.assessment_approaches.AssessmentApproach;
import de.seanbri.aquaplane.utility.DecisionMaking;
import de.seanbri.aquaplane.utility.TextUtility;
import edu.stanford.nlp.util.StringUtils;
import org.languagetool.JLanguageTool;
import org.languagetool.language.*;
import org.languagetool.rules.RuleMatch;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class SpellChecker implements AssessmentApproach {

    public record SpellCheckingRecord(
            double wrongWritten,
            double unknown,
            double sumOfLevenshteinDistancesToCorrectWords,
            List<SpellCheckMatch> matches
    ) {
        public List<SpellCheckMatch> getSpellCheckMatchesOfWrongWrittenWords() {
            return matches.stream()
                    .filter(spellCheckMatch -> spellCheckMatch.isRatherAWrongSpelledWordThanAnUnknownWord)
                    .collect(Collectors.toList());
        }

        public List<String> getWrongWrittenWords() {
            return getSpellCheckMatchesOfWrongWrittenWords().stream()
                    .map(SpellCheckMatch::getProbableWrongWrittenWord)
                    .collect(Collectors.toList());
        }

        public List<SpellCheckMatch> getSpellCheckMatchesOfUnknownWords() {
            return matches.stream()
                    .filter(spellCheckMatch -> !spellCheckMatch.isRatherAWrongSpelledWordThanAnUnknownWord)
                    .collect(Collectors.toList());
        }

        public List<String> getUnknownWords() {
            return getSpellCheckMatchesOfUnknownWords().stream()
                    .map(SpellCheckMatch::getProbableWrongWrittenWord)
                    .collect(Collectors.toList());
        }
    }


    private static final JLanguageTool langTool_britishEnglish = new JLanguageTool(new BritishEnglish());
    private static final JLanguageTool langTool_americanEnglish = new JLanguageTool(new AmericanEnglish());

    private static final String SPELLCHECKING = "SpellChecking";
    private static final String WRONG_WRITTEN = "wrongWritten";
    private static final String UNKNOWN = "unknown";
    private static final String SUM_OF_LEVENSHTEIN_DISTANCES_TO_CORRECT_WORDS = "sumOfLevenshteinDistancesToCorrectWords";


    @Override
    public String getName() {
        return SPELLCHECKING;
    }

    @Override
    public SpellCheckingRecord computeRecord(String argument, String claim) {
        try {
            argument = TextUtility.removeURLs(argument);
            String normalizedSentence = normalizeText(argument, true,true);

            int numOfWrongWrittenWords = 0;
            int numOfUnknownWords = 0;
            double sumOfLevenshteinDistancesToCorrectWords = 0;

            List<SpellCheckMatch> matches = getSpellCheckMatches(normalizedSentence);
            for (SpellCheckMatch match : matches) {
                if (match.isRatherAWrongSpelledWordThanAnUnknownWord) {
                    numOfWrongWrittenWords++;
                } else {
                    numOfUnknownWords++;
                }
                sumOfLevenshteinDistancesToCorrectWords += match.levenshteinDistance;
            }

            double textLengthInWords = TextUtility.onlyWordTokens(TextUtility.tokenize(argument)).size();
            double wrongWritten = numOfWrongWrittenWords / textLengthInWords;
            double unknown = numOfUnknownWords / textLengthInWords;

            return new SpellCheckingRecord(
                    wrongWritten,
                    unknown,
                    sumOfLevenshteinDistancesToCorrectWords,
                    matches
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new SpellCheckingRecord(
                0.0,
                0.0,
                0.0,
                new ArrayList<>()
        );
    }

    private List<SpellCheckMatch> getSpellCheckMatches(String sentenceToBeChecked) throws IOException {

        List<RuleMatch> matches_britishEnglish = langTool_britishEnglish.check(sentenceToBeChecked);
        List<RuleMatch> matches_americanEnglish = langTool_americanEnglish.check(sentenceToBeChecked);

        List<SpellCheckMatch> spellCheckMatches = new ArrayList<>();

        for (RuleMatch match : matches_britishEnglish) {
            int begin = match.getFromPos();
            int end = match.getToPos();

            boolean correctInOtherLanguages
                    = wordSpellingIsCorrectInAnOtherLanguage(matches_americanEnglish, begin, end);

            String probableWrongWrittenWord = sentenceToBeChecked.substring(begin, end);
            double levenshteinDistance = 0;

            if (!correctInOtherLanguages) {
                if (match.getSuggestedReplacements().size() > 0) {
                    String suggestedReplacement = match.getSuggestedReplacements().get(0);
                    levenshteinDistance = levenshteinDistance(probableWrongWrittenWord, suggestedReplacement);
                }

                spellCheckMatches.add(
                        new SpellCheckMatch(
                                probableWrongWrittenWord,
                                match.getSuggestedReplacements(),
                                levenshteinDistance
                        )
                );
            }
        }

        return spellCheckMatches;
    }

    private boolean wordSpellingIsCorrectInAnOtherLanguage(List<RuleMatch> matches_other, int begin_this, int end_this) {
        for (RuleMatch match : matches_other) {
            int begin_other = match.getFromPos();
            int end_other = match.getToPos();

            if (begin_this == begin_other && end_this == end_other) {
                return false;
            }
        }
        return true;
    }


    public String normalizeText(String text, boolean removeNonWordCharacters, boolean normalizeWhiteSpace) {
        String normalizedText = text;

        if (removeNonWordCharacters)
            normalizedText = text.replaceAll("[^a-zA-Z0-9 ]+", " ");

        if (normalizeWhiteSpace)
            normalizedText = normalizedText.replaceAll("\\s+", " ");

        String[] textFragments = normalizedText.split(" ");

        StringBuilder stringBuilder = new StringBuilder();
        for (String textFragment : textFragments) {

            if (stringBuilder.length() > 0)
                stringBuilder.append(" ");

            stringBuilder.append(textFragment);
        }

        return String.valueOf(stringBuilder);
    }

    public int levenshteinDistance(String s1, String s2) {
        int[][] matrix = new int[s1.length() + 1][s2.length() + 1];

        for (int i = 0; i <= s1.length(); i++) {
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0) {   // fill first row
                    matrix[i][j] = j;
                }
                else if (j == 0) {  // fill first column
                    matrix[i][j] = i;
                }
                else {  // minimal number of operations
                    matrix[i][j] = min(
                            matrix[i - 1][j - 1] + (s1.charAt(i - 1) == s2.charAt(j - 1) ? 0 : 1),
                            matrix[i - 1][j] + 1,
                            matrix[i][j - 1] + 1
                    );
                }
            }
        }

        return matrix[s1.length()][s2.length()];
    }

    public int min(int substitution, int deletion, int insertion) {
        return Math.min(Math.min(substitution, deletion), insertion);
    }

    @Override
    public Comparison compare(Record r1, Record r2) {
        Map<String, Integer> feature_decision = new HashMap<>();

        SpellCheckingRecord scr1 = (SpellCheckingRecord) r1;
        SpellCheckingRecord scr2 = (SpellCheckingRecord) r2;

        feature_decision.put(
                WRONG_WRITTEN,
                DecisionMaking.decisionLow(
                    scr1.wrongWritten,
                    scr2.wrongWritten
                )
        );
        feature_decision.put(
                UNKNOWN,
                DecisionMaking.decisionLow(
                    scr1.unknown,
                    scr2.unknown
                )
        );
        feature_decision.put(
                SUM_OF_LEVENSHTEIN_DISTANCES_TO_CORRECT_WORDS,
                DecisionMaking.decisionLow(
                     scr1.sumOfLevenshteinDistancesToCorrectWords,
                     scr2.sumOfLevenshteinDistancesToCorrectWords
                )
        );

        int decision = DecisionMaking.findMajority(feature_decision.values().stream().toList());

        return new Comparison(scr1, scr2, decision, feature_decision);
    }

    @Override
    public List<String> explain(Comparison comparison) {
        List<String> explanations = new ArrayList<>();
        if (comparison.getDecision() == 0) {        // Only generate explanations if decision is unequivocal
            return explanations;
        }

        String template = "Argument %d has more spelling mistakes.";
        String explanation = String.format(template,
                comparison.getInvertedDecision()
        );
        explanations.add(explanation);

        SpellCheckingRecord spellCheckingRecord = (SpellCheckingRecord) comparison.getObjectOfInvertedDecision();
        if (spellCheckingRecord.wrongWritten() != 0.0) {
            List<String> wrongWrittenWords = spellCheckingRecord.getWrongWrittenWords();

            StringBuilder sb = new StringBuilder();
            sb.append("There are %d wrong written words:\n");
            sb.append(StringUtils.join(wrongWrittenWords, ", "));
            sb.append(".");
            explanation = String.format(sb.toString(), wrongWrittenWords.size());
            explanations.add(explanation);
        }

        if (spellCheckingRecord.unknown() != 0.0) {
            List<String> unknownWords = spellCheckingRecord.getUnknownWords();

            StringBuilder sb = new StringBuilder();
            sb.append("Furthermore %d words are unknown:\n");
            sb.append(StringUtils.join(unknownWords, ", "));
            sb.append(".");
            explanation = String.format(sb.toString(), unknownWords.size());
            explanations.add(explanation);
        }

        return explanations;
    }
}
