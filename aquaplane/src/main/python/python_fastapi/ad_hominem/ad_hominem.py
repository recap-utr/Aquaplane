import torch
from .sentenceclassifier import SentenceClassifier
from .prediction import predict

def load_ad_hominem_classifier():
    path_to_model = "../models/albert-base-v2_ad_hominem.pt"
    model = SentenceClassifier("albert-base-v2")
    device = torch.device("cuda:0" if torch.cuda.is_available() else "cpu")
    print("Loading the weights of the model...")
    model.load_state_dict(torch.load(path_to_model))
    model.to(device)
    model.eval()    # set dropout and batch normalization layers to evaluation mode
    return model, device

def ad_hominem_classification(model, device, arg):
    prob = predict(model, device, arg)
    threshold = 0.5
    pred = (prob >= threshold).astype('uint8')   # predicted label using the above fixed threshold
    result = {
        "is_ad_hominem": 0 if pred == 0 else 1,
    }
    return result
