def obtenerTipoDeRama(){
	//echo "obtenerTipoDeRama";
	//sh "env";
	def rama = env.GIT_BRANCH;
	echo "obtenerTipoDeRama ${rama}";
	if(rama.toLowerCase().contains("release")){
		return "CD";
	} else if (rama.toLowerCase().contains("feature") || rama.contains("develop")){
		return "CI";
	} else {
		return "Rama no soportada";
	}
}

return this;