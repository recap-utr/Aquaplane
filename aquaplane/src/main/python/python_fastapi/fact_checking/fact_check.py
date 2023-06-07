from .gfc_api import searchClaims
from .claim import FactCheckClaimPair, Justification
from .sbert import semantic_similarity
from .claimbuster import getCheckWorthinessOfMultipleSentences
from transformers import pipeline
from nltk.tokenize import sent_tokenize


# first run
# import nltk
# nltk.download('stopwords')


def initialize_rating_system():
    global rather_true
    global rather_false
    global uncertain
    rather_true = open("../data/fact_checking/rating_system/rather_true.txt").read().splitlines()
    rather_false = open("../data/fact_checking/rating_system/rather_false.txt").read().splitlines()
    uncertain = open("../data/fact_checking/rating_system/uncertain.txt").read().splitlines()


def load_nli_model():
    global model
    model = pipeline("text-classification", model='roberta-large-mnli')
    return model


def extract_clauses(arg):
    sentences = sent_tokenize(arg)
    clauses = []
    for sentence in sentences:
        for clause in sentence.split(","):
            clauses.append(clause.strip())
    return clauses


def filter_by_checkworthiness(clauses):
    prepared_clauses = []
    for clause in clauses:
        if (clause.endswith((".", "!", "?"))):
            prepared_clauses.append(clause)
        else:
            prepared_clauses.append(''.join((clause, '. ')))

    map_text_checkWorthiness = getCheckWorthinessOfMultipleSentences(" ".join(prepared_clauses))
    print(map_text_checkWorthiness)
    map_text_checkWorthiness = {text: score for text, score in map_text_checkWorthiness.items() if score > 0.4}
    print(map_text_checkWorthiness)

    return map_text_checkWorthiness


def predict_truth_ratings(map_text_checkWorthiness, topic):
    labeled_claim_pairs = []
    for text in map_text_checkWorthiness.keys():
        rating_system_counter = {'rather_true': 0, 'rather_false': 0, 'uncertain': 0, 'other': 0}
        matched_claims = searchClaims(text, 20, "")

        if matched_claims is None:
            return []

        appropriate_matched_claim, semantic_similarity = get_matched_claim_with_highest_semantic_similarity(text, matched_claims)
        if appropriate_matched_claim is not None:
            if len(appropriate_matched_claim.claimReviews) != 0:
                claim_review = appropriate_matched_claim.claimReviews[0]
                label = map_textual_rating_to_rating_system(claim_review.textualRating.lower())
                is_negation = detect_negation(text, appropriate_matched_claim.text)
                if is_negation:
                    label = invert_label(label)
                rating_system_counter[label] = rating_system_counter[label] + 1

            label_of_text = getKey_with_maxValue(rating_system_counter)
            labeled_claim_pair = FactCheckClaimPair(
                topic=topic,
                claim=text,
                checkWorthiness=map_text_checkWorthiness[text],
                justification=Justification(
                    matchedClaim=appropriate_matched_claim.text,
                    claimReview=claim_review,
                    semantic_similarity=semantic_similarity,
                    mapped_rating= map_textual_rating_to_rating_system(claim_review.textualRating.lower()),
                    is_negation= 'true' if is_negation else 'false',
                ),
                label=label_of_text
            )
            labeled_claim_pairs.append(labeled_claim_pair)
        
    return labeled_claim_pairs


def get_matched_claim_with_highest_semantic_similarity(text, matched_claims):
    map_matched_claim_semantic_similarity = {}
    for matchedClaim in matched_claims:
        semantic_sim = semantic_similarity.getSemanticSimilarityOf(text, matchedClaim.text)
        if (semantic_sim > 0.7):    # threshold to guarantee similarity
            map_matched_claim_semantic_similarity[matchedClaim] = semantic_sim

    if map_matched_claim_semantic_similarity:
        # Get matched claim with highest semantic similarity
        appropriate_matched_claim = max(map_matched_claim_semantic_similarity, key=map_matched_claim_semantic_similarity.get)

        return appropriate_matched_claim, map_matched_claim_semantic_similarity[appropriate_matched_claim]
    else:
        return None, None


def map_textual_rating_to_rating_system(textual_rating):
    if textual_rating in rather_true: return "rather_true"
    if textual_rating in rather_false: return "rather_false"
    if textual_rating in uncertain: return "uncertain"
    return "other"


def detect_negation(claim, matchedClaim):
    input = claim + " [SEP] " + matchedClaim
    result = model(input)
    is_negation = result[0]['label'] == 'CONTRADICTION'
    return is_negation


def invert_label(label):
    if label == "rather_true": return "rather_false"
    if label == "rather_false": return "rather_true"
    return label


def getKey_with_maxValue(dict):
    keymax = max(zip(dict.values(), dict.keys()))[1]
    return keymax


def get_labels(argument, topic):
    initialize_rating_system()
    clauses = extract_clauses(argument)
    map_text_checkWorthiness = filter_by_checkworthiness(clauses)
    labeled_claim_pairs = predict_truth_ratings(map_text_checkWorthiness, topic)
    return labeled_claim_pairs
