import os
import torch
import copy
import numpy as np
from torch.cuda.amp import autocast, GradScaler
from tqdm import tqdm
from util import evaluate_loss

def train_bert(bert_model, net, criterion, opti, lr, lr_scheduler, train_loader, val_loader, epochs, iters_to_accumulate, device):

    information_file = os.path.join('..', 'results', 'information.txt')
    w = open(information_file, 'w')
    w.close()

    best_loss = np.Inf
    best_ep = 1
    nb_iterations = len(train_loader)
    print_every = nb_iterations // 5  # print training loss 5 times per epoch

    scaler = GradScaler()

    for ep in range(epochs):

        net.train()
        running_loss = 0.0
        for it, (seq, attn_masks, labels) in enumerate(tqdm(train_loader)):

            # Converting to cuda tensors
            seq, attn_masks, labels = \
                seq.to(device), attn_masks.to(device), labels.to(device)
    
            with autocast():
                logits = net(seq, attn_masks)

                # Computing loss
                loss = criterion(logits.squeeze(-1), labels.float())
                loss = loss / iters_to_accumulate  # Normalize loss

            # Backpropagating the gradients
            scaler.scale(loss).backward()

            if (it + 1) % iters_to_accumulate == 0:
                # Optimization step
                scaler.step(opti)
                scaler.update()
                lr_scheduler.step()
                # Clear gradients
                opti.zero_grad()


            running_loss += loss.item()

            if (it + 1) % print_every == 0:  # Print and write training loss information
                print()
                print("Iteration {}/{} of epoch {} complete. Loss : {} \n"
                      .format(it+1, nb_iterations, ep+1, running_loss / print_every))
                
                w = open(information_file, 'a')
                w.write(
                    "Iteration {}/{} of epoch {} complete. Loss : {} \n"
                      .format(it+1, nb_iterations, ep+1, running_loss / print_every)
                )
                w.close()

                running_loss = 0.0


        val_loss = evaluate_loss(net, device, criterion, val_loader)  # Compute validation loss
        print()
        print("Epoch {} complete! Validation Loss : {}".format(ep+1, val_loss))

        w = open(information_file, 'a')
        w.write(
            "Epoch {} complete! Validation Loss : {}".format(ep+1, val_loss)
        )
        w.write("\n")
        w.close()


        if val_loss < best_loss:
            print("Best validation loss improved from {} to {}".format(best_loss, val_loss))
            print()

            w = open(information_file, 'a')
            w.write(
                "Best validation loss improved from {} to {}".format(best_loss, val_loss)
            )
            w.write("\n")
            w.close()

            net_copy = copy.deepcopy(net)  # save a copy of the model
            best_loss = val_loss
            best_ep = ep + 1

    # Saving the model
    path_to_model = os.path.join('..', 'models', '{}_lr_{}_val_loss_{}_ep_{}.pt'.format(bert_model, lr, round(best_loss, 5), best_ep))
    torch.save(net_copy.state_dict(), path_to_model)
    print("The model has been saved in {}".format(path_to_model))

    del loss
    torch.cuda.empty_cache()

    return path_to_model
