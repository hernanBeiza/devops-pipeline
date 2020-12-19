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
					    stage('iniciar') {
					    	echo "iniciar"
					    	String paramHerramienta = params.paramHerramienta;
					    	echo "paramHerramienta ${paramHerramienta}";					    	
					    	env.ALUMNO="Hernán Beiza";
					    	if(paramHerramienta=="maven"){
					    		env.BUILD_TOOL="MAVEN";
								//Esto no funciona usando un pipeline como librería, 
								//porque los archivos no están dentro el workspace
								//def ejecucionMaven = load 'maven.groovy'
								//ejecucionMaven.call()
								//$("{paramHerramienta").call();
								//Recordar que ahora los archivos están en vars
								maven.call();
				    		} else {
					    		env.BUILD_TOOL="GRADLE";
								//Esto no funciona usando un pipeline como librería, 
								//porque los archivos no están dentro el workspace
								//def ejecucionGradle = load 'gradle.groovy'
								//ejecucionGradle.call()
								//Recordar que ahora los archivos están en vars
								gradle.call();
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