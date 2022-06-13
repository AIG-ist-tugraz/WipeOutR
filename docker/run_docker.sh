#
# WipeOutR: Automated Redundancy Detection for Feature Models
#
# Copyright (c) 2022-2022 AIG team, Institute for Software Technology,
# Graz University of Technology, Austria
#
# Contact: http://ase.ist.tugraz.at/ASE/
#

echo "Test cases Runtime"
echo ""

echo "--------------------"
echo "|T| = 10, red.% = 0"
echo ""
java -jar ./target/ts_runtime.jar -cfg ./docker/ts_runtime/ts_runtime_10_0.cfg
echo "--------------------"
echo "|T| = 10, red.% = 50"
echo ""
java -jar ./target/ts_runtime.jar -cfg ./docker/ts_runtime/ts_runtime_10_50.cfg
java -jar ./target/ts_runtime.jar -cfg ./docker/ts_runtime/ts_runtime_10_50_nonred.cfg
echo "--------------------"
echo "|T| = 10, red.% = 90"
echo ""
java -jar ./target/ts_runtime.jar -cfg ./docker/ts_runtime/ts_runtime_10_90.cfg
java -jar ./target/ts_runtime.jar -cfg ./docker/ts_runtime/ts_runtime_10_90_nonred.cfg

echo "--------------------"
echo "|T| = 50, red.% = 0"
echo ""
java -jar ./target/ts_runtime.jar -cfg ./docker/ts_runtime/ts_runtime_50_0.cfg
echo "--------------------"
echo "|T| = 50, red.% = 50"
echo ""
java -jar ./target/ts_runtime.jar -cfg ./docker/ts_runtime/ts_runtime_50_50.cfg
java -jar ./target/ts_runtime.jar -cfg ./docker/ts_runtime/ts_runtime_50_50_nonred.cfg
echo "--------------------"
echo "|T| = 50, red.% = 90"
echo ""
java -jar ./target/ts_runtime.jar -cfg ./docker/ts_runtime/ts_runtime_50_90.cfg
java -jar ./target/ts_runtime.jar -cfg ./docker/ts_runtime/ts_runtime_50_90_nonred.cfg

echo "--------------------"
echo "|T| = 100, red.% = 0"
echo ""
java -jar ./target/ts_runtime.jar -cfg ./docker/ts_runtime/ts_runtime_100_0.cfg
echo "--------------------"
echo "|T| = 100, red.% = 50"
echo ""
java -jar ./target/ts_runtime.jar -cfg ./docker/ts_runtime/ts_runtime_100_50.cfg
java -jar ./target/ts_runtime.jar -cfg ./docker/ts_runtime/ts_runtime_100_50_nonred.cfg
echo "--------------------"
echo "|T| = 100, red.% = 90"
echo ""
java -jar ./target/ts_runtime.jar -cfg ./docker/ts_runtime/ts_runtime_100_90.cfg
java -jar ./target/ts_runtime.jar -cfg ./docker/ts_runtime/ts_runtime_100_90_nonred.cfg

echo "--------------------"
echo "|T| = 250, red.% = 0"
echo ""
java -jar ./target/ts_runtime.jar -cfg ./docker/ts_runtime/ts_runtime_250_0.cfg
echo "--------------------"
echo "|T| = 250, red.% = 50"
echo ""
java -jar ./target/ts_runtime.jar -cfg ./docker/ts_runtime/ts_runtime_250_50.cfg
java -jar ./target/ts_runtime.jar -cfg ./docker/ts_runtime/ts_runtime_250_50_nonred.cfg
echo "--------------------"
echo "|T| = 250, red.% = 90"
echo ""
java -jar ./target/ts_runtime.jar -cfg ./docker/ts_runtime/ts_runtime_250_90.cfg
java -jar ./target/ts_runtime.jar -cfg ./docker/ts_runtime/ts_runtime_250_90_nonred.cfg

echo ""
echo "Test cases Runtime - DONE"
echo "-------------------------"
echo ""
echo "Solver Runtime"
echo ""

echo "--------------------"
echo "|CF| = 13,972, solv. red."
echo ""
java -jar ./target/solver_runtime.jar -cfg ./docker/solver_runtime/solver_runtime_13972_red.cfg
echo "--------------------"
echo "|CF| = 13,972, solv. non-red."
echo ""
java -jar ./target/solver_runtime.jar -cfg ./docker/solver_runtime/solver_runtime_13972_nonred.cfg

echo "--------------------"
echo "|CF| = 18,342, solv. red."
echo ""
java -jar ./target/solver_runtime.jar -cfg ./docker/solver_runtime/solver_runtime_18342_red.cfg
echo "--------------------"
echo "|CF| = 18,342, solv. non-red."
echo ""
java -jar ./target/solver_runtime.jar -cfg ./docker/solver_runtime/solver_runtime_18342_nonred.cfg

echo "--------------------"
echo "|CF| = 30,572, solv. red."
echo ""
java -jar ./target/solver_runtime.jar -cfg ./docker/solver_runtime/solver_runtime_30572_red.cfg
echo "--------------------"
echo "|CF| = 30,572, solv. non-red."
echo ""
java -jar ./target/solver_runtime.jar -cfg ./docker/solver_runtime/solver_runtime_30572_nonred.cfg

echo ""
echo "Solver Runtime - DONE"
echo "-------------------------"
echo ""
echo "WipeOutR_FM Evalutions"
echo ""

echo "--------------------"
echo "|CF| = 13,972"
echo ""
java -jar ./target/wipeoutr_fm.jar -cfg ./docker/wipeoutr_fm/wipeoutr_fm_13972.cfg

echo "--------------------"
echo "|CF| = 18,342"
echo ""
java -jar ./target/wipeoutr_fm.jar -cfg ./docker/wipeoutr_fm/wipeoutr_fm_18342.cfg

echo "--------------------"
echo "|CF| = 30,572"
echo ""
java -jar ./target/wipeoutr_fm.jar -cfg ./docker/wipeoutr_fm/wipeoutr_fm_30572.cfg

echo ""
echo "WipeOutR_FM Evalutions - DONE"
echo "-------------------------"
echo ""
echo "WipeOutR_T evaluations"
echo ""

echo "--------------------"
echo "|T| = 10, red.% = 90"
echo ""
java -jar ./target/wipeoutr_t.jar -cfg ./docker/wipeoutr_t/wipeoutr_t_10_90.cfg
echo "--------------------"
echo "|T| = 10, red.% = 50"
echo ""
java -jar ./target/wipeoutr_t.jar -cfg ./docker/wipeoutr_t/wipeoutr_t_10_50.cfg
echo "--------------------"
echo "|T| = 10, red.% = 0"
echo ""
java -jar ./target/wipeoutr_t.jar -cfg ./docker/wipeoutr_t/wipeoutr_t_10_0.cfg

echo "--------------------"
echo "|T| = 50, red.% = 90"
echo ""
java -jar ./target/wipeoutr_t.jar -cfg ./docker/wipeoutr_t/wipeoutr_t_50_90.cfg
echo "--------------------"
echo "|T| = 50, red.% = 50"
echo ""
java -jar ./target/wipeoutr_t.jar -cfg ./docker/wipeoutr_t/wipeoutr_t_50_50.cfg
echo "--------------------"
echo "|T| = 50, red.% = 0"
echo ""
java -jar ./target/wipeoutr_t.jar -cfg ./docker/wipeoutr_t/wipeoutr_t_50_0.cfg

echo "--------------------"
echo "|T| = 100, red.% = 90"
echo ""
java -jar ./target/wipeoutr_t.jar -cfg ./docker/wipeoutr_t/wipeoutr_t_100_90.cfg
echo "--------------------"
echo "|T| = 100, red.% = 50"
echo ""
java -jar ./target/wipeoutr_t.jar -cfg ./docker/wipeoutr_t/wipeoutr_t_100_50.cfg
echo "--------------------"
echo "|T| = 100, red.% = 0"
echo ""
java -jar ./target/wipeoutr_t.jar -cfg ./docker/wipeoutr_t/wipeoutr_t_100_0.cfg

echo "--------------------"
echo "|T| = 250, red.% = 90"
echo ""
java -jar ./target/wipeoutr_t.jar -cfg ./docker/wipeoutr_t/wipeoutr_t_250_90.cfg
echo "--------------------"
echo "|T| = 250, red.% = 50"
echo ""
java -jar ./target/wipeoutr_t.jar -cfg ./docker/wipeoutr_t/wipeoutr_t_250_50.cfg
echo "--------------------"
echo "|T| = 250, red.% = 0"
echo ""
java -jar ./target/wipeoutr_t.jar -cfg ./docker/wipeoutr_t/wipeoutr_t_250_0.cfg

echo ""
echo "WipeOutR_T evaluations - DONE"