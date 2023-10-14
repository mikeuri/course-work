#!/usr/bin/env groovy

def branchName = env.BRANCH_NAME

properties([
        pipelineTriggers([cron('H 23 * * *')]),
        buildDiscarder([
                $class               : 'EnhancedOldBuildDiscarder',
                artifactDaysToKeepStr: '7',
                artifactNumToKeepStr : '10',
                daysToKeepStr        : '7',
                discardOnlyOnSuccess : false,
                numToKeepStr         : '20',
        ]),
        disableConcurrentBuilds(),
        [$class: 'RebuildSettings', autoRebuild: false, rebuildDisabled: false],
        parameters([
                choice(
                        name: 'environment',
                        choices: ['dev', 'stg'],
                        description: ''
                ),
                choice(
                        name: 'groups',
                        choices: ['smoke','regression'],
                        description: ''
                ),
                choice(
                        name: 'browser',
                        choices: ['chrome','firefox'],
                        description: ''
                ),
                /*choice(
                        name: 'testbed',
                        choices: ['local','grid'],
                        description: ''
                ),*/
                booleanParam(
                        name: 'NO_GUI',
                        defaultValue: true,
                        description: 'Run tests without GUI'
                ),
        ])
])

node {
    currentBuild.displayName = "#" + (currentBuild.number + "-${params.environment}" + "-${params.groups}")

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/// ----- Stage: Build && Run tests
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    try {
        stage('Build && Run tests') {
            cleanWs()
            checkout scm
            // sh
            withMaven(maven: 'maven-3.9.5') {
                sh "mvn clean"
                sh "mvn test -Denv=${params.environment} -Dgroups=${params.groups} -Dweb.browser.no.gui=${params.NO_GUI} -Dtestbed=grid -Dweb.browser.name=${params.browser}"
            }
        }
    } catch (err) {
        throw err
    } finally {
        stage('Generate Reports') {
            withMaven(maven: 'maven-3.9.5') {
                sh "mvn allure:report"
            }
            publishHTML(
                    target: [
                            reportName           : "Allure Report",
                            reportDir            : "${WORKSPACE}/target/site/allure-maven-plugin",
                            reportFiles          : "index.html",
                            keepAll              : true,
                            alwaysLinkToLastBuild: true,
                            allowMissing         : false
                    ]
            )
        }
    }
}
