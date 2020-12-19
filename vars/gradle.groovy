/*
	forma de invocación de método call:
	def ejecucion = load 'script.groovy'
	ejecucion.call()
*/

def call(){
	echo "call(); gradle.groovy";

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

def etapas(pasadas=['build','test','sonar','run','rest','nexus']){
	if(pasadas.contains("build") || pasadas.contains("test")){
		stage('build & test') {
			echo env.STAGE_NAME
			//Usar el gradlewrapper, incluido en el repo
			sh './gradlew clean build'
		}
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
	}
	if(pasadas.contains("run")){
		stage('run') {
			echo env.STAGE_NAME

			sh 'nohup bash ./gradlew bootRun &'
		}
	}
	if(pasadas.contains("rest")){
		stage('rest') {
			echo env.STAGE_NAME

			//sh './gradle build'
			sh "sleep 30 && curl -X GET 'http://localhost:8082/rest/mscovid/test?msg=testing'"
		}
	}
	if(pasadas.contains("nexus")){
		stage('nexus') {
			echo env.STAGE_NAME

			nexusPublisher nexusInstanceId: 'nexus', nexusRepositoryId: 'test-nexus', packages: [[$class: 'MavenPackage', mavenAssetList: [[classifier: '', extension: 'jar', filePath: './build/libs/DevOpsUsach2020-0.0.1.jar']], mavenCoordinate: [artifactId: 'DevOpsUsach2020', groupId: 'com.devopsusach2020', packaging: 'jar', version: '1.0.0']]]
		}
	}

}

return this;
