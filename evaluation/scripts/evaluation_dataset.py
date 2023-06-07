import pandas as pd
import os


def create_evaluation_dataset(ukp_preprocessed_path, dagstuhl_preprocessed_path):

    global df_ukp 
    df_ukp = pd.read_csv(
        ukp_preprocessed_path,
        encoding="latin-1",
        sep="\t"
    )

    global df_dagstuhl 
    df_dagstuhl = pd.read_csv(
        dagstuhl_preprocessed_path,
        encoding="latin-1",
        sep="\t"
    )

    global df_evaluation 
    df_evaluation = pd.DataFrame()
    for index, row in df_ukp.iterrows():
        id_a1, id_a2 = split(row['#id'])

        if(dagstuhl_contains(id_a1) & dagstuhl_contains(id_a2)):
            a1 = df_dagstuhl.loc[df_dagstuhl['#id'] == id_a1]
            a2 = df_dagstuhl.loc[df_dagstuhl['#id'] == id_a2]

            row['claim'] = a1.iloc[0]['claim']

            row['a1'] = row['a1'].replace('<br/>', '')
            row['a2'] = row['a2'].replace('<br/>', '')

            dict_dim_decisions = compare_dimension_scores(a1.iloc[0], a2.iloc[0])
            for dim, decision in dict_dim_decisions.items():
                row[dim] = decision

            df_evaluation = df_evaluation.append(row, ignore_index=True)

    df_evaluation = df_evaluation.set_index('#id')
    df_evaluation.to_csv(
        os.path.join('data', 'evaluation', 'ukp-convarg-dagstuhl-argquality-combined.csv'),
        sep="\t",
        columns=['a1', 'a2', 'claim', 'more_convincing', 'overall quality', 'cogency', 'effectiveness', 'reasonableness', 'local acceptability', 'local relevance', 'sufficiency', 'credibility', 'emotional appeal', 'clarity', 'appropriateness', 'arrangement', 'global acceptability', 'global relevance', 'global sufficiency']
    )


def split(combined_id):
    id_list = combined_id.split("_", 1)

    return id_list[0], id_list[1]


def dagstuhl_contains(id):
    return id in df_dagstuhl['#id'].unique()


def compare_dimension_scores(a1, a2):
    dimension_columns = df_dagstuhl.columns.difference(['#id', 'claim', 'argument'])

    decisions = {}
    for dim in dimension_columns:
        decision = get_decision(a1[dim], a2[dim])
        decisions[dim] = decision
    
    return decisions


def get_decision(score_a1, score_a2):
    return 1 if score_a1 > score_a2 else 2 if score_a2 > score_a1 else 0


if __name__ == '__main__':

    create_evaluation_dataset(
        ukp_preprocessed_path = os.path.join('data', 'preprocessed', 'ukp-convarg-preprocessed.csv'),
        dagstuhl_preprocessed_path = os.path.join('data', 'preprocessed', 'dagstuhl-argquality-preprocessed.csv')
    )
