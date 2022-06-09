#
# WipeOutR: Automated Redundancy Detection for Feature Models
#
# Copyright (c) 2022 AIG team, Institute for Software Technology,
# Graz University of Technology, Austria
#
# Contact: http://ase.ist.tugraz.at/ASE/
#

echo "Scenarios selection"
echo ""

java -jar target/ts_select-jar-with-dependencies.jar -cfg ./conf/ts_select.cfg

echo "DONE"