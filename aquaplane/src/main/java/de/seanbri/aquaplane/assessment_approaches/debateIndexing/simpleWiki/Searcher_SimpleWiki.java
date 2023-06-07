package de.seanbri.aquaplane.assessment_approaches.debateIndexing.simpleWiki;

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

import static de.seanbri.aquaplane.assessment_approaches.debateIndexing.simpleWiki.Indexer_SimpleWiki.TEXT;
import static de.seanbri.aquaplane.assessment_approaches.debateIndexing.simpleWiki.Indexer_SimpleWiki.TITLE;

public class Searcher_SimpleWiki implements AssessmentApproach {

    private static final String SIMPLEWIKI_SCORE = "SimpleWiki_Score";

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


    public record SimpleWikiRecord(
            SimpleWikiSearchResult simpleWikiSearchResult
    ) {}


    public static List<SimpleWikiSearchResult> search(String queryStringClaim, String queryStringPremise, int maxHitSize) {
        initializeReaderAndSearcher();
        try {
            List<Term> termList = getTermListFrom(queryStringClaim, queryStringPremise);
            Query bm25FQuery = buildBM25FQueryFrom(termList);

            TopDocs topDocs = indexSearcher.search(bm25FQuery, maxHitSize);
            ScoreDoc[] hits = topDocs.scoreDocs;
            List<SimpleWikiSearchResult> swResults = getListOfSimpleWikiSearchResultsFromHits(queryStringClaim, queryStringPremise, hits);

            indexReader.close();

            return swResults;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void initializeReaderAndSearcher() {
        try {
            indexReader = DirectoryReader.open(Indexer_SimpleWiki.index);
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
            termList.add(new Term(TEXT, term));
        }
        for (String term : queryPremiseTerms) {
            if (stopWords.contains(term)) {
                continue;
            }
            termList.add(new Term(TEXT, term));
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

    private static List<SimpleWikiSearchResult> getListOfSimpleWikiSearchResultsFromHits(
            String queryStringClaim,
            String queryStringPremise,
            ScoreDoc[] hits) throws IOException
    {
        List<SimpleWikiSearchResult> swResults = new ArrayList<>();
        for (ScoreDoc hit : hits) {
            int docId = hit.doc;
            Document document = indexSearcher.doc(docId);

            SimpleWikiSearchResult swResult = new SimpleWikiSearchResult(
                    queryStringClaim,
                    queryStringPremise,
                    document.get(TITLE),
                    document.get(TEXT),
                    hit.score
            );
            swResults.add(swResult);
        }

        return swResults;
    }

    @Override
    public String getName() {
        return SIMPLEWIKI_SCORE;
    }

    @Override
    public SimpleWikiRecord computeRecord(String argument, String claim) {
        List<SimpleWikiSearchResult> swResults = search(claim, argument, 1);

        return new SimpleWikiRecord(swResults.get(0));
    }

    @Override
    public Comparison compare(Record r1, Record r2) {
        Map<String, Integer> feature_decision = new HashMap<>();

        SimpleWikiRecord sr1 = (SimpleWikiRecord) r1;
        SimpleWikiRecord sr2 = (SimpleWikiRecord) r2;

        int decision = DecisionMaking.decisionHigh(
                sr1.simpleWikiSearchResult().getScore(),
                sr2.simpleWikiSearchResult().getScore()
        );
        feature_decision.put(
                "simpleWikiScore",
                decision
        );

        return new Comparison(sr1, sr2, decision, feature_decision);
    }

    @Override
    public List<String> explain(Comparison comparison) {
        List<String> explanations = new ArrayList<>();
        if (comparison.getDecision() == 0) {        // Only generate explanations if decision is unequivocal
            return explanations;
        }

        String template =
                "Argument %d is more relevant than argument %d. " +
                "This is because argument %d achieves a higher BM25F-Score, when searching within SimpleWiki" +
                ", which provides general knowledge about topics.";
        String explanation = String.format(template,
                comparison.getDecision(),
                comparison.getInvertedDecision(),
                comparison.getDecision()
        );
        explanations.add(explanation);

        return explanations;
    }
}
