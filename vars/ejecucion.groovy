/*
	forma de invocación de método call:
	def ejecucion = load 'script.groovy'
	ejecucion.call()
*/

def call(){
	pipeline {
		agent any

		parameters {
	    	choice(
		        name: 'paramHerramienta',
		        choices: "maven\ngradle",
		        description: 'Parámetro que determinará si se ejecuta maven.groovy o gradle.groovy'
	        )
	        string(
	        	name: 'paramStage',
	        	defaultValue: '',
	        	description: 'Etapas a ejecutar'
        	)
		}

		stages {
			stage('Pipeline') {
				steps {
			      	script {			      	
			      		//Variables de entorno del sistema
			      		//bat "set"
			      		//sh "env"
					    stage('iniciar') {
					    	echo "iniciar"
					    	env.ALUMNO="Hernán Beiza";
					    	String paramHerramienta = params.paramHerramienta;
					    	env.BUILD_TOOL = paramHerramienta;
					    	String tipoDeRama = validaciones.obtenerTipoDeRama(); 
					    	echo "paramHerramienta ${paramHerramienta}";					    	
					    	echo "tipoDeRama ${tipoDeRama}";
					    	if(paramHerramienta == "gradle" && tipoDeRama == "CD"){
					    		gradleCD.iniciar();
					    	} else if (paramHerramienta == "gradle" && tipoDeRama == "CI"){
					    		gradleCI.iniciar();
					    	} else if (paramHerramienta == "maven" && tipoDeRama == "CD"){
					    		mavenCD.iniciar();
					    	} else if (paramHerramienta == "maven" && tipoDeRama == "CI"){
					    		mavenCI.iniciar();
					    	} else {
					    		echo "Herramienta o tipo de rama no soportadas";
					    	}
					    }
		      		}
				}
	    	}
		}
	    //Manejar si el pipeline fue exitoso o fallido
	    post {
	        success {
	        	echo "Ejecución exitosa [${env.ALUMNO}][${env.JOB_NAME}][${env.BUILD_TOOL}]";
	            slackSend channel: 'D01E5ED8TK2', color: 'good', message: "Ejecución exitosa [${env.ALUMNO}][${env.JOB_NAME}][${env.BUILD_TOOL}]", teamDomain: 'dipdevopsusach2020', tokenCredentialId: 'jenkins-slack'
	        }
	        failure {
		    	echo "Ejecución fallida [${env.ALUMNO}][${env.JOB_NAME}][${env.BUILD_TOOL}] en stage [${env.STAGE_NAME}]";
	            slackSend channel: 'D01E5ED8TK2', color: 'danger', message: "Ejecución fallida [${env.ALUMNO}][${env.JOB_NAME}][${env.BUILD_TOOL}] en stage [${env.STAGE_NAME}]", teamDomain: 'dipdevopsusach2020', tokenCredentialId: 'jenkins-slack'
	        }
	    }
	}
}

return this;