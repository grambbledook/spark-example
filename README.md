___
# Example Spark Webapp
___
### Build instructions
Execute command `gradle clean build`

Requirements: Latest JDK8 

### Launch instructions
To start application executed command `java -jar ${JAR_PATH} port=${PORT} data=${INITAIL_DATA_FILE}`

Where 
- `jar_path` is a path to example jar
- `port` is target webserver port
- `data` is a path to initial data in format file:/path (data.csv from resources is used by default)

### File structure with initial data

CSV file with structure as in table below 

| id     | owner     | amount    |
|:------:|:---------:|:---------:|
| 1      | John Doe  | 1000.0    |

