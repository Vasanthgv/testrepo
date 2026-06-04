pipeline {

    agent any

    environment {
        IMAGE_NAME = "yourdockerhub/springboot-demo"
    }

    stages {

        stage('Checkout') {
            steps {
                git branch: 'main',
                url: 'https://github.com/yourrepo.git'
            }
        }

        stage('Build') {
            steps {
                bat 'mvn clean package'
            }
        }

        stage('Docker Build') {
            steps {
                bat 'docker build -t %IMAGE_NAME%:latest .'
            }
        }

        stage('Docker Push') {
            steps {
                withCredentials([
                    usernamePassword(
                        credentialsId: 'dockerhub',
                        usernameVariable: 'USER',
                        passwordVariable: 'PASS'
                    )
                ]) {

                    bat '''
                    docker login -u %USER% -p %PASS%
                    docker push %IMAGE_NAME%:latest
                    '''
                }
            }
        }

        stage('Deploy') {
            steps {

                bat '''
                ssh vasanth@192.168.1.120 ^
                "docker pull yourdockerhub/springboot-demo:latest && ^
                docker stop springboot-demo || true && ^
                docker rm springboot-demo || true && ^
                docker run -d --name springboot-demo -p 8080:8080 yourdockerhub/springboot-demo:latest"
                '''
            }
        }
    }
}