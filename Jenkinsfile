@Library('openshift-library') _

openshift.withCluster() {
  env.APP_NAME = "spring-boot-pipeline-example"
  env.BUILD_ENV = "enterprise-pipeline-build"
  env.DVL_ENV = "enterprise-pipeline-dvl"
  env.INT_ENV = "enterprise-pipeline-int"
  env.PRD_ENV = "enterprise-pipeline-prd"
  env.ARTIFACT_DIRECTORY = "openjdk/spring-boot-rest-example/build/libs"
}

def gradleCmd = './gradlew'

pipeline {

  agent { label 'maven' }

  stages {

    stage('SCM Checkout') {
      steps {
        checkout scm
      }
    }

    stage('Build') {
      steps {
        sh "cd openjdk/spring-boot-rest-example && ${gradleCmd} clean build -xtest"
      }
    }

    stage('Unit Test') {
       steps {
         sh "cd openjdk/spring-boot-rest-example && ${gradleCmd} test"
       }
    }

    stage('SonarQube Code Analysis') {

      steps {
        withSonarQubeEnv("Enterprise Sonarqube") {
          sh "cd openjdk/spring-boot-rest-example && ${gradleCmd} sonarqube -Dsonar.projectVersion=0.1"
        }
      }
     }

     stage('Publish to Nexus') {
      steps {
        withCredentials([[$class: 'UsernamePasswordMultiBinding' , credentialsId: "${BUILD_ENV}-nexus-upload-token",
                        usernameVariable: 'NEXUS_CREDENTIALS_USR', passwordVariable: 'NEXUS_CREDENTIALS_PSW']]){
          sh "cd openjdk/spring-boot-rest-example && ${gradleCmd} -Pnexus_version=${BUILD_NUMBER} publish"
        }
      }
    }

    stage('Image Build') {
      steps {
        binaryBuild(projectName: "${BUILD_ENV}", buildConfigName: "${APP_NAME}", artifactsDirectoryName: "${ARTIFACT_DIRECTORY}")
      }
    }

    stage('Apply DVL Configuration') {
      agent { label 'applier' }

      steps {
        applier(secretName: "pipeline-dvl",
                registryUrl: "https://dvl1con.paasdev.delta.com:443",
                inventoryPath: "openjdk/spring-boot-rest-example/.applier/inventory-dvl/",
                requirementsPath: "openjdk/spring-boot-rest-example/.applier/requirements.yml",
                skipTlsVerify: true )
      }
    }

    stage('Tag Image from Build to DVL') {
        steps {
            tagImage(sourceImageName: "${APP_NAME}",
                     sourceImagePath: "${BUILD_ENV}",
                     sourceImageTag: "latest",
                     toImageName: "${APP_NAME}",
                     toImagePath: "${DVL_ENV}",
                     toImageTag: "latest")
        }
    }


    stage ("Verify Deployment to DVL") {
        steps {
            verifyDeployment(targetApp: "${APP_NAME}", projectName: "${DVL_ENV}")
        }
    }

    stage ("Input - Deploy to INT") {
      steps {
        input "Do you want to deploy to INT?"
      }
    }

    stage('Apply INT Configuration') {
      agent { label 'applier' }

      steps {
        applier(secretName: "pipeline-int",
                registryUrl: "https://int1con.paassi.delta.com:443",
                inventoryPath: "openjdk/spring-boot-rest-example/.applier/inventory-int/",
                requirementsPath: "openjdk/spring-boot-rest-example/.applier/requirements.yml",
                skipTlsVerify: true )
      }
    }

    stage ('Promote to INT') {
      steps {
        imageMirror(sourceSecret: "${BUILD_ENV}-pipeline-dvl",
                    sourceRegistry: "https://docker-registry-default.lab1apps.paaseng.delta.com/",
                    destinationSecret: "${BUILD_ENV}-pipeline-int",
                    destinationRegistry: "https://docker-registry-default.lab1apps.paaseng.delta.com/",
                    insecure: "true",
                    sourceNamespace: "${DVL_ENV}",
                    destinationNamespace: "${INT_ENV}",
                    sourceImage: "${APP_NAME}",
                    sourceImageVersion: "latest",
                    destinationImageVersion: "latest"
                    )
      }
    }

    stage ("Verify Deployment to INT") {
        steps {

          withCredentials([usernamePassword(credentialsId: "${BUILD_ENV}-pipeline-int", usernameVariable: "username", passwordVariable: "tokenInt")]) {
            verifyDeployment(targetApp: "${APP_NAME}",
                 projectName: "${INT_ENV}",
                 clusterUrl: "https://lab1con.paaseng.delta.com",
                 clusterToken: "$tokenInt")
          }
        }
    }

    stage ("Input - Deploy to Production?") {
      steps {
        input "Do you want to deploy to production?"
      }
    }

    stage('Apply PRD1 Configuration') {
      agent { label 'applier' }

      steps {
        applier(secretName: "pipeline-prd1",
                registryUrl: "https://prd1con.paas.delta.com:443",
                inventoryPath: "openjdk/spring-boot-rest-example/.applier/inventory-prd1/",
                requirementsPath: "openjdk/spring-boot-rest-example/.applier/requirements.yml",
                skipTlsVerify: true )
      }
    }

    stage ('Promote to PRD1') {
      steps {
        imageMirror(sourceSecret: "${BUILD_ENV}-pipeline-int",
                    sourceRegistry: "https://docker-registry-default.lab1apps.paaseng.delta.com/",
                    destinationSecret: "${BUILD_ENV}-pipeline-prd1",
                    destinationRegistry: "https://docker-registry-default.lab1apps.paaseng.delta.com/",
                    insecure: "true",
                    sourceNamespace: "${INT_ENV}",
                    destinationNamespace: "${PRD_ENV}",
                    sourceImage: "${APP_NAME}",
                    sourceImageVersion: "latest",
                    destinationImageVersion: "latest"
                    )
      }
    }

    stage ("Verify Deployment to PRD1") {
        steps {

          withCredentials([usernamePassword(credentialsId: "${BUILD_ENV}-pipeline-prd1", usernameVariable: "username", passwordVariable: "tokenInt")]) {
            verifyDeployment(targetApp: "${APP_NAME}",
                 projectName: "${PRD_ENV}",
                 clusterUrl: "https://lab1con.paaseng.delta.com",
                 clusterToken: "$tokenInt")
          }
        }
    }

    stage('Apply PRD3 Configuration') {
      agent { label 'applier' }

      steps {
        applier(secretName: "pipeline-prd3",
                registryUrl: "https://docker-registry-default.lab1apps.paaseng.delta.com/",
                inventoryPath: "openjdk/spring-boot-rest-example/.applier/inventory-prd3/",
                requirementsPath: "openjdk/spring-boot-rest-example/.applier/requirements.yml",
                skipTlsVerify: true )
      }
    }

    stage ('Promote to PRD3') {
      steps {
        imageMirror(sourceSecret: "${BUILD_ENV}-pipeline-int",
                    sourceRegistry: "https://docker-registry-default.lab1apps.paaseng.delta.com/",
                    destinationSecret: "${BUILD_ENV}-pipeline-prd3",
                    destinationRegistry: "https://docker-registry-default.lab1apps.paaseng.delta.com/",
                    insecure: "true",
                    sourceNamespace: "${INT_ENV}",
                    destinationNamespace: "${PRD_ENV}",
                    sourceImage: "${APP_NAME}",
                    sourceImageVersion: "latest",
                    destinationImageVersion: "latest"
                    )
      }
    }

    stage ("Verify Deployment to PRD3") {
        steps {

          withCredentials([usernamePassword(credentialsId: "${BUILD_ENV}-pipeline-prd3", usernameVariable: "username", passwordVariable: "tokenInt")]) {
            verifyDeployment(targetApp: "${APP_NAME}",
                 projectName: "${PRD_ENV}",
                 clusterUrl: "https://lab1con.paaseng.delta.com",
                 clusterToken: "$tokenInt")
          }
        }
    }
  }
}
