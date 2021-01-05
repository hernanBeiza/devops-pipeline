def iniciar(){
    figlet "GRADLE CD";
	String paramStage = params.paramStage;
    echo "paramStage ${paramStage}";
	if (paramStage=="") {
		echo "Ejecutar todo";
		etapas();		
	} else {
		echo "Ejecutar solo las configuradas";
		def pasadas = paramStage.split(":");
		etapas(pasadas);
	}
}

def etapas(pasadas=['downloadNexus','runDownloadedJar','rest','nexusCD']){
    Boolean noEncontrada = false;

	if(pasadas.contains("downloadNexus")){
		stage('downloadNexus') {
			echo env.STAGE_NAME;
			sh "curl -X GET -u admin:admin http://localhost:8081/repository/test-nexus/com/devopsusach2020/DevOpsUsach2020/${env.VERSION}-${env.BRANCH_NAME}/DevOpsUsach2020-${env.VERSION}-${env.BRANCH_NAME}.jar -O";
			sh "ls -l"
		}
    } else {
        noEncontrada = true;
    }
	if(pasadas.contains("runDownloadedJar")){
		stage('runDownloadedJar') {			
			echo env.STAGE_NAME;
			sh 'nohup bash ./gradlew bootRun &'
		}
    } else {
        noEncontrada = true;
    }
	if(pasadas.contains("rest")){
		stage('rest') {
			echo env.STAGE_NAME;
			//sh './gradle build'
			sh "sleep 30 && curl -X GET 'http://localhost:8082/rest/mscovid/test?msg=testing'"
		}
    } else {
        noEncontrada = true;
    }
	if(pasadas.contains("nexusCD")){
		stage('nexusCD') {
			echo env.STAGE_NAME;
			nexusPublisher nexusInstanceId: 'nexus', nexusRepositoryId: 'test-nexus', packages: [[$class: 'MavenPackage', mavenAssetList: [[classifier: '', extension: 'jar', filePath: "./DevOpsUsach2020-${env.VERSION}-${env.BRANCH_NAME}.jar"]], mavenCoordinate: [artifactId: 'DevOpsUsach2020', groupId: 'com.devopsusach2020', packaging: 'jar', version: "${env.VERSION}"]]]
		}
	 } else {
        noEncontrada = true;
    }
    if(noEncontrada){
        echo "Tarea(s) ${pasadas} no encontrada(s)";
    }
}

return this;