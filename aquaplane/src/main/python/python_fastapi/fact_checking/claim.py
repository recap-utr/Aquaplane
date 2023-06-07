class Claim:
    def __init__(self, text, claimReviews):
        self.text = text
        self.claimReviews = claimReviews

class ClaimReview:
    def __init__(self, publisher, textualRating):
        self.publisher = publisher
        self.textualRating = textualRating

class Justification:
    def __init__(self, matchedClaim, claimReview, semantic_similarity, mapped_rating, is_negation):
        self.matchedClaim = matchedClaim
        self.claimReview = claimReview
        self.semantic_similarity = semantic_similarity
        self.mapped_rating = mapped_rating
        self.is_negation = is_negation

class FactCheckClaimPair:
    def __init__(self, topic, claim, checkWorthiness, justification, label):
        self.topic = topic
        self.claim = claim
        self.checkWorthiness = checkWorthiness
        self.justification = justification
        self.label = label
