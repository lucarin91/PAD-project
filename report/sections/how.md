# User Guide
It is possible to use the distributed file system in the multi-threaded version for a single machine, the multi- server for a cluster of machines or using the *Docker* container either in a single machine or in cluster.

The simplest way to use the file system is to download the last release and run it with the only requirement of Java8. In this way, the application can be used either in the multi-thread version with the `app-<version>.jar` or in the cluster version with the storage node `core-<version>.jar` and the front node `api-<version>.jar`.
In the release is also possible to find the the MonitorWebApp for Linux, MacOS and Windows.


**Requirements**:

- Java8
- *Nodejs/npm (optional only for the MonitorWebApp)*
- *Docker>=10 (optional only for the Docker version of the file system)*


## Thread version
It can be built with:
```bash
./gradlew app:build
```
and run with:
```bash
java -jar app-<version>.jar
```
Optional parameters:

- `-N <number>` number of servers to start
- `-n <number>` number of seeds servers
- `-gport <number>` the port number used by the gossip protocol
- `-mport <number>` the port number of the management server

example:

```bash
java -jar -N 10 -n 2 -gport 3000 -mport 2000"
```

## Single server
It can be built with:
```bash
./gradlew core:build api:build
```

To start a storage node run:
```bash
./java -jar core-<version>.jar
```

To start a front server run:
```bash
./java -jar api-<version>.jar
```

optional parameters:

- `-id <string>` the port of the server
- `-ip <string>` the ip of the server
- `-p <number>` the port of the server (two successive port are used)
- `-m <id>:<ip>:<port>` a seed server to start the gossip protocol (use this parameters for each seed server)
- `-h <id>:<ip>:<port>` all the server configuration as a single string

example:
```bash
./java -jar core-<version>.jar \
    -h server1:192.0.0.5:2000 -m server2:192.0.0.2 -m server3:192.0.0.3
```

## Docker version
To build the docker image of the front node and the storage node run
```bash
./gradlew core:build core:docker api:build api:docker
```

Now is possible to execute a demo by running the following Perl script:
```bash
perl start-docker.pl <number of storage node :default 5>
```

To manually run a file system node you have to create a new docker network with the command:

```bash
docker network create --subnet=172.18.0.1/16 fs-net
```
then to start a two node file system
```bash
docker run -d \
    --net fs-net \
    --ip 172.18.0.1 pad-fs/core:0.1 \
    -h server1:172.18.0.1:2000

docker run -d \
    --net fs-net \
    --ip 172.18.0.2 pad-fs/core:0.1 \
    -h server2:172.18.0.2:2000  -m server1:172.18.0.1:2000
```

and a front end node with
```bash
docker run -d \
    -p 8080:8080 \
    --net fs-net \
    --ip 172.18.0.20 pad-fs/api:0.1 \
    -h rest:172.18.0.20:2000 -m server2:172.18.0.2:2000
```

## MonitorWebApp
The web-app can be used with one of the released versions for the different OS's or run with the Node.js interpreter with the following command:
```bash
./gradlew webapp:run
```
