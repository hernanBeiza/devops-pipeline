def iniciar(){
    figlet "MAVEN CD";
	String paramStage = params.paramStage;
    def etapasPasadas = null;
    if(!paramStage.isEmpty()){
        etapasPasadas = paramStage.split(":");
    }
    //runEjecutar ya que run choca con el nombre definido antes
    def etapasOriginales = ['build','test','sonar','runEjecutar','rest','nexusCD'];
    def etapasValidadas = utils.validarEtapas(etapasPasadas,etapasOriginales);
    
    etapasValidadas.each{
        stage(it){
            try{
                //"${it.toLowerCase()}"()
                "${it}"()
            }catch(Exception e) {
                echo e.getMessage();
                //env.MensajeErrorSlack = "Stage ${it.toLowerCase()} tiene problemas : ${e}"
                //error env.MensajeErrorSlack
            }
        }
    }
}

def build(){
    stage('build') {
        echo env.STAGE_NAME
        //Usar el gradlewrapper, incluido en el repo
        sh "./mvnw clean package -e"
    }
}

def test(){
    stage('test') {
        echo env.STAGE_NAME
        //Usar el gradlewrapper, incluido en el repo
        sh "./mvnw clean package -e"
    }
}

def sonar(){
    stage('sonar') {
        echo env.STAGE_NAME
        //Nombre en SonarQubeScanner en AdminJenkins/ConfigureTools/SonarQubeScanner
        def scannerHome = tool 'sonar-scanner';
        //Nombre en AdminJenkins/Configuración Global/SonarQube Servers
        withSonarQubeEnv('sonar') { 
            // If you have configured more than one global server connection, you can specify its name
            sh './mvnw org.sonarsource.scanner.maven:sonar-maven-plugin:3.7.0.1746:sonar'
        }
    }
}

def runEjecutar(){
    stage('run') {
        echo env.STAGE_NAME
        sh "nohup bash mvnw spring-boot:run &"
    }
}

def rest(){
    stage('rest') {
        echo env.STAGE_NAME
        //sh './gradle build'
        sh "sleep 30 && curl -X GET 'http://localhost:8082/rest/mscovid/test?msg=testing'"
    }
}

def nexusCD(){
    stage('nexusCD') {
        echo env.STAGE_NAME
        nexusPublisher nexusInstanceId: 'nexus', nexusRepositoryId: 'test-nexus', packages: [[$class: 'MavenPackage', mavenAssetList: [[classifier: '', extension: 'jar', filePath: './build/DevOpsUsach2020-0.0.1.jar']], mavenCoordinate: [artifactId: 'DevOpsUsach2020', groupId: 'com.devopsusach2020', packaging: 'jar', version: '1.0.0']]]
    }
}
return this;