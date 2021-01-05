def obtenerTipoDeRama(){
	echo "obtenerTipoDeRama";
	sh "env.GIT_BRANCH";
	def rama = env.GIT_BRANCH;
	echo "rama ${rama}";
	return "";
}

return this;