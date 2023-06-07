package de.seanbri.aquaplane.argumentQuality;

import de.seanbri.aquaplane.assessment_approaches.AARecords;
import de.seanbri.aquaplane.assessment_approaches.AssessmentApproach;
import de.seanbri.aquaplane.assessment_approaches.ad_hominem.AdHominem;
import de.seanbri.aquaplane.assessment_approaches.all_caps.AllCaps;
import de.seanbri.aquaplane.assessment_approaches.debateIndexing.debateOrg.Searcher_DebateOrg;
import de.seanbri.aquaplane.assessment_approaches.debateIndexing.simpleWiki.Searcher_SimpleWiki;
import de.seanbri.aquaplane.assessment_approaches.excessive_punctuation.ExcessivePunctuation;
import de.seanbri.aquaplane.assessment_approaches.fact_checking.FactChecking;
import de.seanbri.aquaplane.assessment_approaches.profanity.Profanity;
import de.seanbri.aquaplane.assessment_approaches.spell_checking.SpellChecker;
import de.seanbri.aquaplane.assessment_approaches.styleexplorer.models.*;
import de.seanbri.aquaplane.assessment_approaches.url_sources.URLSources;
import de.seanbri.aquaplane.assessment_approaches.wiktionary_lists.WiktionaryLists;

import java.util.*;

public class AssessmentApproachProcessor {

    public static AARecords compute(String argument, String claim) {
        HashSet<AssessmentApproach> assApps = getAllAssessmentApproaches();

        AARecords records = new AARecords();
        for (AssessmentApproach assApp : assApps) {
            Record record = assApp.computeRecord(argument, claim);
            records.put(assApp, record);
        }

        return records;
    }

    /**
     * The HashSet can hold Objects that implement the "AssessmentApproach" interface
     */
    public static HashSet<AssessmentApproach> getAllAssessmentApproaches() {
        HashSet<AssessmentApproach> assessmentApproaches = new HashSet<>();

        assessmentApproaches.add(new FactChecking());
        assessmentApproaches.add(new SpellChecker());
        assessmentApproaches.add(new WiktionaryLists());
        assessmentApproaches.add(new Searcher_SimpleWiki());
        assessmentApproaches.add(new URLSources());
        assessmentApproaches.add(new AllCaps());
        assessmentApproaches.add(new ExcessivePunctuation());
        assessmentApproaches.add(new Profanity());
        assessmentApproaches.add(new Searcher_DebateOrg());
        assessmentApproaches.add(new AdHominem());
        assessmentApproaches.add(new StyleExplorer_Readability());
        assessmentApproaches.add(new StyleExplorer_StyleComplexity());
        assessmentApproaches.add(new StyleExplorer_VocabularyRichness());
        assessmentApproaches.add(new StyleExplorer_VocabularyDiversity());
        assessmentApproaches.add(new StyleExplorer_Errors());

        return assessmentApproaches;
    }

}
