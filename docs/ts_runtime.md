# ts_runtime.jar

**ts_runtime.jar** is a standalone Java application that measures test case execution run-times.

### Usage

**Requirements**: the latest Java

**Syntax**:
```
java -jar ts_runtime.jar [-cfg <path-to-configuration-file>]
```

If the parameter `-cfg` isn't specified, the program will find the default configuration file in `./conf/ts_runtime.cfg`.

### Configuration file

The configuration file needs the following parameters:

| *parameters*        | *default value*      | *description*                                                                 |
|---------------------|----------------------|-------------------------------------------------------------------------------|
| ```nameKB```        | **null**             | filename of the feature model                                                 |
| ```dataPath```      | **./data/**          | the folder where the dataset is stored                                        |
| ```outputPath```    | **./results/**       | the folder where the results will be saved                                    |
| ```scenarioPath```  | **./data/scenario/** | path to the folder, where you store scenarios you want to measure the runtime |
| ```numIter```       | **3**                | number of iterations                                                          |

For examples on configuring these parameters, we refer to configuration files in the folder *./conf*.