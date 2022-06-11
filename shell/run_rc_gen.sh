#
# WipeOutR: Automated Redundancy Detection for Feature Models
#
# Copyright (c) 2022-2022 AIG team, Institute for Software Technology,
# Graz University of Technology, Austria
#
# Contact: http://ase.ist.tugraz.at/ASE/
#

echo "Redundant constraints generation"
echo ""

java -jar ../app/rc_gen.jar -cfg ../conf/rc_gen.cfg

echo "DONE"