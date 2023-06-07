import torch
import torch.nn as nn
from transformers import AutoModel
from torch.cuda.amp import autocast

class SentenceClassifier(nn.Module):

    def __init__(self, bert_model="albert-base-v2", freeze_bert=False):
        super(SentenceClassifier, self).__init__()
        #  Instantiating BERT-based model object
        self.bert_layer = AutoModel.from_pretrained(bert_model, return_dict=False)

        #  Fix the hidden-state size of the encoder outputs
        if bert_model == "albert-base-v2":  # 12M parameters
            hidden_size = 768
        elif bert_model == "albert-large-v2":  # 18M parameters
            hidden_size = 1024
        elif bert_model == "albert-xlarge-v2":  # 60M parameters
            hidden_size = 2048
        elif bert_model == "albert-xxlarge-v2":  # 235M parameters
            hidden_size = 4096
        elif bert_model == "bert-base-uncased": # 110M parameters
            hidden_size = 768

        # Freeze bert layers and only train the classification layer weights
        if freeze_bert:
            for p in self.bert_layer.parameters():
                p.requires_grad = False

        # Classification layer
        self.cls_layer = nn.Linear(hidden_size, 1)

        self.dropout = nn.Dropout(p=0.1)

    @autocast()
    def forward(self, input_ids, attn_masks):

        # Obtain contextualized representations
        cont_reps, pooler_output = self.bert_layer(input_ids, attn_masks)

        logits = self.cls_layer(self.dropout(pooler_output))

        return logits