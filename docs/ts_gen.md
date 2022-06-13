# ts_gen.jar

**ts_gen.jar** is a standalone Java application that generates a test suite for a given feature model.
The test suite consists of five types of test cases: dead features, false optional, full mandatory, false mandatory, and partial configuration. Each partial configuration refers to 2-5 randomly features.

### Supported feature models

1. [SPLOT feature models](splot-research.org). The file extension could be “.sxfm” or “.splx.”
2. [FeatureIDE format](https://featureide.github.io). The file extension should be “xml.”
3. v.control format. The feature model format of the v.control tool. The file extension should be “xmi.”
4. [Glencoe format](https://glencoe.hochschule-trier.de). The file extension should be “json.”
5. [Descriptive format](https://github.com/manleviet/CA-CDR-V2/blob/main/fm-package/src/test/resources/bamboobike.fm4conf). Our feature model format. The file extension should be “fm4conf”.

### Usage

**Requirements**: the latest Java

**Syntax**:
```
java -jar ts_gen.jar [-cfg <path-to-configuration-file>]
```

If the parameter `-cfg` has not been specified, the program will find the default configuration file in `./conf/ts_gen.cfg`.

### Configuration file

The configuration file needs the following parameters:

| *parameters* | *default value* | *description*                                                                                                                                |
| ----------- |-----------------|----------------------------------------------------------------------------------------------------------------------------------------------|
| ```nameKB``` | **null**        | the filename of the feature model                                                                                                            |
| ```dataPath``` | **./data/**     | the folder where the dataset is stored                                                                                                       |
| ```outputPath``` | **./results/**  | the folder where the results will be saved                                                                                                   |
| ```maxCombinations``` | **3000**        | the maximum number of combinations to generate for each cardinality of features (used in the generation of partial configuration test cases) |
| ```randomlySearch``` | **false**       | whether the program randomly selects combinations from a list of combinations (used in the generation of partial configuration test cases)   |
| ```maxFeaturesInTestCase``` | **5**           | the maximum number of features in a test case (used in the generation of partial configuration test cases)                                   |

For examples of configuring these parameters, we refer to configuration files in the folder *./conf*.

## Test suite file structure

A test suite file starts with the six-lines header. Each header line has a number that represents:
- 1st line - the total number of test cases,
- 2nd line - the number of dead features test cases,
- 3rd line - the number of false optional test cases,
- 4th line - the number of full mandatory test cases,
- 5th line - the number of false mandatory test cases, and
- 6th line - the number of partial configuration test cases.

Following the header, each line represents a test case.
