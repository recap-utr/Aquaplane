import pandas as pd
import os


def find_majority(decisions):
    decisions = decisions[decisions != 0]   # remove all zeros (no decision possible)
    candidate = find_majority_candidate(decisions)
    return check_majority_candidate(decisions, candidate)


def find_majority_candidate(decisions):
    count = 0
    candidate = -1

    for decision in decisions:
        if count == 0:
            candidate = decision
        
        count += 1 if decision == candidate else -1

        return candidate


def check_majority_candidate(decisions, candidate):
    count = 0

    for decision in decisions:
        if decision == candidate:
            count += 1

    if count > len(decisions)/2:
        return candidate
    
    return 0


def decision_is_majority_of_decisions(decision, decisions):
    majority = find_majority(decisions)
    return decision == majority


def frequency_of_match_with_majority_element(df, derived_dimension, dimensions):

    number_of_instances = df[derived_dimension].size
    
    majority_elements = df[dimensions].apply(find_majority, axis=1)
    match_with_majority_element = df[derived_dimension] == majority_elements

    frequency = df[match_with_majority_element][derived_dimension].size / number_of_instances

    return frequency


if __name__ == '__main__':

    df_evaluate = pd.read_csv(
        os.path.join('data', 'evaluation', 'ukp-convarg-dagstuhl-argquality-combined.csv'),
        encoding="latin-1",
        sep="\t",
        index_col='#id'
    )

    higher_level_dimensions = ['cogency', 'effectiveness', 'reasonableness']
    cogency_subdimensions = ['local acceptability', 'local relevance', 'sufficiency']
    effectiveness_subdimensions = ['credibility', 'emotional appeal', 'clarity', 'appropriateness', 'arrangement']
    reasonableness_subdimensions = ['global acceptability', 'global relevance', 'global sufficiency']
    all_dimensions = ['overall quality'] + higher_level_dimensions + cogency_subdimensions + effectiveness_subdimensions + reasonableness_subdimensions

    dim_frequency = {}
    frequency_more_convincing = frequency_of_match_with_majority_element(df=df_evaluate, derived_dimension='more_convincing', dimensions=higher_level_dimensions)
    dim_frequency['more_convincing'] = frequency_more_convincing

    frequency_overall_quality = frequency_of_match_with_majority_element(df=df_evaluate, derived_dimension='overall quality', dimensions=higher_level_dimensions)
    dim_frequency['overall quality'] = frequency_overall_quality

    frequency_cogency = frequency_of_match_with_majority_element(df=df_evaluate, derived_dimension='cogency', dimensions=cogency_subdimensions)
    dim_frequency['cogency'] = frequency_cogency

    frequency_effectiveness = frequency_of_match_with_majority_element(df=df_evaluate, derived_dimension='effectiveness', dimensions=effectiveness_subdimensions)
    dim_frequency['effectiveness'] = frequency_effectiveness

    frequency_reasonableness = frequency_of_match_with_majority_element(df=df_evaluate, derived_dimension='reasonableness', dimensions=reasonableness_subdimensions)
    dim_frequency['reasonableness'] = frequency_reasonableness


    frequency_overall_quality_all = frequency_of_match_with_majority_element(df=df_evaluate, derived_dimension='overall quality', dimensions=higher_level_dimensions + cogency_subdimensions + effectiveness_subdimensions + reasonableness_subdimensions)
    dim_frequency['overall quality_all_dimensions'] = frequency_overall_quality_all

    print(dim_frequency)

    more_convincing_frequency = {}
    frequency_more_convincing_all = frequency_of_match_with_majority_element(df=df_evaluate, derived_dimension='more_convincing', dimensions=all_dimensions)
    more_convincing_frequency['more_convincing_all_dimensions'] = frequency_more_convincing_all

    frequency_more_convincing_effectiveness = frequency_of_match_with_majority_element(df=df_evaluate, derived_dimension='more_convincing', dimensions=['effectiveness'] + effectiveness_subdimensions)
    more_convincing_frequency['more_convincing_effectiveness'] = frequency_more_convincing_effectiveness

    frequency_more_convincing_cogency = frequency_of_match_with_majority_element(df=df_evaluate, derived_dimension='more_convincing', dimensions=['cogency'] + cogency_subdimensions)
    more_convincing_frequency['more_convincing_cogency'] = frequency_more_convincing_cogency

    frequency_more_convincing_reasonableness = frequency_of_match_with_majority_element(df=df_evaluate, derived_dimension='more_convincing', dimensions=['reasonableness'] + reasonableness_subdimensions)
    more_convincing_frequency['more_convincing_reasonableness'] = frequency_more_convincing_reasonableness

    print(more_convincing_frequency)