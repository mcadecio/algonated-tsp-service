# algonated-tsp-service

![alt text](img/logo.svg)

The algonated-tsp-service is part of a larger project Algonated. This service can be used to compile and run java code that is constrained to a specific format.

## High-level diagram
![alt text](img/tsp-hld.png)
## Low-level diagram
![alt text](img/tsp-lld.png)
## Installation

Use the package manager [pip](https://pip.pypa.io/en/stable/) to install foobar.

```shell
mvn clean package
java -jar target/algonated-tsp-service-x.x.jar
```

## Usage
### Exercise Submission Endpoint
Through this endpoint you can submit your java code as a string and it will be compiled and executed.
#### Request
```shell
curl --location --request POST 'https://algonated-tsp-service.herokuapp.com/exercise/submit/tsp' \
--header 'Content-Type: application/json' \
--data-raw '{
    "className": "TSPProblem",
    "packageName": "com.exercise",
    "methodToCall": "runTSP",
    "iterations": 5,
    "importsAllowed": [],
    "illegalMethods": [],
    "code": "import java.util.ArrayList;\nimport java.util.List;\n\npublic class TSPProblem {\n    private List<List<Integer>> solutions = new ArrayList<>();\n\n    public List<Integer> runTSP(double[][] distances, int iterations) {\n        return List.of(1,2,3,4,5);\n    }\n\n}\n",
    "distances": [
        [0, 1.0, 2.0],
        [1.0, 0.0, 3.0],
        [2.0, 3.0, 0]
    ]
}'
```
#### Response
```json
{
    "consoleOutput": "Compile and Run was a success",
    "result": [
        11011
    ],
    "data": [
        1.0,
        2.0,
        3.0,
        4.0,
        5.0
    ],
    "summary": {
        "iterations": 5,
        "timeRun": 0.0,
        "fitness": 1.0,
        "efficacy": 0.0
    },
    "solutions": [],
    "isSuccess": true
}
```

### Demo Request Endpoint
The demo endpoint allows the user to execute a variety of pre-defined algorithms. Some of them include:
* Random Mutation Hill Climbing
* Random Restart Hill Climbing
* Simulated Annealing
* Stochastic Search

#### Request
```shell
curl --location --request POST 'https://algonated-tsp-service.herokuapp.com/exercise/demo/tsp' \
--header 'Content-Type: application/json' \
--data-raw '{
    "algorithm": "sa",
    "iterations": 1,
    "temperature": 100.0,
    "coolingRate": 5.3,
    "distances": [
        [0, 1.0, 2.0],
        [1.0, 0.0, 3.0],
        [2.0, 3.0, 0]
    ]
}'
```
#### Response
```json
{
    "consoleOutput": "Your Demo is ready!",
    "result": [
        2,
        0,
        1
    ],
    "distances": [
      [0, 1.0, 2.0],
      [1.0, 0.0, 3.0],
      [2.0, 3.0, 0]
    ],
    "summary": {
        "iterations": 1,
        "timeRun": 0.0,
        "fitness": 6.0,
        "efficacy": -1.0
    },
    "solutions": [
        [2, 0, 1]
    ],
    "isSuccess": true
}
```

## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to update tests as appropriate.
