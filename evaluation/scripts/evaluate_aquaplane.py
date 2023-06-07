import pandas as pd
import numpy as np
import os

from sklearn.metrics import accuracy_score, precision_recall_fscore_support


if __name__ == '__main__':

    df_evaluate = pd.read_csv(
        os.path.join('data', 'evaluation', 'ukp-convarg-dagstuhl-argquality-combined.csv'),
        encoding="latin-1",
        sep="\t",
        index_col='#id'
    )
    df_evaluate.sort_index(inplace=True)

    df_aquaplane = pd.read_csv(
        os.path.join('data', 'aquaplane', 'aquaplane-results.csv'),
        encoding="latin-1",
        sep="\t",
        index_col='#id'
    )
    df_aquaplane.sort_index(inplace=True)


    # Compute the accuracy, precision, recall and f-score for every dimension
    no_dimension_columns = ['#id', "a1", "a2", 'claim']
    dimension_columns = df_evaluate.columns.difference(no_dimension_columns)

    dim_metrics = {}
    dim_metrics['metric'] = ['accuracy', 'baseline_accuracy', 'precision', 'baseline_precision', 'recall', 'baseline_recall', 'f1', 'baseline_f1']
    for dim_col in dimension_columns:
        
        labels = df_evaluate[dim_col].values

        # Baseline: classifier that predicts the most frequent decision
        most_frequent_decision = df_evaluate[dim_col].mode()[0]
        baseline_predictions = np.full(985, most_frequent_decision)

        baseline_accuracy = accuracy_score(labels, baseline_predictions)

        baseline_precision, baseline_recall, baseline_fscore, baseline_support = precision_recall_fscore_support(labels, baseline_predictions, average='macro')
        
        # Compute metrics for aquaplane
        predictions = df_aquaplane[dim_col].values

        accuracy = accuracy_score(labels, predictions)

        precision, recall, fscore, support = precision_recall_fscore_support(labels, predictions, average='macro')

        dim_metrics[dim_col] = [accuracy, baseline_accuracy, precision, baseline_precision, recall, baseline_recall, fscore, baseline_fscore]
    
    df = pd.DataFrame(data=dim_metrics)
    df = df.set_index('metric')

    df.to_csv(
        os.path.join('data', 'metrics', 'dimension_metrics_with_baseline.csv'),
        sep="\t"
    )
