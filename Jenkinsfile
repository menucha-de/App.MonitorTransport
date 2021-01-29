podTemplate(
  imagePullSecrets: ['peramicdockerauth'],
  containers: [
    containerTemplate(name: 'build', image: 'peramic.azurecr.io/build:stretch', ttyEnabled: true, command: 'cat')
  ]) {
  node(POD_LABEL) {
    def PLATFORM = "linux-all"
    if ("armhf".equals(params.ARCH)) {
      PLATFORM = "linux-arm"
    } else if ("amd64".equals(params.ARCH)) {
      PLATFORM = "linux-amd64"
    }
    stage('Clone repository') {
      checkout scm
    }
    stage('Build package') {
      container('build') {
        withCredentials([sshUserPrivateKey(credentialsId: "ivy", keyFileVariable: 'keyfile')]) {
          sh """
            export JAVA_TOOL_OPTIONS=-Dfile.encoding=UTF8
            echo "deb http://httpredir.debian.org/debian stretch-backports main" >> /etc/apt/sources.list
            apt-get update
            apt-get install -y openjdk-11-jdk-headless
            update-java-alternatives -s java-1.8.0-openjdk-amd64
            ant -Dant.build.javac.source=1.8 -Dant.build.javac.target=1.8 -Dkeyfile=${keyfile} -Divy.resolver=remote -Divy.settings=ivysettings_remote.xml -Divy.url=${IVY} -Dapi.url=${API} -Dplatform=${PLATFORM} -Djavac.debug=${DEBUG}
          """
        }
      }
    }
  }
}
