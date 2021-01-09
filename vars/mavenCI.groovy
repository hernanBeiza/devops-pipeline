import cl.*;

def iniciar(){
    figlet "MAVEN CI";
	String paramStage = params.paramStage;
    def etapasPasadas = null;
    if(!paramStage.isEmpty()){
        etapasPasadas = paramStage.split(":");
    }
    //runEjecutar ya que run choca con el nombre definido antes
    def etapasOriginales = ['build','test','sonar','runEjecutar','rest','nexusCI','createBranchs'];
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

def build(){
    stage('build & test') {
        echo env.STAGE_NAME
        //Usar el gradlewrapper, incluido en el repo
        sh "./mvnw clean package -e"
    }
}

def test(){
    stage('build & test') {
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

def nexusCI(){
    stage('nexusCI') {
        echo env.STAGE_NAME
        nexusPublisher nexusInstanceId: 'nexus', nexusRepositoryId: 'test-nexus', packages: [[$class: 'MavenPackage', mavenAssetList: [[classifier: '', extension: 'jar', filePath: './build/DevOpsUsach2020-0.0.1.jar']], mavenCoordinate: [artifactId: 'DevOpsUsach2020', groupId: 'com.devopsusach2020', packaging: 'jar', version: '1.0.0']]]
    }
}

def createBranchs(){
    def git = new GitMetodos();
    //Ver como obtener parámetro
    //El versionado es dinámico, verificar versión de pom.xml o build.gradle
    //El nombre de la rama debe ser dinámico
    //env.GIT_BRANCH debería ser feature-* o develop
    if (git.checkIfBranchExists('release-v1-0-0')){
        if(git.isBranchUpdated(env.GIT_BRANCH,'release-v1-0-0')){
            echo "Rama creada y actualizada contra " + env.GIT_BRANCH;
        } else {
            git.deleteBranch('release-v1-0-0');
            git.createBranch('release-v1-0-0',env.GIT_BRANCH);
        }
    } else {
        git.createBranch('release-v1-0-0',env.GIT_BRANCH);
    }

}

return this;