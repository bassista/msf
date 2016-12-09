## Fuse Managed Service Factory for Camel Routes

#### Run with Fuse Fabric

    wait-for-service -t 300000 io.fabric8.api.BootstrapComplete
    fabric:create --wait-for-provisioning --profile fabric --profile feature-camel

    profile-create --parent feature-camel msf
    profile-edit --bundle mvn:com.demo/msf/1.0.0 msf

    profile-create --parent msf s1
    profile-edit --pid org.apache.camel.msf-s1/camelContextId=serviceOne s1
    profile-edit --pid org.apache.camel.msf-s1/from=timer://foo?fixedRate=true&period=1000 s1
    profile-edit --pid org.apache.camel.msf-s1/to=log:ticketServer?showBody=true s1
    container-add-profile root s1

    profile-create --parent msf s2
    profile-edit --pid org.apache.camel.msf-s2/camelContextId=serviceTwo s2
    profile-edit --pid org.apache.camel.msf-s2/from=timer://foo?fixedRate=true&period=2000 s2
    profile-edit --pid org.apache.camel.msf-s2/to=log:ticketServer?showBody=true s2
    container-add-profile root s2

    watch camel:context-list

#### Run with Standalone Fuse

    osgi:install -s mvn:com.demo/msf/1.0.0

    propset -p org.apache.camel.msf-s1 camelContextId 1234
    propset -p org.apache.camel.msf-s1 from timer://foo?fixedRate=true&period=1000
    propset -p org.apache.camel.msf-s1 to log:ticketServer?showAll=true

    watch camel:context-list

#### Demo for removing service s1

    camel:context-list

    container-remove-profile root s1

    camel:context-list

    container-add-profile root s1

    camel:context-list

#### Demo for removing service factory

    camel:context-list

    container-remove-profile root msf

    camel:context-list

    container-add-profile root msf

    camel:context-list

#### Update Bundle During Development

    profile-edit --delete --bundle mvn:com.demo/msf/1.0.0 msf
    profile-edit --bundle mvn:com.demo/msf/1.0.0 msf

    watch camel:context-list

#### Create service from properties
    profile-import file:demo.profile.zip
    watch camel:context-list
