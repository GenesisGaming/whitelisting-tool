node {

    cleanWs()

    checkout scm

    withFolderProperties{
       //JENKINS_CONFIG_VERSION = env.JENKINS_CONFIG_VERSION
       JENKINS_CONFIG_VERSION = 'feature/CSN-97035-quarkus-cicd'
       IS_QUARKUS_PROJECT = true
       BUILDER_JAVA_VERSION = 2.1-SNAPSHOT
    }

    JENKINS_CONFIG_VERSION = JENKINS_CONFIG_VERSION ?: env.JENKINS_CONFIG_VERSION

    checkout changelog: false, poll: false, scm: [
      $class: 'GitSCM',
      branches: [[name: JENKINS_CONFIG_VERSION ]],
      doGenerateSubmoduleConfigurations: false,
      extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: 'jenkins_config']],
      submoduleCfg: [],
      userRemoteConfigs: [[credentialsId: '97a82246-9e9f-41bf-bc30-c373e68c2b5f', url: 'git@github.com:GenesisGaming/jenkins_config.git']]
    ]

    if ( env.JOB_NAME.toLowerCase() ==~ /(.*_build.*)/) {
      if (fileExists('pom.xml')) {
        echo 'Loading maven build script'
        load 'jenkins_config/jenkins-build-maven'
      } else if (fileExists('package.json')) {
        echo 'Loading nodejs build script'
        load 'jenkins_config/jenkins-build-nodejs'
      }
    } else if ( env.JOB_NAME.toLowerCase() ==~ /(.*_gitflow.*)/) {
      echo 'Loading gitflow script'
      load 'jenkins_config/jenkins-gitflow'
    } else if ( env.JOB_NAME.toLowerCase() ==~ /(.*_deploy.*)/) {
      echo 'Loading deploy script'
      load 'jenkins_config/jenkins-deploy'
    }
}