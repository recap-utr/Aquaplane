from pathlib import Path
import json
import requests
from .claim import Claim, ClaimReview

def getIssues():
    inpath = Path('../../data/factChecking/issues.txt')
    args = {"newline": "", "encoding": "utf-8-sig"}
    with inpath.open("r", **args) as readFile:
        issues = readFile.readlines()
        readFile.close()
    return issues

def searchClaims(query, pageSize, reviewPublisherSiteFilter):
    payload = {
        'key': 'TODO',
        'query': query,
        'pageSize': pageSize,
        'reviewPublisherSiteFilter': reviewPublisherSiteFilter,
        'languageCode': 'en',
    }
    url ='https://factchecktools.googleapis.com/v1alpha1/claims:search'
    response = requests.get(url, params=payload)
    if response.status_code == 200:
        result = json.loads(response.text)
        all_claims = []
        try:
            claims = result["claims"]
            if not claims:
                return all_claims
            else:
                print(str(len(claims)) + " claims have been found" + " for query '" + query + "'")
                for claim in claims:
                    c = createClaimWithReviews(claim)
                    all_claims.append(c)
            return all_claims
        except:
            print("No claims have been found for query '" + query + "'")
            return all_claims

def createClaimWithReviews(claim):
    try:
        claim_reviews = claim["claimReview"]
        all_claim_reviews = []
        for claimReview in claim_reviews:
            textual_rating = claimReview["textualRating"]
            publisher_name = claimReview['publisher']['name']
            textual_rating = extractTruthRatingFrom(textual_rating, publisher_name)
            cr = ClaimReview(publisher_name, textual_rating)
            all_claim_reviews.append(cr)
        c = Claim(claim['text'], all_claim_reviews)
        return c
    except:
        print("No claim reviews have been found for claim")
        c = Claim(claim['text'], [])
        return c

def printClaims(claims):
    for claim in claims:
        print(claim.text)
        for claimReview in claim.claimReviews:
            print(claimReview.publisher)
            print(claimReview.textualRating)
        print()

def extractTruthRatingFrom(textualRating, publisher):
    rating = textualRating.strip().lower()

    if publisher == 'Australian Associated Press':
        delimiters = [".", "-", "\u2013"]
        delimiters_index = dict.fromkeys(delimiters , len(textualRating))
        for delimiter in delimiters:
            pos = rating.find(delimiter)
            if pos != -1:
                delimiters_index[delimiter] = pos
        end_of_rating = min(delimiters_index.values())
        rating = rating[:end_of_rating].strip()

    return rating

def writeTruthRatingsToFile(truth_ratings):
    outpath = Path('../../data/factChecking/truth_ratings.txt')
    args = {"newline": "", "encoding": "utf-8-sig"}
    with outpath.open('w', **args) as writeFile:
        for tr in truth_ratings:
            writeFile.write(tr)
            writeFile.write('\n')

def writePublisherWithTruthRatingsToJSON(publisher_with_truth_ratings):
    json_object = json.dumps(publisher_with_truth_ratings, default=list, indent=4)

    outpath = Path('../../data/factChecking/publisher_truth_ratings.json')
    args = {"newline": "", "encoding": "utf-8-sig"}
    with outpath.open('w', **args) as writeFile:
        writeFile.write(json_object)