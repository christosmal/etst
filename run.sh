#export OPTS="-Xdebug -Xnoagent -Xrunjdwp:transport=dt_socket,address=4000,server=y,suspend=y"
export CLASSPATH=etst/target/etst-1.0-SNAPSHOT.jar:$HOME/.m2/repository/org/jsoup/jsoup/1.10.2/jsoup-1.10.2.jar:$HOME/.m2/repository/org/glassfish/javax.json/1.0.2/javax.json-1.0.2.jar
java -cp $CLASSPATH $OPTS com.christosmal.etst.App $1
