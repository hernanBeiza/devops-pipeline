def obtenerTipoDeRama(){
	//echo "obtenerTipoDeRama";
	//sh "env";
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

return this;