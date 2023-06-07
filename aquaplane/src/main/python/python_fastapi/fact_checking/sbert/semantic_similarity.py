import sys
import numpy
numpy.set_printoptions(threshold=sys.maxsize)

from sentence_transformers import SentenceTransformer, util
model = SentenceTransformer('all-MiniLM-L6-v2')

def getEmbedding(str):
    return model.encode(str, convert_to_tensor=True)

def cosSimOfEmbeddings(e1, e2):
    cosine_score = util.cos_sim(e1, e2)
    return cosine_score.item()

def getSemanticSimilarityOf(a1, a2):
    e1 = getEmbedding(a1)
    e2 = getEmbedding(a2)
    semantic_similarity = cosSimOfEmbeddings(e1, e2)
    return semantic_similarity
