from transformers import AutoTokenizer
from torch.utils.data import Dataset

class CustomDataset(Dataset):

    def __init__(self, data, maxlen, with_labels=True, bert_model='albert-base-v2'):
        self.data = data
        self.tokenizer = AutoTokenizer.from_pretrained(bert_model)  
        self.maxlen = maxlen
        self.with_labels = with_labels 

    def __len__(self):
        return len(self.data)

    def __getitem__(self, index):

        # Selecting body at the specified index in the data frame
        body = str(self.data.loc[index, 'body'])

        # Tokenize the sentence to get token ids and attention masks
        encoded_sentence = self.tokenizer(body,
                                      padding='max_length',  # Pad to max_length
                                      truncation=True,  # Truncate to max_length
                                      max_length=self.maxlen,  
                                      return_tensors='pt')  # Return torch.Tensor objects
        
        token_ids = encoded_sentence['input_ids'].squeeze(0) 
        attn_masks = encoded_sentence['attention_mask'].squeeze(0)

        if self.with_labels:
            label = self.data.loc[index, 'is_ad_hominem']
            return token_ids, attn_masks, label  
        else:
            return token_ids, attn_masks