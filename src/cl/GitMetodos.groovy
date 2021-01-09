package cl;

def call(){
	
}

def checkIfBranchExists(String rama){
	echo "checkIfBranchExists ${rama}";
	//Retornará el commit solo sí existe. Si no existe, no retornará commit, retornará nada
	def salida = sh (script:"git fetch -p; git ls-remote  --heads origin ${rama}", returnStdout:true);
	def respuesta = (!salida?.trim()) ? true:false;
	return respuesta;
}

def isBranchUpdated(String ramaOrigen, String ramaDestino){
	echo "isBranchUpdated ${ramaOrigen} ${ramaDestino} ";
	//Para actualizar el ambiente de trabajo con las ramas correspondiente
	sh "git checkout ${ramaOrigen} && git pull;";
	sh "git checkout ${ramaDestino} && git pull;";
	//Si no arroja commits, está actualizada
	//Si arroja commits, está desactualizada
	//Hacer git log directamente con remoto falla
	def salida = sh (script:"git log ${ramaDestino}..${ramaOrigen}", returnStdout:true);
	def respuesta = (!salida?.trim()) ? true:false;
	return respuesta;
	/*
	if(salida?.isEmpty()){
		return false;
	} else {
		return true;
	}
	*/
}

def deleteBranch(String rama){
	echo "deleteBranch ${rama}";
	sh "git push origin --delete ${rama}";
}

def createBranch(String ramaDestino, String ramaOrigen){
	echo "createBranch ${ramaDestino} ${ramaOrigen}";

	sh "git reset --hard head";
	sh "git fetch -p";
	sh "git pull";
	sh "git checkout ${ramaOrigen}";
	sh "git checkout -b ${ramaDestino}";
	sh "git push origin ${ramaDestino}";
	/*
	sh '''
		git reset --hard head
		git pull
		git checkout '''+ramaOrigen+'''
		git checkout -b '''+ramaDestino+'''
		git push origin '''+ramaDestino+'''
	'''
	*/
}

return this;