package de.seanbri.aquaplane.utility;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUtility {

    // https://gist.github.com/dperini/729294
    private static final String URLREGEX =
            "(?:(?:(?:https?|ftp):)?\\/\\/)(?:\\S+(?::\\S*)?@)?(?:" +
            "(?!(?:10|127)(?:\\.\\d{1,3}){3})(?!(?:169\\.254|192\\.168)(?:\\.\\d{1,3}){2})" +
            "(?!172\\.(?:1[6-9]|2\\d|3[0-1])(?:\\.\\d{1,3}){2})(?:[1-9]\\d?|1\\d\\d|2[01]\\d|22[0-3])" +
            "(?:\\.(?:1?\\d{1,2}|2[0-4]\\d|25[0-5])){2}(?:\\.(?:[1-9]\\d?|1\\d\\d|2[0-4]\\d|25[0-4]))" +
            "|(?:(?:[a-z0-9\\u00a1-\\uffff][a-z0-9\\u00a1-\\uffff_-]{0,62})?[a-z0-9\\u00a1-\\uffff]\\." +
            ")+(?:[a-z\\u00a1-\\uffff]{2,}\\.?))(?::\\d{2,5})?(?:[/?#]\\S*)?";

    private static final String ONLYWORDSREGEX = "[^a-zA-Z0-9]";


    public static String removeURLs(String text) {
        Pattern pattern = Pattern.compile(URLREGEX, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);

        while(matcher.find()) {
            String url = matcher.group();
            url = url.replaceAll("[()!?.,:;{}\\[\\]]+$", "");   // remove punctuation from text at the end of url
            text = text.replace(url, "");
        }

        return text;
    }

    public static ArrayList<String> tokenize(String string){
        StringReader reader = new StringReader(string);
        PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "");

        ArrayList<String> tokens = new ArrayList<String>();
        while (tokenizer.hasNext()) {
            CoreLabel label = tokenizer.next();
            tokens.add(label.word());
        }

        return tokens;
    }

    public static ArrayList<String> onlyWordTokens(ArrayList<String> tokens){
        ArrayList<String> tokensOnlyWords = new ArrayList<String>();
        for (String token : tokens) {
            String cleanedToken = token.replaceAll(ONLYWORDSREGEX, "");
            if (!cleanedToken.equals("")) {
                tokensOnlyWords.add(token);
            }
        }

        return tokensOnlyWords;
    }

    public static ArrayList<String> lowerCaseTokens(ArrayList<String> tokens){
        ArrayList<String> lowerCaseTokens = new ArrayList<String>();
        for (String token : tokens) {
            lowerCaseTokens.add(token.toLowerCase());
        }

        return lowerCaseTokens;
    }

}
