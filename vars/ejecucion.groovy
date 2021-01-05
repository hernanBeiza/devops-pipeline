import cl.*;

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
						    def mensajes = new Mensajes();
						    mensajes.mostrar("Iniciando...");
					    	env.ALUMNO="Hernán Beiza";
					    	String paramHerramienta = params.paramHerramienta;
					    	env.BUILD_TOOL = paramHerramienta;
					    	String tipoDeRama = utils.obtenerTipoDeRama();
					    	env.BRANCH_NAME= utils.obtenerNombreDeRama();
					    	String version = utils.obtenerVersion();
					    	env.VERSION = version;
					    	sh "ls -l";
					    	echo "paramHerramienta ${paramHerramienta}";
					    	echo "tipoDeRama ${tipoDeRama}";
					    	echo "version ${version}";
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
	    //Manejar si el pipeline fue exitoso o fallido y enviar un mensaje en Slack
	    post {
	        success {
	        	echo "Ejecución exitosa [${env.ALUMNO}][${env.JOB_NAME}][${env.BUILD_TOOL}]";
	            slackSend channel: 'D01E5ED8TK2', color: 'good', message: "Ejecución exitosa [${env.ALUMNO}][${env.JOB_NAME}][${env.BUILD_TOOL}][${env.BRANCH_TYPE}]", teamDomain: 'dipdevopsusach2020', tokenCredentialId: 'jenkins-slack'
	        }
	        failure {
		    	echo "Ejecución fallida [${env.ALUMNO}][${env.JOB_NAME}][${env.BUILD_TOOL}] en stage [${env.STAGE_NAME}]";
	            slackSend channel: 'D01E5ED8TK2', color: 'danger', message: "Ejecución fallida [${env.ALUMNO}][${env.JOB_NAME}][${env.BUILD_TOOL}] en stage [${env.STAGE_NAME}]", teamDomain: 'dipdevopsusach2020', tokenCredentialId: 'jenkins-slack'
	        }
	    }
	}
}

return this;