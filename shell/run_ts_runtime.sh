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
java -jar ../app/ts_runtime.jar -cfg ../conf/ts_runtime/ts_runtime_10_0.cfg
echo "--------------------"
echo "|T| = 10, red.% = 50"
echo ""
java -jar ../app/ts_runtime.jar -cfg ../conf/ts_runtime/ts_runtime_10_50.cfg
java -jar ../app/ts_runtime.jar -cfg ../conf/ts_runtime/ts_runtime_10_50_nonred.cfg
echo "--------------------"
echo "|T| = 10, red.% = 90"
echo ""
java -jar ../app/ts_runtime.jar -cfg ../conf/ts_runtime/ts_runtime_10_90.cfg
java -jar ../app/ts_runtime.jar -cfg ../conf/ts_runtime/ts_runtime_10_90_nonred.cfg

echo "--------------------"
echo "|T| = 50, red.% = 0"
echo ""
java -jar ../app/ts_runtime.jar -cfg ../conf/ts_runtime/ts_runtime_50_0.cfg
echo "--------------------"
echo "|T| = 50, red.% = 50"
echo ""
java -jar ../app/ts_runtime.jar -cfg ../conf/ts_runtime/ts_runtime_50_50.cfg
java -jar ../app/ts_runtime.jar -cfg ../conf/ts_runtime/ts_runtime_50_50_nonred.cfg
echo "--------------------"
echo "|T| = 50, red.% = 90"
echo ""
java -jar ../app/ts_runtime.jar -cfg ../conf/ts_runtime/ts_runtime_50_90.cfg
java -jar ../app/ts_runtime.jar -cfg ../conf/ts_runtime/ts_runtime_50_90_nonred.cfg

echo "--------------------"
echo "|T| = 100, red.% = 0"
echo ""
java -jar ../app/ts_runtime.jar -cfg ../conf/ts_runtime/ts_runtime_100_0.cfg
echo "--------------------"
echo "|T| = 100, red.% = 50"
echo ""
java -jar ../app/ts_runtime.jar -cfg ../conf/ts_runtime/ts_runtime_100_50.cfg
java -jar ../app/ts_runtime.jar -cfg ../conf/ts_runtime/ts_runtime_100_50_nonred.cfg
echo "--------------------"
echo "|T| = 100, red.% = 90"
echo ""
java -jar ../app/ts_runtime.jar -cfg ../conf/ts_runtime/ts_runtime_100_90.cfg
java -jar ../app/ts_runtime.jar -cfg ../conf/ts_runtime/ts_runtime_100_90_nonred.cfg

echo "--------------------"
echo "|T| = 250, red.% = 0"
echo ""
java -jar ../app/ts_runtime.jar -cfg ../conf/ts_runtime/ts_runtime_250_0.cfg
echo "--------------------"
echo "|T| = 250, red.% = 50"
echo ""
java -jar ../app/ts_runtime.jar -cfg ../conf/ts_runtime/ts_runtime_250_50.cfg
java -jar ../app/ts_runtime.jar -cfg ../conf/ts_runtime/ts_runtime_250_50_nonred.cfg
echo "--------------------"
echo "|T| = 250, red.% = 90"
echo ""
java -jar ../app/ts_runtime.jar -cfg ../conf/ts_runtime/ts_runtime_250_90.cfg
java -jar ../app/ts_runtime.jar -cfg ../conf/ts_runtime/ts_runtime_250_90_nonred.cfg

echo ""
echo "DONE"