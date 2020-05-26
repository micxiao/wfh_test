pipeline {
    agent { label 'master' }
    stages {
        stage('build') {
            steps {
				dir('wfh_test') {
					sh 'mvn --version'
				}
            }
        }
    }
}