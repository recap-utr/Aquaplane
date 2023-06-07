package de.seanbri.aquaplane.assessment_approaches.spell_checking;

import java.util.List;

public class SpellCheckMatch {

    String probableWrongWrittenWord;
    List<String> suggestedReplacements;
    boolean isRatherAWrongSpelledWordThanAnUnknownWord;
    double levenshteinDistance;

    public SpellCheckMatch(String probableWrongWrittenWord, List<String> suggestedReplacements, double levenshteinDistance) {
        this.probableWrongWrittenWord = probableWrongWrittenWord;
        this.suggestedReplacements = suggestedReplacements;
        this.isRatherAWrongSpelledWordThanAnUnknownWord = suggestedReplacements.size() > 0;
        this.levenshteinDistance = levenshteinDistance;
    }

    public String getProbableWrongWrittenWord() {
        return probableWrongWrittenWord;
    }

    public List<String> getSuggestedReplacements() {
        return suggestedReplacements;
    }

    public boolean isRatherAWrongSpelledWordThanAnUnknownWord() {
        return isRatherAWrongSpelledWordThanAnUnknownWord;
    }

    public double getLevenshteinDistance() {
        return levenshteinDistance;
    }
}
