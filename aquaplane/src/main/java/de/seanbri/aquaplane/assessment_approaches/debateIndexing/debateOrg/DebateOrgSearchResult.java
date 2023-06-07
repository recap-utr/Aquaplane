package de.seanbri.aquaplane.assessment_approaches.debateIndexing.debateOrg;

public class DebateOrgSearchResult {

    private final String nodeId;
    private final String nodeTitle;
    private final String nodeUrl;
    private final String nodeImage;
    private final int nodeWeightsLeft;
    private final int nodeWeightsRight;
    private final String stance;
    private final String premiseId;
    private final String premiseTitle;
    private final String premiseText;
    private final String premiseAuthor;
    private final int premiseLikeCount;
    private final String premiseLikeUsers;
    private final int premiseCommentCount;
    private final int numberOfProArguments;
    private final int numberOfConArguments;
    private final double score;


    public DebateOrgSearchResult(
            String nodeId,
            String nodeTitle,
            String nodeUrl,
            String nodeImage,
            int nodeWeightsLeft,
            int nodeWeightsRight,
            String stance,
            String premiseId,
            String premiseTitle,
            String premiseText,
            String premiseAuthor,
            int premiseLikeCount,
            String premiseLikeUsers,
            int premiseCommentCount,
            int numberOfProArguments,
            int numberOfConArguments,
            double score
    ) {
        this.nodeId = nodeId;
        this.nodeTitle = nodeTitle;
        this.nodeUrl = nodeUrl;
        this.nodeImage = nodeImage;
        this.nodeWeightsLeft = nodeWeightsLeft;
        this.nodeWeightsRight = nodeWeightsRight;
        this.stance = stance;
        this.premiseId = premiseId;
        this.premiseTitle = premiseTitle;
        this.premiseText = premiseText;
        this.premiseAuthor = premiseAuthor;
        this.premiseLikeCount = premiseLikeCount;
        this.premiseLikeUsers = premiseLikeUsers;
        this.premiseCommentCount = premiseCommentCount;
        this.numberOfProArguments = numberOfProArguments;
        this.numberOfConArguments = numberOfConArguments;
        this.score = score;
    }

    public String getNodeId() {
        return nodeId;
    }

    public String getNodeTitle() {
        return nodeTitle;
    }

    public String getNodeUrl() {
        return nodeUrl;
    }

    public String getNodeImage() {
        return nodeImage;
    }

    public int getNodeWeightsLeft() {
        return nodeWeightsLeft;
    }

    public int getNodeWeightsRight() {
        return nodeWeightsRight;
    }

    public String getStance() {
        return stance;
    }

    public String getPremiseId() {
        return premiseId;
    }

    public String getPremiseTitle() {
        return premiseTitle;
    }

    public String getPremiseText() {
        return premiseText;
    }

    public String getPremiseAuthor() {
        return premiseAuthor;
    }

    public int getPremiseLikeCount() {
        return premiseLikeCount;
    }

    public String getPremiseLikeUsers() {
        return premiseLikeUsers;
    }

    public int getPremiseCommentCount() {
        return premiseCommentCount;
    }

    public int getNumberOfProArguments() {
        return numberOfProArguments;
    }

    public int getNumberOfConArguments() {
        return numberOfConArguments;
    }

    public double getScore() {
        return score;
    }
}
