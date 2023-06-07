package de.seanbri.aquaplane.assessment_approaches.url_sources;

import de.seanbri.aquaplane.argumentQuality.comparison.Comparison;
import de.seanbri.aquaplane.assessment_approaches.AssessmentApproach;
import de.seanbri.aquaplane.utility.DecisionMaking;
import edu.stanford.nlp.util.StringUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class URLSources implements AssessmentApproach {

    // https://gist.github.com/dperini/729294
    private static final String URLRegex =
            "(?:(?:(?:https?|ftp):)?\\/\\/)(?:\\S+(?::\\S*)?@)?(?:" +
            "(?!(?:10|127)(?:\\.\\d{1,3}){3})(?!(?:169\\.254|192\\.168)(?:\\.\\d{1,3}){2})" +
            "(?!172\\.(?:1[6-9]|2\\d|3[0-1])(?:\\.\\d{1,3}){2})(?:[1-9]\\d?|1\\d\\d|2[01]\\d|22[0-3])" +
            "(?:\\.(?:1?\\d{1,2}|2[0-4]\\d|25[0-5])){2}(?:\\.(?:[1-9]\\d?|1\\d\\d|2[0-4]\\d|25[0-4]))" +
            "|(?:(?:[a-z0-9\\u00a1-\\uffff][a-z0-9\\u00a1-\\uffff_-]{0,62})?[a-z0-9\\u00a1-\\uffff]\\." +
            ")+(?:[a-z\\u00a1-\\uffff]{2,}\\.?))(?::\\d{2,5})?(?:[/?#]\\S*)?";

    private static final String URLSOURCES = "URLSources";
    private static final String NUMOFURLS = "numOfUrls";


    public record URLSourcesRecord(
            List<String> urls,
            int numOfUrls
    ) {}


    @Override
    public String getName() {
        return URLSOURCES;
    }

    @Override
    public URLSourcesRecord computeRecord(String argument, String claim) {
        List<String> urls = new ArrayList<String>();

        Pattern pattern = Pattern.compile(URLRegex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(argument);
        while(matcher.find()) {
            String url = matcher.group();
            url = url.replaceAll("[()!?.,:;{}\\[\\]]+$", "");   // remove punctuation from text at the end of url
            if (isValidUrl(url)) {
                urls.add(url);
            }
        }

        return new URLSourcesRecord(urls, urls.size());
    }

    private boolean isValidUrl(String url) {
        try {
            URL urlObj = new URL(url);
            urlObj.toURI();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Comparison compare(Record r1, Record r2) {
        Map<String, Integer> feature_decision = new HashMap<>();

        URLSourcesRecord ur1 = (URLSourcesRecord) r1;
        URLSourcesRecord ur2 = (URLSourcesRecord) r2;

        int decision = DecisionMaking.decisionHigh(
                ur1.numOfUrls,
                ur2.numOfUrls
        );

        feature_decision.put(
                NUMOFURLS,
                decision
        );

        return new Comparison(ur1, ur2, decision, feature_decision);
    }

    @Override
    public List<String> explain(Comparison comparison) {
        List<String> explanations = new ArrayList<>();
        if (comparison.getDecision() == 0) {        // Only generate explanations if decision is unequivocal
            return explanations;
        }

        String template =
                "Argument %d generally gives more sources. The following sources were provided:%n";
        String explanation = String.format(template,
                comparison.getDecision()
        );
        URLSourcesRecord URLSourcesRecord = (URLSourcesRecord) comparison.getObjectOfDecision();
        String urls = StringUtils.join(URLSourcesRecord.urls(), ",\n");
        explanation = explanation + urls;
        explanations.add(explanation);

        return explanations;
    }

}
