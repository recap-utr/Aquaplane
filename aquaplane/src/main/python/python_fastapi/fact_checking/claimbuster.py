import requests

# ClaimBuster uses BERT ClaimSpotter algorithm to determine how check-worthy a claim is.
api_key = "TODO"

def getCheckWorthinessOfSingleSentence(claim):
    api_endpoint = f"https://idir.uta.edu/claimbuster/api/v2/score/text/{claim}"
    request_headers = {"x-api-key": api_key}
    api_response = requests.get(url=api_endpoint, headers=request_headers)
    response_json = api_response.json()
    result = response_json["results"][0]
    score = result["score"]
    return score

def getCheckWorthinessOfMultipleSentences(claims):
    try:
        api_endpoint = f"https://idir.uta.edu/claimbuster/api/v2/score/text/sentences/{claims}"
        request_headers = {"x-api-key": api_key}
        api_response = requests.get(url=api_endpoint, headers=request_headers)
        response_json = api_response.json()
        results = response_json["results"]
        map_text_score = {}
        for result in results:
            map_text_score[result["text"]] = result["score"]
        return map_text_score
    except:
        return {}
