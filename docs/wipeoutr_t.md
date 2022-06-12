# wipeoutr_t.jar

This program measures the runtime of WipeOutR_T's execution.
You can put as many test suites as you want in the *scenarioPath* folder.
The result is an average runtime.

### Usage

**Requirements**: the latest Java

**Syntax**:
```
java -jar wipeoutr_t.jar [-cfg <path-to-configuration-file>]
```

If the parameter `-cfg` isn't specified, the program will find the default configuration file in `./conf/wipeoutr_t.cfg`.

### Configuration file

The configuration file needs the following parameters:

| *parameters*        | *default value*      | *description*                                                                   |
|---------------------|----------------------|---------------------------------------------------------------------------------|
| ```nameKB```        | **null**             | filename of the feature model                                                   |
| ```dataPath```      | **./data/**          | the folder where the dataset is stored                                          |
| ```outputPath```    | **./results/**       | the folder where the results will be saved                                      |
| ```scenarioPath```  | **./data/scenario/** | path to the folder, where you store scenarios you want to measure the runtime |
| ```numIter```       | **3**                | number of iterations                                                            |

For examples on configuring these parameters, we refer to configuration files in the folder *./conf*.