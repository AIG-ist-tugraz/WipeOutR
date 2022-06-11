#
# WipeOutR: Automated Redundancy Detection for Feature Models
#
# Copyright (c) 2022-2022 AIG team, Institute for Software Technology,
# Graz University of Technology, Austria
#
# Contact: http://ase.ist.tugraz.at/ASE/
#

echo "WipeOutR_FM Evalutions"
echo ""

echo "--------------------"
echo "|CF| = 13,972"
echo ""
java -jar ../app/wipeoutr_fm.jar -cfg ../conf/wipeoutr_fm/wipeoutr_fm_13972.cfg

echo "--------------------"
echo "|CF| = 18,342"
echo ""
java -jar ../app/wipeoutr_fm.jar -cfg ../conf/wipeoutr_fm/wipeoutr_fm_18342.cfg

echo "--------------------"
echo "|CF| = 30,572"
echo ""
java -jar ../app/wipeoutr_fm.jar -cfg ../conf/wipeoutr_fm/wipeoutr_fm_30572.cfg

echo ""
echo "DONE"