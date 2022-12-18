#!/usr/bin/env groovy
node {

    stage('Initialise') {
        /* Checkout the scripts */
        checkout scm: [
                $class: 'GitSCM',
                userRemoteConfigs: [
                        [
                                url: "https://bitbucket.org/xxxxxx/performance-test-repo.git",
                                credentialsId: "octoperf-user"
                        ]
                ],
                branches: [[name: "master"]]
        ], poll: false
    }

    stage('Complete any setup steps') {
        echo "Complete set-up steps"
        echo "${octoperf_test_value}"
    }

    stage('Execute Performance Tests') {
        dir("${WORKSPACE}/scripts") {
            bat "c:/apache-jmeter/apache-jmeter/bin/jmeter.bat -n -t Shift-Left.jmx -l Shift-Left.jtl -Joptestvalue=${octoperf_test_value}"
        }
    }

    stage('Analyse Results') {
        echo "Analyse results"
    }
}
