pipeline {
  agent any
  stages {
    stage('error') {
      steps {
        sh 'mvn clean install -U'
        sh 'mvn test'
      }
    }

  }
}