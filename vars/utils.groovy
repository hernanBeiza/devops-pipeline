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

def obtenerVersionGradle(){
	if (fileExists('build.gradle')) {
		echo build.gradle;
		return "";
	} else {
		return null;
	}
}

def obtenerVersionMaven(){
	if (fileExists('pom.gradle')) {
		return "";
	} else {
		return null;
	}
}

def obtenerVersion(){
	echo "obtenerVersion";
	def herramienta = env.BUILD_TOOL;
	if(herramienta=="maven"){
		def version = obtenerVersionMaven();
	} else if (herramienta=="gradle"){
		def version = obtenerVersionGradle();
	}
	env.VERSION = version;
	return vesion;
}

return this;