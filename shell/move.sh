#
# WipeOutR: Automated Redundancy Detection for Feature Models
#
# Copyright (c) 2022-2022 AIG team, Institute for Software Technology,
# Graz University of Technology, Austria
#
# Contact: http://ase.ist.tugraz.at/ASE/
#

echo "Move and rename"
echo ""

mkdir ../app
mv ../target/rc_gen-jar-with-dependencies.jar ../app/rc_gen.jar
mv ../target/solver_runtime-jar-with-dependencies.jar ../app/solver_runtime.jar
mv ../target/ts_gen-jar-with-dependencies.jar ../app/ts_gen.jar
mv ../target/ts_runtime-jar-with-dependencies.jar ../app/ts_runtime.jar
mv ../target/ts_select-jar-with-dependencies.jar ../app/ts_select.jar
mv ../target/wipeoutr_fm-jar-with-dependencies.jar ../app/wipeoutr_fm.jar
mv ../target/wipeoutr_t-jar-with-dependencies.jar ../app/wipeoutr_t.jar

echo "DONE"