# ts_select.jar

**ts_select.jar** is a standalone Java application that generate test scenarios on the basic of a given test suite.


generates a test suite for a given feature model.
The test suite consists of five types of test cases: dead features, false optional, full mandatory, false mandatory, and partial configuration.

This class selects randomly test cases (scenarios) from a test suites.
* Besides, it also automatically generates redundant test cases according to the redundancy ratio.

### Usage

**Requirements**: the latest Java

**Syntax**:
```
java -jar ts_select.jar [-cfg <path-to-configuration-file>]
```

If the parameter `-cfg` isn't specified, the program will find the default configuration file in `./conf/ts_select.cfg`.

### Configuration file

The configuration file needs the following parameters:

| *parameters*     | *default value*            | *description*                                                  |
|------------------|----------------------------|----------------------------------------------------------------|
| ```nameKB```     | **null**                   | filename of the feature model                                  |
| ```dataPath```   | **./data/**                | the folder where the dataset is stored                         |
| ```outputPath``` | **./results/**             | the folder where the results will be saved                     |
| ```cardTC```     | **5,10,25,50,100,250,500** | list of numbers of test cases want to be selected in scenarios |
| ```redRatio```   | **0.0,0.2,0.5,0.7,0.9**    | list of redundancy ratios in scenarios                         |

For examples on configuring these parameters, we refer to configuration files in the folder *./conf*.

## Scenario file structure

A scenario file should have "testcases" as its file name extension. It should start the file with the number of test cases,
that the scenario has. After this number, each line is a test case.