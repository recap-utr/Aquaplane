from fastapi import FastAPI
from pydantic import BaseModel

from fact_checking import fact_check
from ad_hominem import ad_hominem


class ArgumentTopicPair(BaseModel):
    argument: str
    topic: str

class Argument(BaseModel):
    text: str

app = FastAPI()


# load models
global roberta_large_mnli
roberta_large_mnli = fact_check.load_nli_model()

global albert_ad_hominem_classifier, device_ad_hominem
albert_ad_hominem_classifier, device_ad_hominem = ad_hominem.load_ad_hominem_classifier()


@app.post("/api/v1/fact_checking")
def factCheck(argument_topic_pair: ArgumentTopicPair):
    return fact_check.get_labels(argument_topic_pair.argument, argument_topic_pair.topic)

@app.post("/api/v1/ad_hominem")
def ad_hominem_classification(argument: Argument):
    return ad_hominem.ad_hominem_classification(albert_ad_hominem_classifier, device_ad_hominem, argument.text)
