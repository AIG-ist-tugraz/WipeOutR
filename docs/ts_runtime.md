# ts_runtime.jar

**ts_runtime.jar** is a standalone Java application that measures test case execution run-times.

### Usage

**Requirements**: the latest Java

**Syntax**:
```
java -jar ts_runtime.jar [-cfg <path-to-configuration-file>]
```

If the parameter `-cfg` has not been specified, the program will find the default configuration file in `./conf/ts_runtime.cfg`.

### Configuration file

The configuration file needs the following parameters:

| *parameters*        | *default value*      | *description*                                                             |
|---------------------|----------------------|---------------------------------------------------------------------------|
| ```nameKB```        | **null**             | the filename of the feature model                                         |
| ```dataPath```      | **./data/**          | the folder where the dataset is stored                                    |
| ```outputPath```    | **./results/**       | the folder where the results will be saved                                |
| ```scenarioPath```  | **./data/scenario/** | the path to the folder storing scenarios that need to measure the runtime |
| ```numIter```       | **3**                | the number of iterations                                                  |

For examples of configuring these parameters, we refer to configuration files in the folder *./conf*.
