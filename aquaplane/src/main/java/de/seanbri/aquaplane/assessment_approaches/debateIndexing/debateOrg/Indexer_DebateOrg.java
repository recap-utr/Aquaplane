package de.seanbri.aquaplane.assessment_approaches.debateIndexing.debateOrg;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static de.seanbri.aquaplane.assessment_approaches.debateIndexing.Utils.isDirectoryEmpty;

public class Indexer_DebateOrg {


    static final String NODE_ID = "nodeId";
    static final String NODE_TITLE = "nodeTitle";
    static final String NODE_URL = "nodeUrl";
    static final String NODE_IMAGE = "nodeImage";
    static final String NODE_WEIGHTS_LEFT = "nodeWeightsLeft";
    static final String NODE_WEIGHTS_RIGHT = "nodeWeightsRight";
    static final String STANCE = "stance";
    static final String PREMISE_ID = "premiseId";
    static final String PREMISE_TITLE = "premiseTitle";
    static final String PREMISE_TEXT = "premiseText";
    static final String PREMISE_AUTHOR = "premiseAuthor";
    static final String PREMISE_LIKE_COUNT = "premiseLikeCount";
    static final String PREMISE_LIKE_USERS = "premiseLikeUsers";
    static final String PREMISE_COMMENT_COUNT = "premiseCommentCount";
    static final String NUMBER_OF_PRO_ARGUMENTS = "numberOfProArguments";
    static final String NUMBER_OF_CON_ARGUMENTS = "numberOfConArguments";



    static Analyzer analyzer;
    static Path pathToIndex = Paths.get("src/main/resources/indexes", "index_debateOrg");
    static Directory index;
    static IndexWriterConfig indexWriterConfig;
    static IndexWriter indexWriter;


    static {
        try {
            analyzer = new StandardAnalyzer();
            index = FSDirectory.open(pathToIndex);
            indexWriterConfig = new IndexWriterConfig(analyzer);
            indexWriter = new IndexWriter(index, indexWriterConfig);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static void index (List<Argument_DebateOrg> arguments_debateOrg) {

        try {
            if (!isDirectoryEmpty(pathToIndex)) {
                return;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            for (Argument_DebateOrg argument : arguments_debateOrg) {

                Document document = new Document();

                document.add(new StringField(NODE_ID, argument.nodeId, Field.Store.YES));          // immutable string, e.g. id
                document.add(new TextField(NODE_TITLE, argument.nodeTitle, Field.Store.YES));      // mutable String
                document.add(new StringField(NODE_URL, argument.nodeUrl, Field.Store.YES));
                document.add(new StringField(NODE_IMAGE, argument.nodeImage, Field.Store.YES));
                document.add(new TextField(NODE_WEIGHTS_LEFT, String.valueOf(argument.nodeWeightsLeft), Field.Store.YES));
                document.add(new TextField(NODE_WEIGHTS_RIGHT, String.valueOf(argument.nodeWeightsRight), Field.Store.YES));
                document.add(new TextField(STANCE, argument.stance, Field.Store.YES));
                document.add(new StringField(PREMISE_ID, argument.premiseId, Field.Store.YES));
                document.add(new TextField(PREMISE_TITLE, argument.premiseTitle, Field.Store.YES));
                document.add(new TextField(PREMISE_TEXT, argument.premiseText, Field.Store.YES));
                document.add(new StringField(PREMISE_AUTHOR, argument.premiseAuthor, Field.Store.YES));
                document.add(new TextField(PREMISE_LIKE_COUNT, String.valueOf(argument.premiseLikeCount), Field.Store.YES));
                document.add(new StringField(PREMISE_LIKE_USERS, argument.premiseLikeUsers, Field.Store.YES));
                document.add(new TextField(PREMISE_COMMENT_COUNT, String.valueOf(argument.premiseCommentCount), Field.Store.YES));
                document.add(new StringField(NUMBER_OF_PRO_ARGUMENTS, String.valueOf(argument.numberOfProArguments), Field.Store.YES));
                document.add(new StringField(NUMBER_OF_CON_ARGUMENTS, String.valueOf(argument.numberOfConArguments), Field.Store.YES));

                indexWriter.addDocument(document);
            }

            indexWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
