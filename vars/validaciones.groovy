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

return this;