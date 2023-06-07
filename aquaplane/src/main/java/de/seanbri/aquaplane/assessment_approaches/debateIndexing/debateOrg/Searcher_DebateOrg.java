package de.seanbri.aquaplane.assessment_approaches.debateIndexing.debateOrg;

import de.seanbri.aquaplane.argumentQuality.comparison.Comparison;
import de.seanbri.aquaplane.assessment_approaches.AssessmentApproach;
import de.seanbri.aquaplane.utility.DecisionMaking;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static de.seanbri.aquaplane.assessment_approaches.debateIndexing.debateOrg.Indexer_DebateOrg.*;

public class Searcher_DebateOrg implements AssessmentApproach {

    private static final String DEBATEORG_SCORE = "DebateOrg_Score";

    static IndexReader indexReader;
    static IndexSearcher indexSearcher;
    static List<String> stopWords;


    static {
        try {
            stopWords = Files.readAllLines(Paths.get("src/main/resources/debateIndexing", "stopwords"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public record DebateOrgRecord(
            DebateOrgSearchResult debateOrgSearchResult
    ) {}


    public static List<DebateOrgSearchResult> search(String queryStringClaim, String queryStringPremise, int maxHitSize) {
        initializeReaderAndSearcher();
        try {
            List<Term> termList = getTermListFrom(queryStringClaim, queryStringPremise);
            Query bm25FQuery = buildBM25FQueryFrom(termList);

            TopDocs topDocs = indexSearcher.search(bm25FQuery, maxHitSize);
            ScoreDoc[] hits = topDocs.scoreDocs;
            List<DebateOrgSearchResult> doResults = getListOfDebateOrgSearchResultsFromHits(hits);

            indexReader.close();

            return doResults;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void initializeReaderAndSearcher() {
        try {
            indexReader = DirectoryReader.open(Indexer_DebateOrg.index);
            indexSearcher = new IndexSearcher(indexReader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<Term> getTermListFrom(String queryStringClaim, String queryStringPremise) {
        String[] queryClaimTerms = queryStringClaim.split(" ");
        String[] queryPremiseTerms = queryStringPremise.split(" ");
        List<Term> termList = new ArrayList<>();
        for (String term : queryClaimTerms) {
            if (stopWords.contains(term)) {
                continue;
            }
            termList.add(new Term(NODE_TITLE, term));
        }
        for (String term : queryPremiseTerms) {
            if (stopWords.contains(term)) {
                continue;
            }
            termList.add(new Term(PREMISE_TITLE, term));
            termList.add(new Term(PREMISE_TEXT, term));
        }

        return termList;
    }

    private static Query buildBM25FQueryFrom(List<Term> termList) {
        // https://opensourceconnections.com/blog/2016/10/19/bm25f-in-lucene/
        BlendedTermQuery.Builder builder = new BlendedTermQuery.Builder();
        for (Term term : termList) {
            builder.add(term);
        }

        return builder.setRewriteMethod(BlendedTermQuery.BOOLEAN_REWRITE).build();
    }

    private static List<DebateOrgSearchResult> getListOfDebateOrgSearchResultsFromHits(
            ScoreDoc[] hits
    ) throws IOException
    {
        List<DebateOrgSearchResult> doResults = new ArrayList<>();
        for (ScoreDoc hit : hits) {
            int docId = hit.doc;
            Document document = indexSearcher.doc(docId);

            DebateOrgSearchResult doResult = new DebateOrgSearchResult(
                    document.get(NODE_ID),
                    document.get(NODE_TITLE),
                    document.get(NODE_URL),
                    document.get(NODE_IMAGE),
                    Integer.parseInt(document.get(NODE_WEIGHTS_LEFT)),
                    Integer.parseInt(document.get(NODE_WEIGHTS_RIGHT)),
                    document.get(STANCE),
                    document.get(PREMISE_ID),
                    document.get(PREMISE_TITLE),
                    document.get(PREMISE_TEXT),
                    document.get(PREMISE_AUTHOR),
                    Integer.parseInt(document.get(PREMISE_LIKE_COUNT)),
                    document.get(PREMISE_LIKE_USERS),
                    Integer.parseInt(document.get(PREMISE_COMMENT_COUNT)),
                    Integer.parseInt(document.get(NUMBER_OF_PRO_ARGUMENTS)),
                    Integer.parseInt(document.get(NUMBER_OF_CON_ARGUMENTS)),
                    hit.score
            );
            doResults.add(doResult);
        }

        return doResults;
    }

    @Override
    public String getName() {
        return DEBATEORG_SCORE;
    }

    @Override
    public DebateOrgRecord computeRecord(String argument, String claim) {
        List<DebateOrgSearchResult> doResults = search(claim, argument, 1);

        return new DebateOrgRecord(doResults.get(0));
    }

    @Override
    public Comparison compare(Record r1, Record r2) {
        Map<String, Integer> feature_decision = new HashMap<>();

        DebateOrgRecord dr1 = (DebateOrgRecord) r1;
        DebateOrgRecord dr2 = (DebateOrgRecord) r2;

        int decision = DecisionMaking.decisionHigh(
                dr1.debateOrgSearchResult().getScore(),
                dr2.debateOrgSearchResult().getScore()
        );
        feature_decision.put(
                "debateOrgScore",
                decision
        );

        return new Comparison(dr1, dr2, decision, feature_decision);
    }

    @Override
    public List<String> explain(Comparison comparison) {
        List<String> explanations = new ArrayList<>();
        if (comparison.getDecision() == 0) {        // Only generate explanations if decision is unequivocal
            return explanations;
        }

        String template =
                "Argument %d is more relevant than argument %d. " +
                "This is because argument %d achieves a higher BM25F-Score, when searching within the debate.org dataset" +
                ", which contains numerous arguments already given in debates.";
        String explanation = String.format(template,
                comparison.getDecision(),
                comparison.getInvertedDecision(),
                comparison.getDecision()
        );
        explanations.add(explanation);

        return explanations;
    }

}