# WipeOutR: Automated Redundancy Detection for Feature Models

**WipeOutR** is an algorithmic approach to support the automated identification of redundancies in feature models (FM) and FM test suites.
This approach has the potential to significantly improve the quality of feature model development and configuration.

This repository shows the implementation and the evaluation of the **WipeOutR** algorithms,
which will be presented at the SPLC 2022 in the paper entitled
*WipeOutR: Automated Redundancy Detection for Feature Models*.
The research community can fully exploit this repository to reproduce the work described in our paper.

## Table of Contents

- [Repository structure](#repository-structure)
- [Evaluation process](#evaluation-process)
- [How to reproduce the experiment](#how-to-reproduce-the-experiment)
  - [Build a Docker image](#build-a-docker-image)
  - [Use pre-build Java applications](#use-pre-build-java-applications)
  - [Build apps by yourself](#build-apps-by-yourself)

## Repository structure

| *folder*         | *description*                                                                           |
|------------------|-----------------------------------------------------------------------------------------|
| ./conf           | contains all configuration files used in the evaluation of the **WipeOutR** algorithms  |
| ./data           | stores *Linux-2.6.33.3* feature models, a test suite, and scenarios                     |
| ./data/testsuite | stores a test suite of the original *Linux-2.6.33.3* feature model                      |
| ./data/scenarios | contains scenarios selected to evaluate the **WipeOutR_T** algorithm                    |
| ./data/results   | evaluation results published in the paper                                               |
| ./docs           | guides of *jar* files                                                                   |
| ./results        | stores the results                                                       |
| ./src            | source code                                                                             |
| ./shell          | bash scripts to execute the evaluations                                                 |
| ./docker         | a bash script and a copy of configuration files, which are used to build a Docker image |
| Dockerfile       | Dockerfile to build the Docker image                                                    |
| settings.xml     | settings of the GitHub Maven repository                                                 |

## Evaluation process

We have evaluated the **WipeOutR** algorithms using the *Linux-2.6.33.3* feature model
taken from [Diverso Lab's benchmark](https://github.com/diverso-lab/benchmarking) [2].
To ensure the reproducibility of the results, we have used the seed value of **141982L** for the random number generator.
The folder *./conf* stores all configuration files used in the evaluations.

### WipeOutR_T evaluation

To evaluate the **WipeOutR_T** algorithm, we analyzed different degrees of additionally induced redundancy with regard to the impact
on (1) run-time of WipeOutR_R and (2) test case execution run-time. In particular, the evaluation results are shown in the paper as the following:

*Table 5: Avg. runtime (sec) of **WipeOutR_T** in test set (T) evaluated on an Intel Core i7 (6 cores) 2.60GHz (16GB of RAM).*

| #T  | 0% redundancy                                       | 50% redundancy                                      | 90% redundancy                                      |
|-----|-----------------------------------------------------|-----------------------------------------------------|-----------------------------------------------------|
| 10  | *wr_t_runtime* / *ts_runtime* / *nonred_ts_runtime* | *wr_t_runtime* / *ts_runtime* / *nonred_ts_runtime* | *wr_t_runtime* / *ts_runtime* / *nonred_ts_runtime* |
| 50  | *wr_t_runtime* / *ts_runtime* / *nonred_ts_runtime* | *wr_t_runtime* / *ts_runtime* / *nonred_ts_runtime* | *wr_t_runtime* / *ts_runtime* / *nonred_ts_runtime* |
| 100 | *wr_t_runtime* / *ts_runtime* / *nonred_ts_runtime* | *wr_t_runtime* / *ts_runtime* / *nonred_ts_runtime* | *wr_t_runtime* / *ts_runtime* / *nonred_ts_runtime* |
| 250 | *wr_t_runtime* / *ts_runtime* / *nonred_ts_runtime* | *wr_t_runtime* / *ts_runtime* / *nonred_ts_runtime* | *wr_t_runtime* / *ts_runtime* / *nonred_ts_runtime* |

> **Legends:**
> 
> *wr_t_runtime* - **WipeOutR_T** runtime
>
> *ts_runtime* - execution runtime with redundant T
>
> *nonred_ts_runtime* - execution runtime with non-redundant T

The evaluation process of the algorithm **WipeOutR_T** consists of the following **four** steps:

#### Step 1 - Test suite generation

We have generated a test suite for the *Linux-2.6.33.3* feature model.
The test suite includes 5 types of test cases: dead features, false optional, full mandatory, false mandatory, and partial configuration.
Each partial configuration refers to 2-5 randomly features. 
The folder *./data/testsuite* stores the test suite file with "testsuite" as its file name extension.

| *type of test cases*  | *number of generated test cases* |
|-----------------------|----------------------------------|
| dead feature          | 6.466                            |
| false optional        | 249                              |
| full mandatory        | 6222                             |
| false mandatory       | 244                              |
| partial configuration | 18.800                           |

> **Test suite file structure**
> 
> A test suite file starts with the six-lines header. Each header line has a number that represents:
> - 1st line - the total number of test cases,
> - 2nd line - the number of dead features test cases,
> - 3rd line - the number of false optional test cases,
> - 4th line - the number of full mandatory test cases,
> - 5th line - the number of false mandatory test cases, and
> - 6th line - the number of partial configuration test cases.
> 
> After the header, each line represents a test case.

[ts_gen.jar](https://github.com/AIG-ist-tugraz/WipeOutR/releases/tag/ts_gen_v1.0) is a *jar* file executing this step.
For further details, we refer to the [ts_gen.jar guideline](https://github.com/AIG-ist-tugraz/WipeOutR/blob/main/docs/ts_gen.md).

#### Step 2 - Scenario selection

Based on the generated test suite in Step 1, we have selected 12 test scenarios with the test set cardinalities of 10, 50, 100, 250, 500, 1000 and 
the redundancy ratios of 0%, 20%, 50%, 70%, and 90%. Besides, for each selected redundant scenario,
we used the **WipeOutR_T** algorithm to obtain its non-redundant scenario, which is used in Step 4 to compare test case executions. 

The folder *./data/scenarios* stores the selected scenarios (only the selected scenarios with the cardinalities of 10, 50, 100, 500, and the redundancy ratios of 0%, 50%, and 90%).

[ts_select.jar](https://github.com/AIG-ist-tugraz/WipeOutR/releases/tag/ts_select_v1.0) is a *jar* file executing this step. 
For further details, we refer to the [ts_select.jar guideline](https://github.com/AIG-ist-tugraz/WipeOutR/blob/main/docs/ts_select.md).

#### Step 3 - WipeOutR_T evaluation

We calculated the average run-time of the **WipeOutR_T** algorithm (after three iterations) for 12 scenarios.
Evaluation results are filled in *wr_t_runtime* elements of the [Table 5](#wipeoutr_t-evaluation).

[wipeoutr_t.jar](https://github.com/AIG-ist-tugraz/WipeOutR/releases/tag/wipeoutr_t_v1.0) is a *jar* file executing this step.
For further details, we refer to the [wipeoutr_t.jar guideline](https://github.com/AIG-ist-tugraz/WipeOutR/blob/main/docs/wipeoutr_t.md).

#### Step 4 - Test case execution evaluation

We measured average test case execution run-times (after 3 iterations) for 12 scenarios 
in both cases of redundant scenario and non-redundant scenario. Evaluation results are filled in *ts_runtime*/*nonred_ts_runtime* elements of the [Table 5](#wipeoutr_t-evaluation).

[ts_runtime.jar](https://github.com/AIG-ist-tugraz/WipeOutR/releases/tag/ts_runtime_v1.0) is a *jar* file executing this step.
For further details, we refer to the [ts_runtime.jar guideline](https://github.com/AIG-ist-tugraz/WipeOutR/blob/main/docs/ts_runtime.md).

### WipeOutR_FM evaluation

To the **WipeOutR_FM** algorithm, we analyzed (1) runtime of **WipeOutR_FM** 
and (2) solution search on the basis of increased redundancy degrees in CF. Evaluation results are shown as the following:

*Table 6: Avg. runtime (sec) of **WipeOutR_FM** evaluated on an Intel Core i7 (6 cores) 2.60GHz (16GB of RAM).*

| #CF    | red.%  | runtime         | solv. (red.)  | solv. (non-red)      |
|--------|--------|-----------------|---------------|----------------------|
| 13,972 | 34.36% | *wr_fm_runtime* | *sol_runtime* | *nonred_sol_runtime* |
| 18,342 | 50%    | *wr_fm_runtime* | *sol_runtime* | *nonred_sol_runtime* |
| 30,572 | 70%    | *wr_fm_runtime* | *sol_runtime* | *nonred_sol_runtime* |

> **Legends:**
> 
> *wr_fm_runtime* - **WipeOutR_FM** runtime
>
> *sol_runtime* - solution search runtime on the redundant feature model
>
> *nonred_sol_runtime* - solution search runtime on the non-redundant feature model

The evaluation process of the algorithm **WipeOutR_FM** consists of the following **four** steps:

#### Step 1 - Redundant constraints generation

We have generated a set of redundant constraints automatically. These constraints belong to one of two following types:
- Excludes constraints between child features of alternative relationships
- Requires constraints from an optional feature to a mandatory feature

For the *Linux-2.6.33.3* feature model, we generated *693* redundant constraints of the first type and *51531* redundant constraints
of the second type.

[rc_gen.jar](https://github.com/AIG-ist-tugraz/WipeOutR/releases/tag/rc_gen_v1.0) is a *jar* file executing this step.
For further details, we refer to the [rc_gen.jar guideline](https://github.com/AIG-ist-tugraz/WipeOutR/blob/main/docs/rc_gen.md).

#### Step 2 - Create two variant feature models with increased redundancy

We manually added 4370 and 16600 generated redundant constraints to the original *Linux-2.6.33.3* feature model to create two variant
feature models with increased redundancy. The new redundancy ratios of the two variants are 50% and 70%, respectively, compared with 34.36% of the original feature model.

#### Step 3 - WipeOutR_FM evaluation

We calculated the average run-time of the **WipeOutR_FM** algorithm (after three iterations) for three feature models, i.e., the original
feature model and two variants with 50% and 70% redundancy ratios.
Evaluation results are filled in *wr_fm_runtime* elements of the [Table 6](#wipeoutr_fm-evaluation).

[wipeoutr_fm.jar](https://github.com/AIG-ist-tugraz/WipeOutR/releases/tag/wipeoutr_fm_v1.0) is a *jar* file executing this step.
For further details, we refer to the [wipeoutr_fm.jar guideline](https://github.com/AIG-ist-tugraz/WipeOutR/blob/main/docs/wipeoutr_fm.md).

#### Step 4 - Solution search evaluation

We measured average solution search run-times (after 3 iterations) for three feature models
in both cases of redundant feature models and non-redundant feature models. Evaluation results are filled in *solver_runtime*/*nonred_solver_runtime* elements of the [Table 6](#wipeoutr_fm-evaluation).

[solver_runtime.jar](https://github.com/AIG-ist-tugraz/WipeOutR/releases/tag/solver_runtime_v1.0) is a *jar* file executing this step.
For further details, we refer to the [solver_runtime.jar guideline](https://github.com/AIG-ist-tugraz/WipeOutR/blob/main/docs/solver_runtime.md).

## How to reproduce the experiment

You can reproduce the experiment by choosing one of three following ways:
1. [Build a Docker image](#build-a-docker-image)
2. [Use pre-build Java applications](#use-pre-build-java-applications)
3. [Build apps by yourself](#build-apps-by-yourself)

### Build a Docker image

> *Note:* The evaluation process in this Docker image would take 5 - 6 days to complete. If you want to reduce the runtime, 
> you could reset the ```numIter``` parameter in configuration files (in the *./docker* folder) to 1.

After downloading the repository, please replace USERNAME and TOKEN in the *settings.xml* file with your GitHub username and your personal access token
(see [Creating a personal access token](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/creating-a-personal-access-token)).

Next, build a Docker image of the **WipeOutR** evaluations with the following command:

```shell
docker build -t wipeoutr-image .
```

Next, create a folder for the experiment results and copy results inside the Docker image to the folder:

```shell
mkdir results
docker run --rm --entrypoint tar wipeoutr-image cC ./results . | tar xvC ./results
```

[//]: # (### Use a CodeOcean capsule)

[//]: # ()
[//]: # (The easiest way to reproduce the experiment is to use a [CodeOcean]&#40;https://codeocean.com&#41; capsule.)

[//]: # (You can find our reproducible evaluation of **WipeOutR** algorithms [here]&#40;https://codeocean.com/capsule/5824065/tree/v1&#41;.)

[//]: # ()
[//]: # (> Due to CodeOcean only supports Java 1.8, the code source on CodeOcean has the Java target version set to 1.8, and uses )

[//]: # (> the CA-CDR library version 1.3.8-1, which is a version of the CA-CDR library compatible with Java 1.8.)

[//]: # (> )

[//]: # (> The code source on this GitHub repository has the Java target version set to 17, and uses the CA-CDR library version 1.3.8.)

### Use pre-build Java applications

> **Install Java**
> 
> If you have not installed Java or the Java version isn't the latest one, 
> please go to Java's website at https://www.java.com/en/download/,
> download and install the latest version.

In this way of reproducing the experiment, you need to download the folders *./conf/*, *./data*, and *./shell/* from the repository.
You need also create a *./results* folder for the experiment results.

#### Download the standalone Java applications

| *apps*                                                                                            | *description*                            |
|---------------------------------------------------------------------------------------------------|------------------------------------------|
| [wipeoutr_t.jar](https://github.com/AIG-ist-tugraz/WipeOutR/releases/tag/wipeoutr_t_v1.0)         | **WipeOutR_T** evaluation                |
| [ts_runtime.jar](https://github.com/AIG-ist-tugraz/WipeOutR/releases/tag/ts_runtime_v1.0)         | Execution runtime of a set of test cases |
| [wipeoutr_fm.jar](https://github.com/AIG-ist-tugraz/WipeOutR/releases/tag/wipeoutr_fm_v1.0)       | **WipeOutR_FM** evaluation               |
| [solver_runtime.jar](https://github.com/AIG-ist-tugraz/WipeOutR/releases/tag/solver_runtime_v1.0) | Solution search runtime                  |
| [ts_gen.jar](https://github.com/AIG-ist-tugraz/WipeOutR/releases/tag/ts_gen_v1.0)                 | Test suite generator                     |
| [ts_select.jar](https://github.com/AIG-ist-tugraz/WipeOutR/releases/tag/ts_select_v1.0)           | Scenarios selector                       |
| [rc_gen.jar](https://github.com/AIG-ist-tugraz/WipeOutR/releases/tag/rc_gen_v1.0)                 | Redundant constraints generator          |

Please download these apps, and put them into the *./app* folder.

#### Use bash scripts

To facilitate the evaluation executions, we provide the following eight bash scripts:

| *apps*                      | *description*                                                                                                                                  | *estimated runtime* |
|-----------------------------|------------------------------------------------------------------------------------------------------------------------------------------------|---------------------|
| ```run.sh```                | Executes all evaluations and gets results for two evaluation tables - [Table 5](#wipeoutr_t-evaluation) and [Table 6](#wipeoutr_fm-evaluation) | 5 - 6 days          |
| ```run_wipeoutr_t.sh```     | Executes the **WipeOutR_T** evaluation and gets results for *wr_t_runtime* elements in the [Table 5](#wipeoutr_t-evaluation)                   | 4 - 5 days          |
| ```run_ts_runtime.sh```     | Executes the test cases checks and gets results for *ts_runtime*/*nonred_ts_runtime* elements in the [Table 5](#wipeoutr_t-evaluation)         | 1 - 2 hours         |
| ```run_wipeoutr_fm.sh```    | Executes the **WipeOutR_FM** evaluation and gets results for *wr_fm_runtime* elements in the [Table 6](#wipeoutr_fm-evaluation)                | 4 - 5 hours         |
| ```run_solver_runtime.sh``` | Executes the solution search and gets results for *sol_runtime*/*nonred_sol_runtime* elements in the [Table 6](#wipeoutr_fm-evaluation)        | 2 - 3 minutes       |
| ```run_ts_gen.sh```         | Generates a test suite for the *Linux-2.6.33.3* feature model                                                                                  | 4 - 5 days          |
| ```run_ts_select.sh```      | Selects 12 scenarios with the #T cardinalities of 10, 50, 100, 250, and the redundancy ratios of 0%, 50%, and 90%                              | 1 minute            |
| ```run_rc_gen.sh```         | Generates redundant constraints for the *Linux-2.6.33.3* feature model                                                                         | 15 - 20 minutes     |

To run these bash scripts in your system:

1. First, you need to make the script executable with **chmod**:

```
$ chmod u+x run.sh
```

2. Run the script by prefixing it with "`./` ":

```
$ ./run.sh
```

### Build apps by yourself

> **JDK requirement:** OpenJDK 17.0.2

#### Get the Maven dependencies from the GitHub package repository

Our implementation depends on our [CA-CDR library](https://github.com/manleviet/CA-CDR-V2). Thus, after cloning the source code into your system,
you need to add the below script in your *settings.xml* file (*not the settings.xml attached in the repository*) to download the dependencies from the GitHub package repository.

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
> If you don't have Maven on your computer, please follow the [Maven guide](https://maven.apache.org/download.cgi) to install it.

#### Move *jar* files to the *./app* folder

Use the bash script `move.sh` to move *jar* files to the *./app* folder. The script will create the *./app* folder.

Next, create a *./results* folder to store the results.

Finally, you could execute the evaluations using [bash scripts](#use-bash-scripts).

[//]: # (## Use the code source for your project)

[//]: # ()
[//]: # (> [You need to get the Maven packages of our CA-CDR library]&#40;#get-the-maven-dependencies-from-github-package-repository&#41;)

[//]: # ()
[//]: # ([Examples of using the **WipeOutR** algorithms for automated redundancy detection]&#40;https://github.com/AIG-ist-tugraz/WipeOutR/tree/main/src/test/java/at/tugraz/ist/ase/wipeoutr/algorithm&#41;)

[//]: # ()
[//]: # (### Knowledge bases)

[//]: # ()
[//]: # (The **WipeOutR** algorithms supports two types of knowledge bases: &#40;1&#41; feature models and &#40;2&#41; CSP knowledge bases.)

[//]: # ()
[//]: # (#### Feature models)

[//]: # ()
[//]: # (> We support only basic feature models [3].)

[//]: # ()
[//]: # (Our **fm-package** in **CA-CDR** library supports five feature model formats:)

[//]: # (1. [SPLOT feature models]&#40;splot-research.org&#41;. The file extension could be “.sxfm” or “.splx.”)

[//]: # (2. [FeatureIDE format]&#40;https://featureide.github.io&#41;. The file extension should be “xml.”)

[//]: # (3. v.control format. The feature model format of the v.control tool. The file extension should be "xmi.”)

[//]: # (4. [Glencoe format]&#40;https://glencoe.hochschule-trier.de&#41;. The file extension should be “json.”)

[//]: # (5. [Descriptive format]&#40;https://github.com/manleviet/CA-CDR-V2/blob/main/fm-package/src/test/resources/bamboobike.fm4conf&#41;. Our feature model format. The file extension should be "fm4conf".)

[//]: # ()
[//]: # ()
[//]: # ()
[//]: # (You could use [**FMParserFactory**]&#40;https://github.com/manleviet/CA-CDR-V2/blob/main/fm-package/src/main/java/at/tugraz/ist/ase/fm/parser/factory/FMParserFactory.java&#41;)

[//]: # (to get the exact )

[//]: # ()
[//]: # (An example of using FMParserFactory to get )

[//]: # ()
[//]: # (### CSP knowledge bases)

[//]: # ()
[//]: # (You need to encode your knowledge base by inheriting the class KB)

[//]: # ()
[//]: # (### WipeOutR_T)

[//]: # ()
[//]: # (To manage constraints/test cases for the **WipeOutR_T** algorithm, we provide the WipeOutRTModel which requires )

[//]: # (two inputs: &#40;1&#41; a feature model and &#40;2&#41; a test suite.)

[//]: # ()
[//]: # ()
[//]: # ()
[//]: # (### WipeOutR_FM)

[//]: # ()
[//]: # (Like the **WipeOutR_T** algorithm, the **WipeOutR_FM** algorithm needs a WipeOutRFMModel )

[//]: # ()
[//]: # (**d2bug_eval** consists of three sub-packages: **Feature Model**, **MBDiagLib**, and **Debugging**.  )

[//]: # (**Feature Model** reads feature model files and supports *feature model generation* and *feature model statistics*. )

[//]: # (**MBDiagLib** provides &#40;1&#41; an abstract model to hold variables and constraints, )

[//]: # (&#40;2&#41; an abstract consistency checker for underlying solvers, )

[//]: # (&#40;3&#41; a *Choco* consistency checker using [Choco Solver]&#40;https://choco-solver.org&#41;, )

[//]: # (and &#40;4&#41; functions to measure the performance of algorithms in terms of run-time or the number of solver calls. )

[//]: # (**Debugging** provides components w.r.t. test-cases management, the DirectDebug implementation, )

[//]: # (a debugging model with test-cases integration, and debugging-related applications &#40;e.g. *test suite generation*, )

[//]: # (*test cases classification*, and *test case selection*&#41;.)

[//]: # ()
[//]: # (Besides feature models encoded in the *SXFM* format and consistency checks conducted )

[//]: # (by [Choco Solver]&#40;https://choco-solver.org&#41;, **d2bug_eval** can be extended to support further formats )

[//]: # (&#40;e.g., *FeatureIDE* format&#41; and other off-the-shelf solvers. )

[//]: # (Furthermore, the program can be extended to evaluate other constraint-based algorithms, )

[//]: # (such as conflict detection algorithms and diagnosis identification algorithms.)

## References

[1] V.M. Le, A. Felfernig, M. Uta, T.N.T. Tran, C. Vidal, WipeOutR: Automated Redundancy Detection for Feature Models, 26th ACM International Systems and Software Product Line Conference (SPLC'22), 2022.

[2] Heradio, R., Fernandez-Amoros, D., Galindo, J.A., et al. Uniform and scalable sampling of highly configurable systems. Empir Software Eng 27, 44 (2022). [https://doi.org/10.1007/s10664-021-10102-5](https://doi.org/10.1007/s10664-021-10102-5)

[3] K. Kang, S. Cohen, J. Hess, W. Novak, and A. Peterson, 'Feature-Oriented Domain Analysis (FODA) Feasibility Study',
Technical Report CMU/SEI-90-TR-021, Software Engineering Institute, Carnegie Mellon University, Pittsburgh, PA, (1990) [link](https://apps.dtic.mil/sti/pdfs/ADA235785.pdf)
