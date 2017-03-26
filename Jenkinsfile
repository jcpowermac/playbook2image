#!groovy

// vim: ft=groovy

/*
 * Process
 * 1. Create image from Dockerfile
 * a. Use new-build to create image
 * 2. Create S2I image from test source
 * a. Use new-build to create s2i image
 * 3. Run image testing output
 * a. Use something to run this and test output ?  Job
 * 
 */
 
node {
    def source = ""
    if (env.CHANGE_URL) {
        def newBuild = null
        def changeUrl = env.CHANGE_URL
        def githubUri = changeUrl.replaceAll("github.com/", "api.github.com/repos/")
        githubUri = githubUri.replaceAll("pull", "pulls")

        sh("curl -o ${env.WORKSPACE}/github.json ${githubUri}")
        def pull = readJSON file: 'github.json'

        openshift.withCluster() {
            openshift.withProject() {
                try {
                    newBuild = openshift.newBuild("${pull.head.repo.clone_url}#${pull.head.ref}")
                    echo "newBuild created: ${newBuild.count()} objects : ${newBuild.names()}"
                    def builds = newBuild.narrow("bc").related("builds")

                    timeout(10) {
                        builds.watch {
                            if (it.count() == 0) {
                                return false
                            }
                            echo "Detected new builds created by buildconfig: ${it.names()}"
                            return true
                        }
                        builds.untilEach(1) {
                            return it.object().status.phase == "Complete"
                        }
                    }
                }
                finally {
                    if (newBuild) {
                        def result = newBuild.narrow("bc").logs()
                        echo "Result of logs operation:"
                        echo "  status: ${result.status}"
                        echo "  stderr: ${result.err}"
                        echo "  number of actions to fulfill: ${result.actions.size()}"
                        echo "  first action executed: ${result.actions[0].cmd}"

                        if (result.status != 0) {
                            echo "${result.out}"
                            error("Image Build Failed")
                        }
                        newBuild.delete()
                    }
                }
            }
        }
    }
}



