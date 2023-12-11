#!/bin/bash
# zavier.zhou@20220225

set -e
function build_qssi()
{
(	cd ./QSSI.12/
	source build/envsetup.sh && lunch qssi-userdebug && bash build.sh dist --qssi_only EXPERIMENTAL_USE_OPENJDK9=1.8 $1
)
}

function build_target()
{
(	cd ./UM.9.15/
	source build/envsetup.sh && lunch bengal-userdebug && bash build.sh dist --target_only EXPERIMENTAL_USE_OPENJDK9=1.8 $1
)
}

function build_super()
{
(	cd ./UM.9.15/
	python vendor/qcom/opensource/core-utils/build/build_image_standalone.py --image super --qssi_build_path ../QSSI.12/ --target_build_path ./ --merged_build_path ./ --target_lunch bengal --output_ota
)
}

build_type="--all"
j_arg=""

while [ $# -gt 0 ];do
	case "$1" in
		"--qssi"|"--target"|"--super"|"--all")
			build_type=$1
			;;
		"-j"[0-9]*)
			j_arg=$1
			;;
	esac
	shift
done

echo $0 "$build_type""$j_arg"

case "$build_type" in
	"--qssi")
		build_qssi $j_arg
		;;
	"--target")
		build_target $j_arg
		;;
	"--super")
		build_super
		;;
	"--all")
		build_qssi $j_arg
		build_target $j_arg
		build_super
		;;

esac
