pipeline {
    agent {
        docker {
            image 'maven:3.5.4-jdk-8-alpine' 
            args '-v /root/.ssh:/root/.ssh -v $HOME/.m2:/root/.m2' 
        }
    }
    
    stages {
		stage('Build Package') {
			steps {
				
				withMaven() {
					//properties([pipelineTriggers([snapshotDependencies()])])
					sh '$MVN_CMD -DskipTests=true -T 1C -B clean deploy'
					//sh 'mvn org.pitest:pitest-maven:mutationCoverage -DtimeoutConstant=8000'
            	}
            }
		}
		stage('Archive') {
	   	    steps {
	   	        archiveArtifacts artifacts: '**/target/**/*.jar', fingerprint: true
	   	    	archiveArtifacts artifacts: '**/pom.xml', fingerprint: true
	   	    }	   			    
	   	}	  
    }
}
