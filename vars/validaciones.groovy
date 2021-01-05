def obtenerTipoDeRama(){
	echo "obtenerTipoDeRama";
	sh "env.GIT_BRANCH";
	echo env.GIT_BRANCH;
	return "";
}

return this;