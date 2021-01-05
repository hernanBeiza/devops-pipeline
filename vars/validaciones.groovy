def obtenerTipoDeRama(){
	echo "obtenerTipoDeRama";
	sh "env";
	def rama = ${env.GIT_BRANCH};
	echo "rama ${rama}";
	return "";
}

return this;