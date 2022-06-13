# wipeoutr_fm.jar

This program measures the runtime of WipeOutR_FM's execution for a given feature model.

### Usage

**Requirements**: the latest Java

**Syntax**:
```
java -jar wipeoutr_fm.jar [-cfg <path-to-configuration-file>]
```

If the parameter `-cfg` has not been specified, the program will find the default configuration file in `./conf/wipeoutr_fm.cfg`.

### Configuration file

The configuration file needs the following parameters:

| *parameters*     | *default value* | *description*                              |
|------------------|-----------------|--------------------------------------------|
| ```nameKB```     | **null**        | the filename of the feature model          |
| ```dataPath```   | **./data/**     | the folder where the dataset is stored     |
| ```outputPath``` | **./results/**  | the folder where the results will be saved |
| ```numIter```    | **3**           | the number of iterations                   |

For examples of configuring these parameters, we refer to configuration files in the folder *./conf*.
