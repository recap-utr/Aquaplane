import pandas as pd
import json
import glob
import os


def preprocess_aquaplane_results(results_folder_path):

    # Get all json files from the folder
    json_files = glob.glob(
        os.path.join(results_folder_path, '*.json')
    )

    df_aquaplane = read_to_dataframe(json_files)
    df_aquaplane = df_aquaplane.reset_index(drop=True)
    df_aquaplane = df_aquaplane.set_index('#id')

    df_aquaplane.to_csv(
        os.path.join('data', 'aquaplane', 'aquaplane-results.csv'),
        sep="\t",
    )


def read_to_dataframe(json_files):
    df_aquaplane = pd.DataFrame()

    index = 0
    for json_file in json_files:
        base = os.path.basename(json_file) 
        filename = os.path.splitext(base)[0]   # filename is the #id
        f = open(json_file, encoding='latin-1')

        data = json.load(f)
        relevant_data = extract_relevant_data(data, filename)
        df_relevant_data = pd.DataFrame(relevant_data, index=[index])

        df_aquaplane = pd.concat([df_aquaplane, df_relevant_data])
        index += 1

    return df_aquaplane


def extract_relevant_data(data, id):
    data_relevant = {}

    data_relevant['#id'] = id
    data_relevant['a1'] = data['a1']
    data_relevant['a2'] = data['a2']
    data_relevant['claim'] = data['claim']
    data_relevant['more_convincing'] = data['decision']     # we derive the more convincing argument from the overall quality decision
    data_relevant['overall quality'] = data['decision']

    argument_quality = data['argumentQuality']
    data_relevant['cogency'] = argument_quality['Logic Quality']['decision']
    data_relevant['effectiveness'] = argument_quality['Rhetoric Quality']['decision']
    data_relevant['reasonableness'] = argument_quality['Dialectic Quality']['decision']

    logic_quality_subdimensions = argument_quality['Logic Quality']['subdimensions']
    data_relevant['local acceptability'] = logic_quality_subdimensions['Local Acceptability']['decision']
    data_relevant['local relevance'] = logic_quality_subdimensions['Local Relevance']['decision']
    data_relevant['sufficiency'] = logic_quality_subdimensions['Local Sufficiency']['decision']

    rhetoric_quality_subdimensions = argument_quality['Rhetoric Quality']['subdimensions']
    data_relevant['credibility'] = rhetoric_quality_subdimensions['Credibility']['decision']
    data_relevant['emotional appeal'] = rhetoric_quality_subdimensions['Emotional Appeal']['decision']
    data_relevant['clarity'] = rhetoric_quality_subdimensions['Clarity']['decision']
    data_relevant['appropriateness'] = rhetoric_quality_subdimensions['Appropriateness']['decision']
    data_relevant['arrangement'] = rhetoric_quality_subdimensions['Arrangement']['decision']

    dialectic_quality_subdimensions = argument_quality['Dialectic Quality']['subdimensions']
    data_relevant['global acceptability'] = dialectic_quality_subdimensions['Global Acceptability']['decision']
    data_relevant['global relevance'] = dialectic_quality_subdimensions['Global Relevance']['decision']
    data_relevant['global sufficiency'] = dialectic_quality_subdimensions['Global Sufficiency']['decision']

    return data_relevant


if __name__ == '__main__':

    preprocess_aquaplane_results(
        os.path.join('data', 'aquaplane', 'json-results'),
    )
