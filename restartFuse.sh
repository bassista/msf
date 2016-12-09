#!/bin/sh

# Update $FUSE_ARCHIVE_PATH and FUSE_INSTALL_PATH below before running!
FUSE_ARCHIVE_PATH=/Users/bibryam/_archive/jboss-fuse-full-6.2.1.redhat-084.zip
FUSE_INSTALL_PATH=/Users/bibryam/Desktop/
FUSE_INSTALL_FOLDER="jboss-fuse-6.2.1.redhat-084/"

jps -lm | grep karaf | grep -v grep | awk '{print $1}' | xargs kill -KILL

rm -rf ${FUSE_INSTALL_PATH}${FUSE_INSTALL_FOLDER}

unzip $FUSE_ARCHIVE_PATH -d $FUSE_INSTALL_PATH

sed -i'' -e 's/#admin/admin/' ${FUSE_INSTALL_PATH}${FUSE_INSTALL_FOLDER}etc/users.properties

cd ${FUSE_INSTALL_PATH}${FUSE_INSTALL_FOLDER}bin/

./start

sleep 120

./client
