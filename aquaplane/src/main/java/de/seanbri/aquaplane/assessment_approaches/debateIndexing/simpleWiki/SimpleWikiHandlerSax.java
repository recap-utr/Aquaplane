package de.seanbri.aquaplane.assessment_approaches.debateIndexing.simpleWiki;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.file.Paths;

import static de.seanbri.aquaplane.assessment_approaches.debateIndexing.simpleWiki.Indexer_SimpleWiki.TEXT;
import static de.seanbri.aquaplane.assessment_approaches.debateIndexing.simpleWiki.Indexer_SimpleWiki.TITLE;

public class SimpleWikiHandlerSax extends DefaultHandler {


    private final StringBuilder currentValue = new StringBuilder();
    PrintWriter printWriter;
    static int cnt = 0;


    @Override
    public void startDocument() {
    }

    @Override
    public void endDocument() {

    }


    @Override
    public void startElement(
            String uri,
            String localName,
            String qName,
            Attributes attributes) {
        currentValue.setLength(0);
    }

    @Override
    public void endElement(String uri,
                           String localName,
                           String qName) {

        if (qName.equalsIgnoreCase(TITLE)) {
            try {
                printWriter = new PrintWriter(
                        String.valueOf(
                                Paths.get("raw_data", "tmp-simplewiki-folder",
                                        String.valueOf(cnt++)
                                )
                        )
                );
                printWriter.println(currentValue); // title in first line
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        if (qName.equalsIgnoreCase(TEXT)) {
            printWriter.println(currentValue);
            printWriter.flush();
            printWriter.close();
        }
    }


    @Override
    public void characters(char[] ch, int start, int length) {
        currentValue.append(ch, start, length);

    }
}
