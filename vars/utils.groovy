def obtenerTipoDeRama(){
	def rama = env.GIT_BRANCH;
	echo "obtenerTipoDeRama ${rama}";
	if(rama.toUpperCase().contains("RELEASE")){
		return "CD";
	} else if (rama.toUpperCase().contains("FEATURE") || rama.toUpperCase().contains("DEVELOP")){
		return "CI";
	} else {
		return "Rama no soportada";
	}
}

def obtenerNombreDeRama(){
	def rama = env.GIT_BRANCH;
	echo "obtenerTipoDeRama ${rama}";	
	if(rama.toUpperCase().contains("RELEASE")){
		return "RELEASE";
	} else if (rama.toUpperCase().contains("DEVELOP")){
		return "DEVELOP";
	} else if (rama.toUpperCase().contains("FEATURE")){
		return "FEATURE";
	} else {
		return "Nombre de rama no soportado";
	}
}

def verificarArchivoHerramienta(){
	def herramienta = env.BUILD_TOOL;
	echo "verificarArchivoHerramienta ${herramienta}";	
	sh "ls -l"
	if(herramienta=="maven"){
		if (fileExists('pom.xml')) {
			echo "Archivo pom.xml existe";
			return true;
		} else {
			echo "Archivo pom.xml no existe";
			return false;
		}
	} else if(herramienta=="gradle"){
		if (fileExists('build.gradle')) {
			echo "Archivo build.gradle existe";
			return true;
		} else {
			echo "Archivo build.gradle no existe";
			return false;
		}
	} else {
		echo "Tipo de herramienta no soportada";
		return false;
	}
}

//TODO Pasar todo el seteo de variables de entorno a una única sección
def iniciarVariablesEntorno(){
	echo "iniciarVariablesEntorno";
	def herramienta = env.BUILD_TOOL;
	if(herramienta=="maven"){
		iniciarMaven();
	} else if (herramienta=="gradle"){
		iniciarGradle();
	}
	sh "env";
}

def iniciarGradle(){
	if (fileExists('build.gradle')) {
		//def buildGradle = readFile("build.gradle");
		def versionValue = sh(returnStdout: true, script: "cat build.gradle | grep -o 'version = [^,]*'").trim()
		def versionString = versionValue.split(/=/)[1]
		def versionNumber = versionString.replace("'", "").trim();
		env.VERSION = versionNumber;
	} else {
		echo "Archivo build.gradle no existe";
	}
}

def iniciarMaven(){
	if (fileExists('pom.xml')) {
		def pomFile = readFile("pom.xml");
		def pom = new XmlParser().parseText(pomFile);
		env.VERSION = pom["version"].text().trim();
		env.PROJECTGROUPID = pom["groupdId"].text().trim();
		env.ARTIFACTID = pom["artifactId"].text().trim();
	} else {
		echo "Archivo pom.xml no existe";
	}
}


def validarEtapas(etapasPasadas, etapasOriginales){
    println etapasPasadas;
    println etapasOriginales;

    def etapasError = [];
    def etapasValidas = [];
    
    if(etapasPasadas.size()>0){
        etapasPasadas.each{
            println "Etapa ${it}";
            if(etapasOriginales.contains(it)){
                println("${it} encontrada");
                etapasValidas.add(it)
            } else {
                println("${it} no encontrada");
                etapasError.add(it)
            }
        }
        //Existen Etapas con Error
        if(etapasError.size() > 0){
            //env.MensajeErrorSlack = " Estos stages ingresados no existen : ${etapasError}.\nStages disponibles para ejecutar:\n${etapasOriginales}"
            //error env.MensajeErrorSlack
            println "Estos stages ingresados no existen : ${etapasError}.\nStages disponibles para ejecutar:\n${etapasOriginales}";
        }
        println "Validación de stages correcta.\nSe ejecutarán los siguientes stages en orden :\n${etapasValidas}"
    } else {
        etapasValidas = etapasOriginales;
        println "Parámetro de stages vacío.\nSe ejecutarán todas los stages en el siguiente orden :\n${etapasValidas}"
    }

    return etapasValidas
}

return this;