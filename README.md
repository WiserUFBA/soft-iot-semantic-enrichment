# SOFT-IoT-Semantic-Enrichment

## Introduction

Module of SOFT-IoT plataform to enrich sensor data, in FoT-Gateway, with Semantic Web descriptions.

## Installation



soft-iot-semantic-enrichment needs of Jena 3.1.0 library to run correctly. This library has some dependencies that should be installed:  does not have a stable version of bundle jena-osgi. Thus, we need install it manually through the compilated version in directory *soft-iot-semantic-enrichmen/jena-gateway.kar/*. So, we need copy:
```
bundle:install mvn:com.github.andrewoma.dexx/collection/0.6
bundle:install mvn:com.github.jsonld-java/jsonld-java/0.8.2
bundle:install mvn:com.fasterxml.jackson.core/jackson-core/2.7.3
bundle:install mvn:com.fasterxml.jackson.core/jackson-databind/2.7.3
bundle:install mvn:com.fasterxml.jackson.core/jackson-annotations/2.7.3
bundle:install mvn:org.apache.httpcomponents/httpcore-osgi/4.4.4
bundle:install mvn:org.apache.httpcomponents/httpclient-osgi/4.5.2
bundle:install mvn:org.apache.commons/commons-csv/1.2
bundle:install mvn:org.apache.commons/commons-lang3/3.4
bundle:install mvn:org.apache.thrift/libthrift/0.9.3
bundle:install mvn:org.apache.servicemix.bundles/org.apache.servicemix.bundles.xerces/2.11.0_1
bundle:install mvn:org.apache.servicemix.bundles/org.apache.servicemix.bundles.xmlresolver/1.2_5

```
After the installation of all Jena 3.1.0 you should install the proper Jena:
```
bundle:install mvn:org.apache.jena/jena-osgi/3.1.0
```
soft-iot-semantic-enrichment also depends of modules [soft-iot-mapping-devices](https://github.com/WiserUFBA/soft-iot-mapping-devices) and [soft-iot-local-storage](https://github.com/WiserUFBA/soft-iot-local-storage). They need to be installed and started before SOFT-IoT-Semantic-Enrichment.

Then, in order to install soft-iot-semantic-enrichment using our custom maven support execute the following commands in Karaf Shell:

```sh
config:edit org.ops4j.pax.url.mvn 
config:property-append org.ops4j.pax.url.mvn.repositories ", https://github.com/WiserUFBA/wiser-mvn-repo/raw/master/releases@id=wiser"
config:update
bundle:install mvn:br.ufba.dcc.wiser.soft_iot/soft-iot-mapping-devices/1.0.0
bundle:install mvn:br.ufba.dcc.wiser.soft_iot/soft-iot-local-storage/1.0.0
bundle:install mvn:br.ufba.dcc.wiser.soft_iot/soft-iot-semantic-enrichment/1.0.0
```

Finally, for correct execution of module you need to copy and to set configuration of file:
```
soft-iot-semantic-enrichment/src/main/resources/br.ufba.dcc.wiser.soft_iot.semantic_enrichment.cfg
```
to:
```
<servicemix_directory>/etc
```

## Deploy to Maven Repo

To deploy this repo into our custom maven repo, change pom according to the new version and after that execute the following command. Please ensure that both wiser-mvn-repo and this repo are on the same folder.

```sh
mvn -DaltDeploymentRepository=release-repo::default::file:../wiser-mvn-repo/releases/ deploy
```



## Support and development

<p align="center">
	Developed by Leandro Andrade at </br>
  <img src="https://wiki.dcc.ufba.br/pub/SmartUFBA/ProjectLogo/wiserufbalogo.jpg"/>
</p>
