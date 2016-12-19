## Managed Service Factory for Camel Routes

#### Start JBoss Fuse
Download and unzip Fuse. Enable (uncomment) admin user in etc/users.properties.
Kill other running Fuse instances, and then start Fuse as a service, and when it is up, connect to it from CLI client.
All of the above can also be done by (first setting variables and then) running ./restartFuse.sh

#### Run the demo with Fuse Fabric

    # verify Fuse is up and create Fabric with Camel profile
    wait-for-service -t 300000 io.fabric8.api.BootstrapComplete
    fabric:create --wait-for-provisioning --profile fabric --profile feature-camel

    # create msf profile with the Managed Service Factory bundle in it
    profile-create --parent feature-camel msf
    profile-edit --bundle mvn:com.demo/msf/1.0.0 msf

    # instantiate s1 Camel route by providing properties only
    profile-create --parent msf s1
    profile-edit --pid org.apache.camel.msf-s1/camelContextId=serviceOne s1
    profile-edit --pid org.apache.camel.msf-s1/from=timer://foo?fixedRate=true&period=1000 s1
    profile-edit --pid org.apache.camel.msf-s1/to=log:ticketServer?showBody=true s1
    container-add-profile root s1

    # instantiate a different s2 Camel route by providing properties only
    profile-create --parent msf s2
    profile-edit --pid org.apache.camel.msf-s2/camelContextId=serviceTwo s2
    profile-edit --pid org.apache.camel.msf-s2/from=timer://foo?fixedRate=true&period=2000 s2
    profile-edit --pid org.apache.camel.msf-s2/to=log:ticketServer?showBody=true s2
    container-add-profile root s2

    # list all Camel routes. Should display two routes
    watch camel:context-list

    # uninstall s1 profile. Should display only s2 routes
    container-remove-profile root s1
    watch camel:context-list

    # uninstall s2 profile. Should remove all routes
    container-remove-profile root s2
    watch camel:context-list

#### Update Bundle During Development

    profile-edit --delete --bundle mvn:com.demo/msf/1.0.0 msf
    profile-edit --bundle mvn:com.demo/msf/1.0.0 msf

    watch camel:context-list

#### Create service from properties
    profile-import file:demo.profile.zip
    watch camel:context-list
