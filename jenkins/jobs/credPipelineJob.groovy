#!groovy

@Library('Utils')
import com.redhat.*

node {
    def id = null
    def utils = new Utils()

    openshift.withCluster() {
        def secret = openshift.selector( "secret/github" ).object()
        println(secret.data)
        //id = utils.createCredentials("username","password", "github")
    }
                
    //println(id)
    //build job: 'seed', parameters: [[$class: 'StringParameterValue', name: 'CRED_ID', value: id]]
}
