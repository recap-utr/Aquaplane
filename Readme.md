# Aquaplane

## Important notes
This repository contains the code for the paper **AQUAPLANE: The Argument Quality Explainer App** which is currently under submission.
The system requires datasets of which we do not have the publication rights, but which are linked in the paper.
Nevertheless, the entire project including the said data is available on request from the authors.

## Demo video
A short demonstration video can be found here:
https://basilika.uni-trier.de/nextcloud/s/mipFoNSfqTx7Jia

## Setup
Aquaplane requires MySQL, Java, Python, and JavaScript.

### Keys
- Write your MySQL root password in "src/main/java/de/seanbri/aquaplane/application.properties" (line 5).
- Write your ClaimBuster API key in "src/main/python/python_fastapi/fact_checking/claimbuster.py" (line 4).
- Write your Google Fact-Check API key in "src/main/python/python_fastapi/fact_checking/gfc_api.py" (line 16).

### JAVA/MySQL
- Run MySQL Server on port 3306.
- Only the first time, i.e. if it does not exist yet: create the database "aquaplane" with MySQL using "create database aquaplane".
- Start "src/main/java/de/seanbri/aquaplane/AquaplaneApplication.java".

### PYTHON
- Navigate to "src/main/python/python_fastapi" (using CLI).
- execute "uvicorn main:app --reload".

### REACT
- Navigate to "src/main/ui" (using CLI).
- Only the first time, execute "npm install".
- Execute "npm start".


## Folder structure

### Aquaplane
The *"aquaplane"* folder is the root directory of the created application. It contains all components of the application and thus all created assessment approaches and the implemented application logic for determining the more convincing argument including explanation.

### Ad hominem classifier
The folder *"ad_hominem_classifier_albert"* contains all Python scripts for training, validating and evaluating the ad hominem model.

### Evaluation
The *"evaluation"* folder contains all Python scripts used to evaluate the system.
