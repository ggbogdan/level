#!/usr/bin/env groovy
node {

    stage('Initialise') {
        /* Checkout the scripts */
        checkout scm: [
                $class: 'GitSCM',
                userRemoteConfigs: [
                        [
                                url: "https://github.com/ggbogdan/level.git",
                                credentialsId: "github-user"
                        ]
                ],
                branches: [[name: "main"]]
        ], poll: false
    }

    stage('Complete any setup steps') {
        echo "Complete set-up steps"
        echo "${octoperf_test_value}"
    }

    stage('Execute Performance Tests') {
        dir("${WORKSPACE}") {
            sh "/usr/jmeter/bin/jmeter.sh -n -t $SCRIPT_PATH -Jusers=$USERS -JrampUp=$RAMP_UP -Jloop=$LOOP"
//             bat "c:/apache-jmeter/apache-jmeter/bin/jmeter.bat -n -t Shift-Left.jmx -l Shift-Left.jtl -Joptestvalue=${octoperf_test_value}"
        }
    }

    stage('Analyse Results') {
        echo "Analyse results"
    }
}
