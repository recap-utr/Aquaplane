package de.seanbri.aquaplane.assessment_approaches.debateIndexing.simpleWiki;

public class SimpleWikiSearchResult {

    private final String claim;
    private final String premise;
    private final String title;
    private final String text;
    private final double score;

    public SimpleWikiSearchResult(String claim, String premise, String title, String text, double score) {
        this.claim = claim;
        this.premise = premise;
        this.title = title;
        this.text = text;
        this.score = score;
    }

    public String getClaim() {
        return claim;
    }

    public String getPremise() {
        return premise;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public double getScore() {
        return score;
    }
}
