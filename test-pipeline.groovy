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
//     stage('Send e-mail') {
//     post
//         {
//         always{
//             mail to: "fortestjenkins@ukr.net",
//             subject: "Test Email",
//             body: "Test"
//                }
//          }
//     }
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
def notifyBuild(String buildStatus = 'STARTED') {
  // build status of null means successful
  buildStatus = buildStatus ?: 'SUCCESS'

  // Default values
  def colorName = 'RED'
  def colorCode = '#FF0000'
  def subject = "${buildStatus}: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'"
  def summary = "${subject} (${env.BUILD_URL})"
  def details = """<p>STARTED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]':</p>
    <p>Check console output at &QUOT;<a href='${env.BUILD_URL}'>${env.JOB_NAME} [${env.BUILD_NUMBER}]</a>&QUOT;</p>"""

  // Override default values based on build status
  if (buildStatus == 'STARTED') {
    color = 'YELLOW'
    colorCode = '#FFFF00'
  } else if (buildStatus == 'SUCCESS') {
    color = 'GREEN'
    colorCode = '#00FF00'
  } else {
    color = 'RED'
    colorCode = '#FF0000'
  }

  // Send notifications
  slackSend (color: colorCode, message: summary)

  hipchatSend (color: color, notify: true, message: summary)

  emailext (
      subject: subject,
      body: details,
      recipientProviders: [[$class: 'DevelopersRecipientProvider']]
    )
}
