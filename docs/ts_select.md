# ts_select.jar

**ts_select.jar** is a standalone Java application that select test scenarios on the basis of a given test suite.
The number of test scenarios as well as the number of test cases in each scenario are specified by user via a [configuration file](#configuration-file).
Besides, users could customize redundancy test case ratios. With these ratios, the program will automatically generate
redundant test cases on the basis of five-features test cases.

### How it works

For a given test suite, the program classifies the test suite into two groups:
- *Group A* - A set of five-features test cases (e.g., ```t: A=true & B=false & C=true & D=false & E=true```)
- *Group B* - A set of the remaining test cases

For each test scenario, with two given numbers - the number of test cases (```#T```) and the redundancy ratio (```red%```), the program could determine
three important values:
- The number of redundant test cases that need to be generated - ```#red = #T * red%```
- The number of non-redundant test cases selected from *Group A* - ```#Ta = #T / 30 + 1```  
- The number of non-redundant test cases selected from *Group B* - ```#Tb = #T - #red - #Ta```

> *Why ```#T / 30```?* For a given five-features test case, we can have totally 30 redundant test cases. 

As soon as the mentioned values have been identified, the program selects randomly ```#Ta``` and ```#Tb``` 
test cases from the corresponding group of test cases.
Next, the program use ```#Ta``` test cases from Group A to generate ```#red``` redundant test cases.

> A redundant test case of a given test case could be simply a subpart of the given test case. For instance,
test case ```t1: A=true & B=false``` could have two redundant test cases ```t2: A=true``` and ```t3: B=false```.

> **Note:** The program won't reselect test cases which have been selected for other scenarios in the same execution.

### Usage

**Requirements**: the latest Java

**Syntax**:
```
java -jar ts_select.jar [-cfg <path-to-configuration-file>]
```

If the parameter `-cfg` has not been specified, the program will find the default configuration file in `./conf/ts_select.cfg`.

### Configuration file

The configuration file needs the following parameters:

| *parameters*     | *default value*            | *description*                                                      |
|------------------|----------------------------|--------------------------------------------------------------------|
| ```nameKB```     | **null**                   | the filename of the feature model                                  |
| ```dataPath```   | **./data/**                | the folder where the dataset is stored                             |
| ```outputPath``` | **./results/**             | the folder where the results will be saved                         |
| ```cardTC```     | **5,10,25,50,100,250,500** | the list of numbers of test cases want to be selected in scenarios |
| ```redRatio```   | **0.0,0.2,0.5,0.7,0.9**    | the list of redundancy ratios in scenarios                         |

For examples of configuring these parameters, we refer to configuration files in the folder *./conf*.

## Scenario file structure

A scenario file should have "testcases" as its file name extension. The file should start with the number of test cases,
that the scenario has. Following this number, each line is a test case.
