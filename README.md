# WipeOutR: Automated Redundancy Detection for Feature Models

This repository shows the implementation and the evaluation of the WipeOutR algorithms,
which will be presented at the SPLC 2022 in the paper entitled
*WipeOutR: Automated Redundancy Detection for Feature Models*.
The research community can exploit this repository to reproduce the work described in our paper fully.

WipeOutR is an algorithmic approach to support the automated identification of redundancies in feature models (FM) and FM test suites.
This approach has the potential to significantly improve the quality of feature model development and configuration.

## Evaluation process

We have evaluated the WipeOutR algorithms using the *Linux-2.6.33.3* feature model taken from [Diverso Lab's benchmark](https://github.com/diverso-lab/benchmarking) [2].
To ensure the reproducibility of the results, we have used the seed value of *141982L* for the random number generator.
The evaluation process consists of the following five steps:

### Step 1 - Test suite generation

We generated a test suite for the feature model.
Each test suite consists of 5 types of test cases: dead features, false optional, full mandatory, false mandatory, and partial configuration.
The folder *./data/testsuite* stores the test suite.

### Step 2 - Scenario selection

We select test-case scenarios where the ratio of violated test cases to non-violated test cases is a specific number predetermined by the user.
The number of scenarios is selected depending on the combination of the number of constraints |CF| and the number of test cases |Tπ|.
For each combination, the average run-time will be calculated (in Step 5) when a specific number of iterations |iter| is reached.

In our paper, for each selected feature model, we selected 21 scenarios for 7 numbers of test cases. In total, there were 378 (3 feature models x 6 |CF| x 7 |Tπ| x 3 |iter|) selected test-case scenarios. The folder *./data/paper/testcases* stores 378 selected scenarios.

### Step 5 - DirectDebug evaluation

The program calculates the average run-time of DirectDebug.

In our paper, the input of this final step is 18 feature models and 378 selected scenarios of test cases.
The output is a table in which each entry represents the average diagnosis computing time derived from 3 repetitions
(see Table III in [1]).

## Implementation

This software package supports the evaluation process via **six** sub-programs which
can be triggered by command line arguments.

| *arguments* | *description* |
| ----------- | ----------- |
| ```-g``` | feature models generation |
| ```-st``` | feature model statistics |
| ```-ts``` | test suite generation |
| ```-tc``` | test cases classification |
| ```-ss``` | scenarios selection |
| ```-e``` | DirectDebug evaluation |
| ```-h``` | help |

## How to reproduce the experiment

### Use a CodeOcean capsule

The easiest way to reproduce the experiment is to use a [CodeOcean](https://codeocean.com) capsule.
You could find a reproducible evaluation of WipeOutR algorithms in [here](https://codeocean.com/capsule/5824065/tree/v1).

### Use the standalone Java applications

We published seven standalone Java applications naming **d2bug_eval.jar** that encapsulates the evaluation steps in one program.

**d2bug_eval.jar** is available from the [latest release](https://github.com/AIG-ist-tugraz/DirectDebug/releases/tag/v1.1).
For further details of this app, we refer to [d2bug_eval.jar guideline](https://github.com/AIG-ist-tugraz/DirectDebug/blob/main/d2bug_eval.jar.md).

#### Use a bash script

We provide two bash scripts that perform all necessary steps from *compiling the source code* to *running the DirectDebug evaluation process*.

First, **run.sh** will compile the source files, package them in one *jar* file, and run only the DirectDebug evaluation step (Step 5) with the dataset used for the paper.

Second, **run_all.sh** will carry out all five steps of the DirectDebug evaluation process, and you will get the new dataset, new results. Three steps Feature model generation, Testsuite generation, and Test case classification will take a long time to complete. Thus, if you have around 3-4 free days, then try with **run_all.sh**. Otherwise, please run the evaluation process step-by-step using our **d2bug_eval.jar**.

To run these bash scripts on your system after cloning the source code:

1. First, you need to make the script executable with **chmod**:

```
$ chmod u+x run.sh
```

2. Run the script by prefixing it with ```./```:

```
$ ./run.sh
```

### Get your own copy to run offline

#### Get the Maven dependencies from GitHub package repository

Some part of our implementation depends on [CA-CDR library](https://github.com/manleviet/CA-CDR-V2). Thus, after cloning the source code into your system,
you need to add the below script in the *settings.xml* file:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd">
    <activeProfiles>
        <activeProfile>github</activeProfile>
    </activeProfiles>

    <profiles>
        <profile>
            <id>github</id>
            <repositories>
                <repository>
                    <id>central</id>
                    <url>https://repo1.maven.org/maven2</url>
                </repository>
                <repository>
                    <id>github</id>
                    <url>https://maven.pkg.github.com/manleviet/*</url>
                </repository>
            </repositories>
        </profile>
    </profiles>
    
    <servers>
        <server>
            <id>github</id>
            <username>USERNAME</username>
            <password>TOKEN</password>
        </server>
    </servers>
</settings>
```
Replacing USERNAME with your GitHub username, and TOKEN with your personal access token 
(see [Creating a personal access token](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/creating-a-personal-access-token)).

#### Compile the source code

After having the source code and the *settings.xml* file, you could compile the source code and package it into *jar* files using Maven.

```shell
$ mvn clean package --settings <path to settings.xml>
```
> If you don't have Maven in your computer, please follow the [Maven guide](https://maven.apache.org/download.cgi) to install it.

#### Build your own software



### Construct Configuration files

The software package supports a wide range of parameters as follows:

- ```showEvaluation```: (**false** by default) determines whether the program prints out the results to the console and the *results.txt* file.
- ```showDebug```: (**false** by default) determines whether the program prints out the information messages to the console and the *results.txt* file.
- ```CF```: (**10,20,50,100,500,1000** by default) a list of the numbers of constraints |CF|.
- ```TC```: (**5,10,25,50,100,250,500** by default) a list of the numbers of test cases |Tπ|.
- ```numGenFM```: (**3** by default) the number of feature model generated for each number of constraints |CF|.
- ```CTC```: (**0.4** by default) the ratio of cross-tree constraints which generated feature models has to be attained.
- ```numIter```: (**3** by default) the number of iterations |iter|.
- ```perViolated_nonViolated```: (**0.3** by default) the percentage of violated test cases to non-violated test cases.
- ```dataPath```: (**./data/** by default) the folder where the dataset is stored.
- ```resultsPath```: (**./results/** by default) the folder where the results will be saved.

For further details on configuring these parameters, we refer to three example configuration files, **confForPaper.txt**, **conf1.1.txt**, **conf1.2.txt**. **confForPaper.txt** is used by **run.sh**, and two remaining files are used by **run_all.sh**.

## Use the code source for your project

> [You need to get the Maven packages of the CA-CDR library](#get-the-maven-dependencies-from-github-package-repository)

[An example of using the WipeOutR algorithms for automated redundancy detection](https://github.com/AIG-ist-tugraz/WipeOutR/tree/main/src/test/java/at/tugraz/ist/ase/wipeoutr/algorithm)

**d2bug_eval** consists of three sub-packages: **Feature Model**, **MBDiagLib**, and **Debugging**.  
**Feature Model** reads feature model files and supports *feature model generation* and *feature model statistics*. 
**MBDiagLib** provides (1) an abstract model to hold variables and constraints, 
(2) an abstract consistency checker for underlying solvers, 
(3) a *Choco* consistency checker using [Choco Solver](https://choco-solver.org), 
and (4) functions to measure the performance of algorithms in terms of run-time or the number of solver calls. 
**Debugging** provides components w.r.t. test-cases management, the DirectDebug implementation, 
a debugging model with test-cases integration, and debugging-related applications (e.g. *test suite generation*, 
*test cases classification*, and *test case selection*).

Besides feature models encoded in the *SXFM* format and consistency checks conducted 
by [Choco Solver](https://choco-solver.org), **d2bug_eval** can be extended to support further formats 
(e.g., *FeatureIDE* format) and other off-the-shelf solvers. 
Furthermore, the program can be extended to evaluate other constraint-based algorithms, 
such as conflict detection algorithms and diagnosis identification algorithms.

## References

[1] V.M. Le, A. Felfernig, M. Uta, T.N.T. Tran, C. Vidal, WipeOutR: Automated Redundancy Detection for Feature Models, 26th ACM International Systems and Software Product Line Conference (SPLC'22), 2022.
[2] Heradio, R., Fernandez-Amoros, D., Galindo, J.A. et al. Uniform and scalable sampling of highly configurable systems. Empir Software Eng 27, 44 (2022). [https://doi.org/10.1007/s10664-021-10102-5](https://doi.org/10.1007/s10664-021-10102-5)
