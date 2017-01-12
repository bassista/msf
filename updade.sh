#!/bin/sh

# Update $FUSE_ARCHIVE_PATH and FUSE_INSTALL_PATH below before running!
FUSE_ARCHIVE_PATH=/Users/bibryam/_archive/jboss-fuse-full-6.2.1.redhat-084.zip
FUSE_INSTALL_PATH=/Users/bibryam/Desktop/
FUSE_INSTALL_FOLDER="jboss-fuse-6.2.1.redhat-084/"

KARAF_USER=admin
KARAF_PASSWORD=admin
CLIENT_INVOCATION_MACRO="${FUSE_INSTALL_PATH}${FUSE_INSTALL_FOLDER}bin/client -u $KARAF_USER -p $KARAF_PASSWORD -r 60"
CLONE_DIR=/tmp
PROJECT_CONFIGS=/Users/bibryam/Desktop/msf/deployment/configs/profiles/*

update_fabric_profile (){
	local version=$1
	local old_path=`pwd`
	rm -rf $CLONE_DIR/tmp-git

    git_host=$($CLIENT_INVOCATION_MACRO "fabric:cluster-list git" | grep 'http://' | awk '{print $NF;}' | cut -c 8-)
    echo "git_host $git_host"

	git clone -b $version http://$KARAF_USER:$KARAF_PASSWORD@$git_host $CLONE_DIR/tmp-git
	cd $CLONE_DIR/tmp-git/
	git checkout -b $version
    cp -rf $PROJECT_CONFIGS ./fabric/profiles/

	git add *
	git config user.email "ESB@openairlines.org"
	git config user.name "ESB deployer process"
	git commit -a -m "Update Fuse configuration"
	git push origin $version
#    rm -rf $CLONE_DIR/tmp-git
#	cd $old_path
}



update_fabric_profile 1.3
