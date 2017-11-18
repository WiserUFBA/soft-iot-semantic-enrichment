# FoT-Gateway-Semantic-Enrichment

Module of SOFT-IoT plataform to enrich sensor data, in FoT-Gateway, with Semantic Web descriptions. 

FoT-Gateway-Semantic-Enrichment needs of Jena 3.1.0 library to run correctly. This library does not have a stable version of bundle jena-osgi. Thus, we need install it manually through the compilated version in directory *fot-gateway-semantic-enrichmen/jena-gateway.kar/*. So, we need copy:
```
fot-gateway-semantic-enrichmen/jena-gateway.kar/target/jena-gateway.kar-1.0-SNAPSHOT.kar
```
to:
```
<servicemix_directory>/deploy
```

Finally, for correct execution of module you need to copy and to set configuration of file:
```
fot-gateway-semantic-enrichment/src/main/resources/br.ufba.dcc.wiser.soft_iot.semantic_enrichment.cfg
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

## Installation

To install this bundle using our custom maven support execute the following commands in Karaf Shell:

```sh
config:edit org.ops4j.pax.url.mvn 
config:property-append org.ops4j.pax.url.mvn.repositories ", https://github.com/WiserUFBA/wiser-mvn-repo/raw/master/releases@id=wiser"
config:update
mvn:br.ufba.dcc.wiser.soft_iot/fot-gateway-semantic-enrichment/1.0.0
```

## Support and development

<p align="center">
	Developed by Leandro Andrade at </br>
  <img src="https://wiki.dcc.ufba.br/pub/SmartUFBA/ProjectLogo/wiserufbalogo.jpg"/>
</p>
