# solver_runtime.jar

**solver_runtime.jar** is a standalone Java application that measures solver runtime to find a configuration for a given feature model.

### Usage

**Requirements**: the latest Java

**Syntax**:
```
java -jar solver_runtime.jar [-cfg <path-to-configuration-file>]
```

If the parameter `-cfg` has not been specified, the program will find the default configuration file in `./conf/solver_runtime.cfg`.

### Configuration file

The configuration file needs the following parameters:

| *parameters*                     | *default value* | *description*                                                                                                                                                                                                    |
|----------------------------------|-----------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| ```nameKB```                     | **null**        | the filename of the feature model                                                                                                                                                                                |
| ```dataPath```                   | **./data/**     | the folder where the dataset is stored                                                                                                                                                                           |
| ```outputPath```                 | **./results/**  | the folder where the results will be saved                                                                                                                                                                       |
| ```hasNonRedundantConstraints``` | **false**       | ```true``` - evaluate redundancy-free model. The program will load non-redundant constraints from a file with the same name as the knowledge base, but with the suffix "_nonred", stored in the *./data* folder. |
| ```numIter```                    | **3**           | the number of iterations                                                                                                                                                                                         |

For examples of configuring these parameters, we refer to configuration files in the folder *./conf*.
