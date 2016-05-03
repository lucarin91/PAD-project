# PAD-FS
#### A distributed persistent data storage

<small>Created by <a href="http://www.github.com/lucarin91">Luca Rinaldi</a></small>



### Characteristics:

weak consistency  
<!-- .element: class="fragment" -->

dynamic number of servers
<!-- .element: class="fragment" -->

replication on multiple servers
<!-- .element: class="fragment" -->

RESTful API to interact
<!-- .element: class="fragment" -->

MonitorApp to manage the whole file system
<!-- .element: class="fragment" -->

data versing and data conflict resolution
<!-- .element: class="fragment" -->


#### Can be executed:

- multi-threaded version

- multi-server version

- multi-container version (with Docker)



### Logical Structure
![img](img/pad-logic.png)

Note: Two type of server one for the storage and one for the user interaction



### Storage Protocol

consistent hashing for key distribution (with logical servers)
<!-- .element: class="fragment" -->

data replication among servers
<!-- .element: class="fragment" -->

single master without consensus
<!-- .element: class="fragment" -->

versing to manage conflicts
<!-- .element: class="fragment" -->


#### Available Operations

- `add(key, value)`

- `get(key)`

- `update(key)`

- `delete(key)`


#### Conflict resolution

- conflicting values for a key are stored

- all conflicting values of a key are returned to the user

- the user can choose the correct value with the `update(key, value)`

- discard all values with `delete(key)`



### Project Structure

- core

- api

- app

- webapp


### core
- it is a library for the rest of the project

- it represent a single **Storage Node**


### api

it is a **front-end node** that expose the following API:

- `GET /api?key=<` *key name* `>`

- `POST /api`, `{"key": "..", "value": ".."}`

- `PUT /api`, `{"key": "..", "value": ".."}`

- `DEL /api?key=<`*key name*`>`


### app

it is a test version of the file-system using **threads**


### webapp

nodejs app to monitoring the file-system

![img](img/webapp.png)



### Test Coverage

| Class    	        | %	Method       | %	Line          |
|-------------------|----------------|------------------|
| GossipResource	  | 90% (9/ 10)	   | 70% (42/ 60)     |
| Node              |	94.4% (17/ 18) | 92.2% (59/ 64)   |
| StorageNode	      | 92.9% (13/ 14) | 83.9% (135/ 161) |
| ConsistentHash	  | 68.2% (15/ 22) | 82.9% (58/ 70)   |
| Data	            | 95.5% (21/ 22) | 98.4% (60/ 61)   |
| PersistentStorage	| 83.3% (10/ 12) | 79.7% (47/ 59)   |
| VectorClock	      | 86.7% (13/ 15) | 86.1% (31/ 36)   |
| Message	          | 100% (6/ 6)	   | 100% (9/ 9)      |
| MessageManage	    |	100% (10/ 10)	 | 100% (21/ 21)    |
| MessageRequest	  | 92.3% (12/ 13) | 90.9% (30/ 33)   |
| MessageResponse	  | 90% (9/ 10)	   | 81% (17/ 21)     |
| MessageStatus	    | 0% (0/ 8)      | 	0% (0/ 16)      |
<!-- .element: style="font-size:20px" -->



### multi-server version

```bash
./java -jar core-<version>.jar <parameters>
```

```bash
./java -jar api-<version>.jar <parameters>
```

optional parameters:
```bash
-id <string>        // the id of the server
-ip <string>        // the ip of the server
-p <number>         // the port of the server
-m <id>:<ip>:<port> // a seed server to for the gossip protocol
-h <id>:<ip>:<port> // all the server configuration as a single string
```
example:

```bash
./java -jar core-<version>.jar \
    -h server1:192.0.0.5:2000 \
    -m server2:192.0.0.2 -m server3:192.0.0.3
```


### multi-threaded version
```bash
java -jar app-<version>.jar
```

Optional parameters:
```
-N <number>       // number of servers to start
-n <number>       // number of seeds servers
-gport <number>   // port of the gossip protocol
-mport <number>   // port of the management service
```

example:
```bash
java -jar app-<version>.jar \
    -N 10 -n 2 -gport 3000 -mport 2000
```


### Docker version
To build the docker images
```bash
./gradlew core:build core:docker api:build api:docker
```

start a demo with following Perl script:
```bash
perl start-docker.pl <number of storage node :default 5>
```



### More details
find the code [here](https://github.com/lucarin91/PAD-project)

or read more [here](https://github.com/lucarin91/PAD-project/blob/master/REPORT.pdf)
