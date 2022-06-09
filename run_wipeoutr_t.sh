#
# WipeOutR: Automated Redundancy Detection for Feature Models
#
# Copyright (c) 2022 AIG team, Institute for Software Technology,
# Graz University of Technology, Austria
#
# Contact: http://ase.ist.tugraz.at/ASE/
#

echo "WipeOutR_T evaluations"
echo ""

echo "--------------------"
echo "|T| = 10, red.% = 90"
echo ""
java -jar target/wipeoutr_t-jar-with-dependencies.jar -cfg ./conf/wipeoutr_t/wipeoutr_t_10_90.cfg
echo "--------------------"
echo "|T| = 10, red.% = 50"
echo ""
java -jar target/wipeoutr_t-jar-with-dependencies.jar -cfg ./conf/wipeoutr_t/wipeoutr_t_10_50.cfg
echo "--------------------"
echo "|T| = 10, red.% = 0"
echo ""
java -jar target/wipeoutr_t-jar-with-dependencies.jar -cfg ./conf/wipeoutr_t/wipeoutr_t_10_0.cfg

echo "--------------------"
echo "|T| = 50, red.% = 90"
echo ""
java -jar target/wipeoutr_t-jar-with-dependencies.jar -cfg ./conf/wipeoutr_t/wipeoutr_t_50_90.cfg
echo "--------------------"
echo "|T| = 50, red.% = 50"
echo ""
java -jar target/wipeoutr_t-jar-with-dependencies.jar -cfg ./conf/wipeoutr_t/wipeoutr_t_50_50.cfg
echo "--------------------"
echo "|T| = 50, red.% = 0"
echo ""
java -jar target/wipeoutr_t-jar-with-dependencies.jar -cfg ./conf/wipeoutr_t/wipeoutr_t_50_0.cfg

echo "--------------------"
echo "|T| = 100, red.% = 90"
echo ""
java -jar target/wipeoutr_t-jar-with-dependencies.jar -cfg ./conf/wipeoutr_t/wipeoutr_t_100_90.cfg
echo "--------------------"
echo "|T| = 100, red.% = 50"
echo ""
java -jar target/wipeoutr_t-jar-with-dependencies.jar -cfg ./conf/wipeoutr_t/wipeoutr_t_100_50.cfg
echo "--------------------"
echo "|T| = 100, red.% = 0"
echo ""
java -jar target/wipeoutr_t-jar-with-dependencies.jar -cfg ./conf/wipeoutr_t/wipeoutr_t_100_0.cfg

echo "--------------------"
echo "|T| = 250, red.% = 90"
echo ""
java -jar target/wipeoutr_t-jar-with-dependencies.jar -cfg ./conf/wipeoutr_t/wipeoutr_t_250_90.cfg
echo "--------------------"
echo "|T| = 250, red.% = 50"
echo ""
java -jar target/wipeoutr_t-jar-with-dependencies.jar -cfg ./conf/wipeoutr_t/wipeoutr_t_250_50.cfg
echo "--------------------"
echo "|T| = 250, red.% = 0"
echo ""
java -jar target/wipeoutr_t-jar-with-dependencies.jar -cfg ./conf/wipeoutr_t/wipeoutr_t_250_0.cfg

echo ""
echo "DONE"