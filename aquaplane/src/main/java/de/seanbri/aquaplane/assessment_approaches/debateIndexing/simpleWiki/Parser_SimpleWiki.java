package de.seanbri.aquaplane.assessment_approaches.debateIndexing.simpleWiki;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static de.seanbri.aquaplane.assessment_approaches.debateIndexing.Utils.isDirectoryEmpty;


// https://mkyong.com/java/how-to-read-xml-file-in-java-sax-parser/
public class Parser_SimpleWiki {

    static final Path pathToTmpSimpleWiki = Paths.get("raw_data", "tmp-simplewiki-folder");

    public static void parse(Path pathToFile) {

        try {
            if (!isDirectoryEmpty(pathToTmpSimpleWiki)) {
                return;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        try {
            Files.createDirectories(pathToTmpSimpleWiki);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        try {
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            saxParserFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            SAXParser saxParser = saxParserFactory.newSAXParser();
            saxParser.parse(String.valueOf(pathToFile), new SimpleWikiHandlerSax());

        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }

    }
}
