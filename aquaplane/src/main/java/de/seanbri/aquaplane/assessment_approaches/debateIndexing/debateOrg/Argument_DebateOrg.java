package de.seanbri.aquaplane.assessment_approaches.debateIndexing.debateOrg;

public class Argument_DebateOrg {

    String nodeId;
    String nodeTitle;
    String nodeUrl;
    String nodeImage;

    // JsonNode nodeWeights;
    Integer nodeWeightsLeft;
    Integer nodeWeightsRight;

    String stance;
    
    String premiseId;
    String premiseTitle;
    String premiseText;
    String premiseAuthor;
    Integer premiseLikeCount;
    String premiseLikeUsers;
    Integer premiseCommentCount;

    Integer numberOfProArguments;
    Integer numberOfConArguments;


    public Argument_DebateOrg(
        String nodeId,
        String nodeTitle,
        String nodeUrl,
        String nodeImage,

        // JsonNode nodeWeights,
        Integer nodeWeightsLeft,
        Integer nodeWeightsRight,
        
        String stance,

        String premiseId,
        String premiseTitle,
        String premiseText,
        String premiseAuthor,
        Integer premiseLikeCount,
        String premiseLikeUsers,
        Integer premiseCommentCount,
        Integer numberOfProArguments,
        Integer numberOfConArguments
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
    }
}
