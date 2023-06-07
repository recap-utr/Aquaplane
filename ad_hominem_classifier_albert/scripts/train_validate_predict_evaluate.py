import os
from util import set_seed
from dataset_util import preprocessDataset, splitDataset
from customdataset import CustomDataset
from torch.utils.data import DataLoader
import torch
import torch.nn as nn
from sentenceclassifier import SentenceClassifier
from transformers import AdamW, get_linear_schedule_with_warmup
from train import train_bert
from prediction import test_prediction
import evaluate
import pandas as pd

if __name__ == '__main__':

    # Dataset

    df_dataset = preprocessDataset(
        os.path.join('..', 'data', 'exported-3621-sampled-positive-negative-ah-no-context.json'),
    )
    
    df_train, df_val, df_test = splitDataset(df_dataset)


    # Parameters

    bert_model = "albert-base-v2"
    freeze_bert = False 
    maxlen = 512  # maximum length of the tokenized input
    bs = 16  # batch size
    iters_to_accumulate = 2
    lr = 1e-5  # learning rate
    epochs = 4  # number of training epochs


    # Train and Validate

    # Set all seeds to make reproducible results
    set_seed(1)

    # Creating instances of training and validation set
    print("Reading training data...")
    train_set = CustomDataset(df_train, maxlen, bert_model)
    print("Reading validation data...")
    val_set = CustomDataset(df_val, maxlen, bert_model)

    # Creating instances of training and validation dataloaders
    train_loader = DataLoader(train_set, batch_size=bs, num_workers=5)
    val_loader = DataLoader(val_set, batch_size=bs, num_workers=5)

    device = torch.device("cuda:0" if torch.cuda.is_available() else "cpu")
    net = SentenceClassifier(bert_model, freeze_bert=freeze_bert)

    if torch.cuda.device_count() > 1:  # if multiple GPUs
        net = nn.DataParallel(net)

    net.to(device)

    criterion = nn.BCEWithLogitsLoss()
    opti = AdamW(net.parameters(), lr=lr, weight_decay=1e-2)
    num_warmup_steps = 0
    num_training_steps = epochs * len(train_loader)
    t_total = (len(train_loader) // iters_to_accumulate) * epochs
    lr_scheduler = get_linear_schedule_with_warmup(optimizer=opti, num_warmup_steps=num_warmup_steps, num_training_steps=t_total)

    path_to_model = train_bert(bert_model, net, criterion, opti, lr, lr_scheduler, train_loader, val_loader, epochs, iters_to_accumulate, device)


    # Predict

    path_to_output_file = os.path.join('..', 'results', 'output.txt')

    print("Reading test data...")
    test_set = CustomDataset(df_test, maxlen, bert_model)
    test_loader = DataLoader(test_set, batch_size=bs, num_workers=5)

    model = SentenceClassifier(bert_model)
    if torch.cuda.device_count() > 1:  # if multiple GPUs
        model = nn.DataParallel(model)

    print()
    print("Loading the weights of the model...")
    model.load_state_dict(torch.load(path_to_model))
    model.to(device)

    print("Predicting on test data...")
    test_prediction(
        net=model, 
        device=device, 
        dataloader=test_loader, 
        with_labels=True,
        result_file=path_to_output_file
    )
    print()
    print("Predictions are available in : {}".format(path_to_output_file))


    # Evaluate

    labels_test = df_test['is_ad_hominem']

    probs_test = pd.read_csv(path_to_output_file, header=None)[0]  # prediction probabilities
    threshold = 0.5 
    preds_test=(probs_test>=threshold).astype('uint8') # predicted labels using the above fixed threshold

    # Compute the Accuracy, F-Score, Precision and Recall
    metrics = evaluate.combine(["accuracy","f1","precision","recall"])
    results = metrics.compute(references=labels_test, predictions=preds_test)

    print(results)
    information_file = os.path.join('..', 'results', 'information.txt')
    w = open(information_file, 'a')
    w.write(str(results))
    w.close()
