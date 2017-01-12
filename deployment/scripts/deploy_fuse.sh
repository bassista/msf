#!/bin/bash 
#
# Creates full Fabric environemnet
#

set -x  # enable logging

if [ "x$PROJECT_CONFIGS" = "x" ]; then
	PROJECT_CONFIGS=/opt/rh/fuse/openairlines/configs
fi

if [ "x$OFFLINE_REPO" = "x" ]; then
	OFFLINE_REPO=/opt/rh/fuse/openairlines/m2repo
fi

if [ "x$SYSTEM_USER" = "x" ]; then
	SYSTEM_USER=esb
fi

if [ "x$PASSWORD" = "x" ]; then
	PASSWORD=esb
fi

if [ "x$PROJECT_HOME" = "x" ]; then
	PROJECT_HOME=/home/$SYSTEM_USER
fi

if [ "x$CONTAINER_PATH" = "x" ]; then
	CONTAINER_PATH=/home/esb/containers
fi

if [ "x$FUSE_VERSION" = "x" ]; then
#	FUSE_VERSION=6.1.0.redhat-379
	FUSE_VERSION=6.1.1.redhat-412
fi

if [ "x$FUSE_HOME" = "x" ]; then
	FUSE_HOME=${CONTAINER_PATH}/jboss-fuse-${FUSE_VERSION}
fi

############################################## Karaf commands ##############################################
KARAF_USER=admin
KARAF_PASSWORD=admin
CLIENT_INVOCATION_MACRO="$FUSE_HOME/bin/client -u $KARAF_USER -p $KARAF_PASSWORD -r 60"

############################################## Helper Functions ##############################################

update_fabric_profile (){
	local version=$1
	local old_path=`pwd`
	rm -rf $PROJECT_HOME/tmp-git

    git_host=$($CLIENT_INVOCATION_MACRO "fabric:cluster-list git" | grep 'http://' | awk '{print $NF;}' | cut -c 8-)
    echo "git_host $git_host"

	git clone -b $version http://$KARAF_USER:$KARAF_PASSWORD@$git_host $PROJECT_HOME/tmp-git
	cd $PROJECT_HOME/tmp-git/
	git checkout -b $version
    cp -rf $PROJECT_CONFIGS/profiles/* ./fabric/profiles/

	git add *
	git config user.email "ESB@openairlines.org"
	git config user.name "ESB deployer process"
	git commit -a -m "Update Fuse configuration"
	git push origin $version
    rm -rf $PROJECT_HOME/tmp-git
	cd $old_path
}

