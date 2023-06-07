# AQUAPLANE: Argument Quality Explainer

AQUAPLANE is a web application that identifies differences in argument quality by comparing certain properties of arguments, derives decisions from them, and interactively provides users with explanations for the decisions. Various dimensions of argument quality are measured, such as rhetoric, dialectics, and logical coherence.
A stepwise majority decision is then used to decide which argument is more convincing with respect to each dimension of argument quality and overall.
Based on these decisions and the results of the methods, static explanations are generated.

## Folder Structure

The folder structure gives an overview of the relevant directories and provides brief explanations.

```
  .
  │ 
  ├───src
  │   ├───main
  │   │   ├───java/de/seanbri/aquaplane        # Spring Boot application source code
  │   │   │   ├───argumentQuality                 # Source code to determine the more convincing argument including explanation
  │   │   │   ├───assessment_approaches           # Implementation of all assessment approaches 
  │   │   │   ├───controller                      # Handling HTTP requests and responses
  │   │   │   ├───evaluation_service              # Service for evaluation based on a data set
  │   │   │   ├───model                           # Entities for the database
  │   │   │   ├───repository                      # Repository to perform common database operations
  │   │   │   ├───service                         # Defining methods that are called by the controller
  │   │   │   ├───utility
  │   │   │   └───AquaplaneApplication.java
  │   │   ├───python                           # Python API source code
  │   │   ├───resources
  │   │   └───ui                               # Implementation of the GUI with React
  │   └───...
  └───...
```

## Instructions

### How to run the system locally?

1. Run MySQL Server on port 3306.
2. Run *AquaplaneApplication.java* to start the Spring Boot application.
3. Follow the instructions of "src/main/python/Readme.md" to start the Python API.
4. Navigate to "src/main/ui" and start the React project with `npm start`.

### How to add a new assessment approach?

1. Create a folder in the "assessment_approaches" folder with the implementation of the new assessment approach.
2. Create a class that implements the *AssessmentApproach* interface:
   * *getName()*: return the unique name of the approach
   * *computeRecord(String argument, String claim)*: call the function of the assessment approach for argument (and claim) and return a record containing the calculated values
   * *compare(Record r1, Record r2)*: specify how the values of the records are to be compared and return a *Comparison*\-Object containing the decision and the compared records
   * *explain(Comparison comparison)*: create a string template that embeds the computed values based on the decisions made
3. Add assessment approach to the *getAllAssessmentApproaches()* function in the *AssessmentApproachProcessor*.
4. In the *DimensionMapper*, the dimensions to which the assessment approach is to be mapped must be specified.