def iniciar(){
    figlet "GRADLE CD";
	String paramStage = params.paramStage;
    echo "paramStage ${paramStage}";
    def etapasPasadas = null;
    if(!paramStage.isEmpty()){
	    etapasPasadas = paramStage.split(":");
	}
	//runEjecutar ya que run choca con el nombre definido antes
	def etapasOriginales = ['downloadNexus','runDownloadedJar','rest','nexusCD'];
	def etapasValidadas = utils.validarEtapas(etapasPasadas,etapasOriginales);
	
    etapasValidadas.each{
        stage(it){
            try{
                //"${it.toLowerCase()}"()
                "${it}"()
            }catch(Exception e) {
            	echo e;
                //env.MensajeErrorSlack = "Stage ${it.toLowerCase()} tiene problemas : ${e}"
                //error env.MensajeErrorSlack
            }
        }
    }
}

def downloadNexus(){
	stage('downloadNexus') {
		echo env.STAGE_NAME;
		sh "curl -X GET -u admin:admin http://localhost:8081/repository/test-nexus/com/devopsusach2020/DevOpsUsach2020/${env.VERSION}-DEVELOP/DevOpsUsach2020-${env.VERSION}-DEVELOP.jar -O";
		//sh "ls -l"
	}
}

def runDownloadedJar(){
	stage('runDownloadedJar') {			
		echo env.STAGE_NAME;
		sh 'nohup bash ./gradlew bootRun &'
	}
}

def rest(){
	stage('rest') {
		echo env.STAGE_NAME;
		//sh './gradle build'
		sh "sleep 30 && curl -X GET 'http://localhost:8082/rest/mscovid/test?msg=testing'"
	}
}

def nexusCD(){
	stage('nexusCD') {
		echo env.STAGE_NAME;
		nexusPublisher nexusInstanceId: 'nexus', nexusRepositoryId: 'test-nexus', packages: [[$class: 'MavenPackage', mavenAssetList: [[classifier: '', extension: 'jar', filePath: "./DevOpsUsach2020-${env.VERSION}-DEVELOP.jar"]], mavenCoordinate: [artifactId: 'DevOpsUsach2020', groupId: 'com.devopsusach2020', packaging: 'jar', version: "${env.VERSION}-RELEASE"]]]
	}
}

return this;