___
# Example Spark Webapp
___
### Build instructions
Execute command `gradle clean build -PtestPort=10000`

`testPort` property is optional and is used by integration tests, default value = rnd(10000, 65000) 

Requirements: Latest JDK8 

### Launch instructions
To start application executed command `java -jar ${JAR_PATH}`

Where 
- `jar_path` is a path to example jar

Application will be started on port `8080`

