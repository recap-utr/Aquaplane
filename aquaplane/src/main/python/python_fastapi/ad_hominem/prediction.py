import torch
from transformers import AutoTokenizer

def get_probs_from_logits(logits):
    probs = torch.sigmoid(logits.unsqueeze(-1))
    return probs.detach().cpu().numpy()

def predict(net, device, arg):
    net.eval()
    with torch.no_grad():
        seq, attn_masks = tokenize_sentence(arg)
        seq, attn_masks = seq.to(device), attn_masks.to(device)
        logits = net(seq.unsqueeze(0), attn_masks.unsqueeze(0))       # unsqueeze because of batch size
        probs = get_probs_from_logits(logits.squeeze(-1)).squeeze(-1)
        return probs

def tokenize_sentence(arg):
    # Tokenize sentence to get token id and attention mask
    tokenizer = AutoTokenizer.from_pretrained("albert-base-v2")
    encoded = tokenizer(arg,
                                padding='max_length',
                                truncation=True,
                                max_length=512,
                                return_tensors='pt'
                            )

    token_ids = encoded['input_ids'].squeeze(0)
    attn_masks = encoded['attention_mask'].squeeze(0) 
    
    return token_ids, attn_masks
