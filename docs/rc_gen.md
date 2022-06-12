# rc_gen.jar

**rc_gen.jar** is a standalone Java application that generates a set of redundant constraints for a given feature model.
Generated redundant constraints includes:
- Excludes constraints between child features of alternative relationships
- Requires constraints from an optional feature to a mandatory feature 
Output constraints are encoded using the *FeatureIDE* format.

### Usage

**Requirements**: the latest Java

**Syntax**:
```
java -jar rc_gen.jar [-cfg <path-to-configuration-file>]
```

If the parameter `-cfg` isn't specified, the program will find the default configuration file in `./conf/rc_gen.cfg`.

### Configuration file

The configuration file needs the following parameters:

| *parameters* | *default value* | *description*                                                                                                                            |
| ----------- |-----------------|------------------------------------------------------------------------------------------------------------------------------------------|
| ```nameKB``` | **null**        | filename of the feature model                                                                                                            |
| ```dataPath``` | **./data/**     | the folder where the dataset is stored                                                                                                   |
| ```outputPath``` | **./results/**  | the folder where the results will be saved                                                                                               |

For examples on configuring these parameters, we refer to configuration files in the folder *./conf*.