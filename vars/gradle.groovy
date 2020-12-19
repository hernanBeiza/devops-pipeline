/*
	forma de invocación de método call:
	def ejecucion = load 'script.groovy'
	ejecucion.call()
*/

def call(){
	echo "call(); gradle.groovy";

	stage('build & test') {
		//Usar el gradlewrapper, incluido en el repo
		sh './gradlew clean build'
	}
	stage('sonar') {
		//Nombre en SonarQubeScanner en AdminJenkins/ConfigureTools/SonarQubeScanner
		def scannerHome = tool 'sonar-scanner';
		//Nombre en AdminJenkins/Configuración Global/SonarQube Servers
	    withSonarQubeEnv('sonar') { 
	    	// If you have configured more than one global server connection, you can specify its name
			sh "${scannerHome}/bin/sonar-scanner -Dsonar.projectKey=ejemplo-gradle -Dsonar.java.binaries=build"
		}
	}
	stage('run') {
		sh 'nohup bash ./gradlew bootRun &'
	}
	stage('rest') {
		//sh './gradle build'
		sh "sleep 30 && curl -X GET 'http://localhost:8082/rest/mscovid/test?msg=testing'"
	}
	stage('nexus') {
		nexusPublisher nexusInstanceId: 'nexus', nexusRepositoryId: 'test-nexus', packages: [[$class: 'MavenPackage', mavenAssetList: [[classifier: '', extension: 'jar', filePath: './build/libs/DevOpsUsach2020-0.0.1.jar']], mavenCoordinate: [artifactId: 'DevOpsUsach2020', groupId: 'com.devopsusach2020', packaging: 'jar', version: '1.0.0']]]
	}
}

return this;
