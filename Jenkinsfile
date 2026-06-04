pipeline {
    agent any

    environment {
        DOCKER_IMAGE = 'vasanth97/test'
        CONTAINER_NAME = 'springboot-demo'
        REMOTE_HOST = '192.168.0.106'
        APP_PORT = '8080'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build Spring Boot App') {
            steps {
                bat 'mvn clean package'
            }
        }

        stage('Build Docker Image') {
            steps {
                bat 'docker build -t %DOCKER_IMAGE%:%BUILD_NUMBER% -t %DOCKER_IMAGE%:latest .'
            }
        }

        stage('Login To Docker Hub') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'dockerhub-creds',
                    usernameVariable: 'DOCKER_USERNAME',
                    passwordVariable: 'DOCKER_PASSWORD'
                )]) {
                    bat 'echo %DOCKER_PASSWORD% | docker login -u %DOCKER_USERNAME% --password-stdin'
                }
            }
        }

        stage('Push Docker Image') {
            steps {
                bat 'docker push %DOCKER_IMAGE%:%BUILD_NUMBER%'
                bat 'docker push %DOCKER_IMAGE%:latest'
            }
        }

        stage('Deploy To Ubuntu VM') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'ubuntu-password',
                    usernameVariable: 'UBUNTU_USER',
                    passwordVariable: 'UBUNTU_PASSWORD'
                )]) {
                    script {
                        def remote = [:]
                        remote.name = 'ubuntu-vm'
                        remote.host = env.REMOTE_HOST
                        remote.user = UBUNTU_USER
                        remote.password = UBUNTU_PASSWORD
                        remote.allowAnyHosts = true

                        sshCommand remote: remote, command: """
                            docker pull ${env.DOCKER_IMAGE}:latest
                            docker stop ${env.CONTAINER_NAME} || true
                            docker rm ${env.CONTAINER_NAME} || true
                            docker run -d --name ${env.CONTAINER_NAME} -p ${env.APP_PORT}:8080 --restart unless-stopped ${env.DOCKER_IMAGE}:latest
                        """
                    }
                }
            }
        }
    }

    post {
        always {
            bat 'docker logout || exit /b 0'
        }

        success {
            echo 'Deployment completed successfully.'
        }

        failure {
            echo 'Pipeline failed. Check Jenkins console output.'
        }
    }
}