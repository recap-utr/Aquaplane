package de.seanbri.aquaplane.assessment_approaches.debateIndexing.debateOrg;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static de.seanbri.aquaplane.assessment_approaches.debateIndexing.Utils.isDirectoryEmpty;
import static de.seanbri.aquaplane.assessment_approaches.debateIndexing.debateOrg.Indexer_DebateOrg.pathToIndex;

public class Parser_DebateOrg {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static List<Argument_DebateOrg> parseJSON(Path pathToRawJSONFile) {

        try {
            if (!isDirectoryEmpty(pathToIndex)) {
                return null;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            List<Argument_DebateOrg> arguments_debateOrg = new ArrayList<>();

            JsonNode root = mapper.readTree(new File(String.valueOf(pathToRawJSONFile)));
            for (JsonNode node : root) {

                JsonNode nodeId = node.get("id");
                JsonNode nodeTitle = node.get("title");
                JsonNode nodeUrl = node.get("url");
                JsonNode nodeImage = node.get("image");

                JsonNode nodeWeights = node.get("weights");
                JsonNode nodeWeightsLeft = nodeWeights.get(0);
                JsonNode nodeWeightsRight = nodeWeights.get(1);

                int numberOfProArguments = node.get("pros").size();
                int numberOfConArguments = node.get("cons").size();

                for (String stance : new String[]{"pros", "cons"}) {
                    JsonNode premises = node.get(stance);
                    for (JsonNode premise : premises) {
                        JsonNode premiseId = premise.get("id");
                        JsonNode premiseTitle = premise.get("title");
                        JsonNode premiseText = premise.get("text");
                        JsonNode premiseAuthor = premise.get("author");
                        JsonNode premiseLikeCount = premise.get("likeCount");
                        JsonNode premiseLikeUsers = premise.get("likeUsers");
                        JsonNode premiseCommentCount = premise.get("commentCount");

                        arguments_debateOrg.add(
                                new Argument_DebateOrg(
                                        nodeId.asText(),
                                        nodeTitle.asText(),
                                        nodeUrl.asText(),
                                        nodeImage.asText(),

                                        nodeWeightsLeft.asInt(),
                                        nodeWeightsRight.asInt(),

                                        stance.equals("pros") ? "pro" : stance.equals("cons") ? "con" : null,

                                        premiseId.asText(),
                                        premiseTitle.asText(),
                                        premiseText.asText(),
                                        premiseAuthor.asText(),
                                        premiseLikeCount.asInt(),
                                        premiseLikeUsers.asText(),
                                        premiseCommentCount.asInt(),

                                        numberOfProArguments,
                                        numberOfConArguments
                                )
                        );
                    }
                }
            }

            return arguments_debateOrg;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
