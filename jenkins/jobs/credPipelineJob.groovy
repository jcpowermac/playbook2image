#!groovy

@Library('Utils')
import com.redhat.*

node {
    def id = null
    def utils = new Utils()

    openshift.withCluster() {
        def secret = openshift.selector( "secret/github" ).object()
        id = utils.createCredentialsFromOpenShift(secret, "github") 
    }
                
    println(id)
    //build job: 'seed', parameters: [[$class: 'StringParameterValue', name: 'CRED_ID', value: id]]
}
