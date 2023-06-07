package de.seanbri.aquaplane.assessment_approaches.debateIndexing.simpleWiki;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;


import static de.seanbri.aquaplane.assessment_approaches.debateIndexing.Utils.isDirectoryEmpty;


public class Indexer_SimpleWiki {


    final static String TITLE = "title";
    final static String TEXT = "text";


    static Analyzer analyzer;
    static Path pathToIndex = Paths.get("src/main/resources/indexes", "index_simpleWiki");
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


    static void index () {
        try {
            if (!isDirectoryEmpty(pathToIndex)) {
                return;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        try (Stream<Path> walk = Files.walk(Parser_SimpleWiki.pathToTmpSimpleWiki)) {
            List<Path> paths = walk.filter(Files::isRegularFile).toList();

            for (Path pathToFile : paths) {
                String[] contents = Files.readString(pathToFile).split("\n", 2); // https://stackoverflow.com/a/23756541
                String title = contents[0];
                String text = contents[1];

                Document document = new Document();
                document.add(new TextField(TITLE, title, Field.Store.YES));
                document.add(new TextField(TEXT, text, Field.Store.YES));
                indexWriter.addDocument(document);
            }

            indexWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
