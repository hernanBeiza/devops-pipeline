def iniciar(){
    figlet "GRADLE CI";
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

def etapas(pasadas=['build','test','sonar','run','rest','nexusCI']){
    Boolean noEncontrada = false;

	if(pasadas.contains("build") || pasadas.contains("test")){
		stage('build & test') {
			echo env.STAGE_NAME
			//Usar el gradlewrapper, incluido en el repo
			sh './gradlew clean build'
		}
    } else {
        noEncontrada = true;
    }
	if(pasadas.contains("sonar")){
		stage('sonar') {
			echo env.STAGE_NAME
			//Nombre en SonarQubeScanner en AdminJenkins/ConfigureTools/SonarQubeScanner
			def scannerHome = tool 'sonar-scanner';
			//Nombre en AdminJenkins/Configuración Global/SonarQube Servers
		    withSonarQubeEnv('sonar') { 
		    	// If you have configured more than one global server connection, you can specify its name
				sh "${scannerHome}/bin/sonar-scanner -Dsonar.projectKey=ejemplo-gradle -Dsonar.java.binaries=build"
			}
		}
    } else {
        noEncontrada = true;
    }
	if(pasadas.contains("run")){
		stage('run') {
			echo env.STAGE_NAME
			sh 'nohup bash ./gradlew bootRun &'
		}
    } else {
        noEncontrada = true;
    }
	if(pasadas.contains("rest")){
		stage('rest') {
			echo env.STAGE_NAME
			//sh './gradle build'
			sh "sleep 30 && curl -X GET 'http://localhost:8082/rest/mscovid/test?msg=testing'"
		}
    } else {
        noEncontrada = true;
    }
	if(pasadas.contains("nexusCI")){
		stage('nexusCI') {
			echo env.STAGE_NAME
			//env.GIT_BRANCH;
			nexusPublisher nexusInstanceId: 'nexus', nexusRepositoryId: 'test-nexus', packages: [[$class: 'MavenPackage', mavenAssetList: [[classifier: '', extension: 'jar', filePath: "./build/libs/DevOpsUsach2020-${env.VERSION}.jar"]], mavenCoordinate: [artifactId: 'DevOpsUsach2020', groupId: 'com.devopsusach2020', packaging: 'jar', version: "${env.VERSION}"]]]
		}
	 } else {
        noEncontrada = true;
    }
    if(noEncontrada){
        echo "Tarea(s) ${pasadas} no encontrada(s)";
    }
}

return this;