#
# WipeOutR: Automated Redundancy Detection for Feature Models
#
# Copyright (c) 2022 AIG team, Institute for Software Technology,
# Graz University of Technology, Austria
#
# Contact: http://ase.ist.tugraz.at/ASE/
#

echo "Solver Runtime"
echo ""

echo "--------------------"
echo "|CF| = 13,972, solv. red."
echo ""
java -jar target/solver_runtime-jar-with-dependencies.jar -cfg ./conf/solver_runtime/solver_runtime_13972_red.cfg
echo "--------------------"
echo "|CF| = 13,972, solv. non-red."
echo ""
java -jar target/solver_runtime-jar-with-dependencies.jar -cfg ./conf/solver_runtime/solver_runtime_13972_nonred.cfg

echo "--------------------"
echo "|CF| = 18,342, solv. red."
echo ""
java -jar target/solver_runtime-jar-with-dependencies.jar -cfg ./conf/solver_runtime/solver_runtime_18342_red.cfg
echo "--------------------"
echo "|CF| = 18,342, solv. non-red."
echo ""
java -jar target/solver_runtime-jar-with-dependencies.jar -cfg ./conf/solver_runtime/solver_runtime_18342_nonred.cfg

echo "--------------------"
echo "|CF| = 30,572, solv. red."
echo ""
java -jar target/solver_runtime-jar-with-dependencies.jar -cfg ./conf/solver_runtime/solver_runtime_30572_red.cfg
echo "--------------------"
echo "|CF| = 30,572, solv. non-red."
echo ""
java -jar target/solver_runtime-jar-with-dependencies.jar -cfg ./conf/solver_runtime/solver_runtime_30572_nonred.cfg

echo ""
echo "DONE"