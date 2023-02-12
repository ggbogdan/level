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
        echo "${test_value}"
    }

    stage('Execute Performance Tests') {
        dir("${WORKSPACE}") {
            sh "/usr/jmeter/bin/jmeter.sh -n -t $SCRIPT_PATH -Jusers=$USERS -JrampUp=$RAMP_UP -Jloop=$LOOP -l test.jtl -e -o report -f"
            sh "sudo cp -r /var/lib/jenkins/workspace/test2/report/ /opt/tomcat/webapps/"

        }
    }  
    post('Send e-mail'){
        always{
            mail to: "fortestjenkins@ukr.net",
            subject: "Test Email",
            body: "Test"
        }
    }
    step([$class: 'ArtifactArchiver', artifacts: 'test.jtl'])
    

}
// pipeline {
//     agent any
//     stages {
//         stage('Hello') {
//             steps {
//                 echo "Hello world"
//                     }
//             }
//         }
//     post{
//         always{
//             mail to: "fortestjenkins@ukr.net",
//             subject: "Test Email",
//             body: "Test"
//         }
//     }
// }
