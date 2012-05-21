#!/bin/bash
set -x

rm /tmp/measuring-Trunk-Nightly.log
start_time=$(date '+%s')
export start_time

#Initialize basic scructure
DIRNAME=`dirname $0`
cd ${DIRNAME}
TRUNK_NIGHTLY_DIRNAME=`pwd`
export BUILD_DESC=trunk-nightly
source init.sh

rm -rf $DIST

if [ ! -z $WORKSPACE ]; then
    #I'm under hudson and have sources here, I need to clone them
    #Clean obsolete sources first
    rm -rf $NB_ALL
    run_and_measure "hg clone -U $WORKSPACE $NB_ALL"
    run_and_measure "hg -R $NB_ALL update $NB_BRANCH"
fi

#if [ $ML_BUILD == 1 ]; then
#    cd $NB_ALL
#    hg clone $ML_REPO $NB_ALL/l10n
#fi

###################################################################
#
# Build all the components
#
###################################################################

cd $TRUNK_NIGHTLY_DIRNAME
run_and_measure "bash build-all-components.sh" "build-all-components.sh in total"
ERROR_CODE=$?

if [ $ERROR_CODE != 0 ]; then
    echo "ERROR: $ERROR_CODE - Build failed"
    exit $ERROR_CODE;
fi

###################################################################
#
# Pack all the components
#
###################################################################

cd $TRUNK_NIGHTLY_DIRNAME
run_and_measure "bash pack-all-components.sh" "pack-all-components.sh in total"
ERROR_CODE=$?

if [ $ERROR_CODE != 0 ]; then
    echo "ERROR: $ERROR_CODE - Packaging failed"
    exit $ERROR_CODE;
fi

###################################################################
#
# Deploy bits to the storage server
#
###################################################################

if [ -n $BUILD_ID ]; then
    mkdir -p $DIST_SERVER2/${BUILD_ID}
    run_and_measure "cp -rp $DIST/*  $DIST_SERVER2/${BUILD_ID}" "Deploy bits to the storage server"
    if [ -n "${TESTING_SCRIPT}" ]; then
        cd $NB_ALL
        TIP_REV=`hg tip --template "{node}"`
        ssh $TESTING_SCRIPT $TIP_REV
        cd $DIRNAME
    fi
fi

run_and_measure
if [ $UPLOAD_ML == 1 ]; then
    cp $DIST/zip/$BASENAME-platform-src.zip $DIST/ml/zip/
    cp $DIST/zip/$BASENAME-src.zip $DIST/ml/zip/
    cp $DIST/zip/$BASENAME-javadoc.zip $DIST/ml/zip/
    cp $DIST/zip/hg-l10n-$BUILDNUMBER.zip $DIST/ml/zip/
    cp $DIST/zip/ide-l10n-$BUILDNUMBER.zip $DIST/ml/zip/
    cp $DIST/zip/stable-UC-l10n-$BUILDNUMBER.zip $DIST/ml/zip/
    cp $DIST/zip/testdist-$BUILDNUMBER.zip $DIST/ml/zip/
fi
run_and_measure

cd $TRUNK_NIGHTLY_DIRNAME
run_and_measure "bash build-nbi.sh" "build-nbi in total"
ERROR_CODE=$?

if [ $ERROR_CODE != 0 ]; then
    echo "ERROR: $ERROR_CODE - NBI installers build failed"
    exit $ERROR_CODE;
fi

if [ -n $BUILD_ID ]; then
    mkdir -p $DIST_SERVER2/${BUILD_ID}
    run_and_measure "cp -rp $DIST/*  $DIST_SERVER2/${BUILD_ID}"
    run_and_measure "rm $DIST_SERVER2/latest.old"
    run_and_measure "mv $DIST_SERVER2/latest $DIST_SERVER2/latest.old"
    ln -s $DIST_SERVER2/${BUILD_ID} $DIST_SERVER2/latest
    if [ $UPLOAD_ML == 0 -a ML_BUILD != 0 ]; then
        run_and_measure "rm -r $DIST/ml"
    fi
fi

if [ $UPLOAD_ML == 1 ]; then
    run_and_measure "mv $DIST/jnlp $DIST/ml/"
    run_and_measure "mv $DIST/javadoc $DIST/ml/"
fi

if [ -z $DIST_SERVER ]; then
    exit 0;
fi

cd $TRUNK_NIGHTLY_DIRNAME
run_and_measure "bash upload-bits.sh"
