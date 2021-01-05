def obtenerTipoDeRama(){
	//echo "obtenerTipoDeRama";
	//sh "env";
	def rama = env.GIT_BRANCH;
	echo "obtenerTipoDeRama ${rama}";
	if(rama.contains("develop")){
		return "CD";
	} else if (rama.contains("feature")){
		return "CI";
	} else {
		return "Rama no soportada";
	}
}

return this;