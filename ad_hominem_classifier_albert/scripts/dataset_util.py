import os
import pandas as pd
from sklearn.model_selection import train_test_split

rule2label = {
    0: 0,
    1: 0,
    2: 1,     # rude or hostile = ad hominem argument
    5: 0,
}

def preprocessDataset(path_to_dataset):

    df_dataset = pd.read_json(path_to_dataset, lines=True)

    df_dataset["is_ad_hominem"] = df_dataset["violated_rule"].map(rule2label)
    df_dataset = df_dataset[["is_ad_hominem", "body"]]
    print(df_dataset.head())

    return df_dataset


def splitDataset(df_dataset):

    # Split the data in 80:10:10 for train:valid:test dataset
    df_train, df_rem = train_test_split(df_dataset, train_size=0.8)

    df_val, df_test = train_test_split(df_rem, test_size=0.5)

    df_train.reset_index(drop=True, inplace=True)
    df_val.reset_index(drop=True, inplace=True)
    df_test.reset_index(drop=True, inplace=True)

    writeSplittedDatasetsToFiles(df_train=df_train, df_val=df_val, df_test=df_test)

    return df_train, df_val, df_test


def writeSplittedDatasetsToFiles(df_train, df_val, df_test):

    df_train.to_json(
        os.path.join('..', 'data_splitted', 'train.json'),
        orient='records',
        lines=True
    )

    df_val.to_json(
        os.path.join('..', 'data_splitted', 'val.json'),
        orient='records',
        lines=True
    )

    df_test.to_json(
        os.path.join('..', 'data_splitted', 'test.json'),
        orient='records',
        lines=True
    )


def loadDatasets(path_to_train, path_to_val, path_to_test):

    df_train = pd.read_json(path_to_train, lines=True)
    df_val = pd.read_json(path_to_val, lines=True)
    df_test = pd.read_json(path_to_test, lines=True)

    return df_train, df_val, df_test
