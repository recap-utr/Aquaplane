import pandas as pd
import glob
import os


def preprocess_dagstuhl(dagstuhl_path):

    df_dagstuhl = pd.read_csv(
        dagstuhl_path,
        encoding="latin-1",
        sep="\t",
        index_col='#id'
    )

    df_dagstuhl = makeClaimColumn(df_dagstuhl)
    df_dagstuhl = medianAnnotatorScores(df_dagstuhl)

    df_dagstuhl.to_csv(
        os.path.join('data', 'preprocessed', 'dagstuhl-argquality-preprocessed.csv'),
        sep="\t"
    )


def makeClaimColumn(df_dagstuhl):
    df_dagstuhl['claim'] = df_dagstuhl['issue'].replace('-', " ", regex=True) + ": " + df_dagstuhl['stance'].replace('-', " ", regex=True)
    df_dagstuhl = df_dagstuhl.drop(columns=['issue', 'stance'])

    return df_dagstuhl


def medianAnnotatorScores(df_dagstuhl):

    # No Score is given for arguments that were classified as non-argumentative ('n')
    df_dagstuhl = df_dagstuhl[df_dagstuhl.argumentative != 'n']

    # Drop unnecessary columns
    df_dagstuhl = df_dagstuhl.drop(columns=['annotator', 'argumentative'])

    groupColumns = ['#id', 'claim', 'argument']
    dimensionColumns = df_dagstuhl.columns.difference(groupColumns)

    # Delete (Low), (Average), (High) annotation in dimension columns; 'Cannot judge' mapped to 2; Casted values to int
    df_dagstuhl[dimensionColumns] = df_dagstuhl[dimensionColumns].replace('[^0-9]+', '', regex=True).replace('', 2, regex=True).astype(int)

    # Group by groupColumns and compute median of annotations for each dimension
    df_dagstuhl = df_dagstuhl.groupby(groupColumns)[dimensionColumns].median(numeric_only=True)
    df_dagstuhl = df_dagstuhl.reset_index()
    df_dagstuhl = df_dagstuhl.set_index('#id')
    
    return df_dagstuhl


def preprocess_ukp(ukp_folder_path):

    # Get all csv files from the folder
    csv_files = glob.glob(
        os.path.join(ukp_folder_path, '*.csv')
    )

    df_ukp = read_to_dataframe(csv_files)

    # Rename column and map label to numeric values
    df_ukp['more_convincing'] = df_ukp['label'].map({
        "a1": 1,
        "a2": 2
    })
    df_ukp = df_ukp.drop(columns=['label'])

    df_ukp.to_csv(
        os.path.join('data', 'preprocessed', 'ukp-convarg-preprocessed.csv'),
        sep="\t"
    )


def read_to_dataframe(csv_files):
    df_ukp = pd.DataFrame()
    for csv_file in csv_files:

        df = pd.read_csv(
            csv_file,
            sep="\t",
            index_col='#id'
        )

        df_ukp = pd.concat([df_ukp, df])

    return df_ukp


if __name__ == '__main__':

    preprocess_dagstuhl(
        os.path.join('data', 'raw', 'Dagstuhl-15512-ArgQuality', 'dagstuhl-15512-argquality-corpus-annotated.csv')
    )

    preprocess_ukp(
        os.path.join('data', 'raw', 'UKPConvArg1Strict-CSV'),
    )
