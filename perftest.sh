export CLIENT_JAR=~/projects/kombit/code/sdm/subprojects/client/build/libs/client-3.0.0-standalone.jar
#export TRUSTSTORE=classpath:/prod.kombit.netic.dk.truststore.jks
export TRUSTSTORE=classpath:/stage.kombit.netic.dk.truststore.jks
export TRUSTSTORE_PASSWORD=Test1234
export KEYSTORE=~/projects/kombit/code/sdm/subprojects/client/keystores/kombit-foces-valid.jks
export KEYSTORE_PASSWORD=Test1234
export REPLICATION_URL=https://stage.kombit.netic.dk/replication

fetchView() {
	echo "$VIEW"
	java -Dstamdata.client.truststore=$TRUSTSTORE -Dstamdata.client.truststore.password=$TRUSTSTORE_PASSWORD -Dstamdata.client.keystore=$KEYSTORE -Dstamdata.client.keystore.password=$KEYSTORE_PASSWORD -Dstamdata.client.security=ssl -Dstamdata.client.url=$REPLICATION_URL -Dstamdata.client.pagesize=5000 -Dstamdata.client.view=$VIEW -Dstamdata.client.starttag= -Dstamdata.client.outputfile=output.xml -jar $CLIENT_JAR
	wc -c output.xml
}

VIEW=Person fetchView
VIEW=BarnRelation fetchView
VIEW=Civilstand fetchView
VIEW=Foedselsregistreringsoplysninger fetchView
VIEW=Folkekirkeoplysninger fetchView
VIEW=ForaeldremyndighedsRelation fetchView
VIEW=Haendelse fetchView
VIEW=KommunaleForhold fetchView
VIEW=MorOgFaroplysninger fetchView
VIEW=Statsborgerskab fetchView
VIEW=Udrejseoplysninger fetchView
VIEW=UmyndiggoerelseVaergeRelation fetchView
VIEW=Valgoplysninger fetchView
VIEW=Beskyttelse fetchView

rm output.xml
