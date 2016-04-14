% PAD Project: Distributed File System
% Luca Rinaldi
% 18 March 2016

# Introduction
The aim of the project is to create a weakly consist distributed file-system by the use of gossiping, consistent hashing and vector clocks.

The communication between node exploit the Java socket mechanism so it can be execute in different ways: on a single machine with threads, on a cluster of servers or in virtual containers using *docker*.

The file-system is implemented as a map with a string key and a number or string value with the following operations:

- **add**(key, value), add only if the key is not present.
- **get**(key), get the value of the key if present.
- **update**(key, value), update the key with the new value only if the key already exists.
- **remove**(key), remove the key if present.

# Logical Structure
![Project logical structure](./img/pad-logic.png)

The file-system is composed by two fundamental parts:

- the front-end, that provides the external access to the file-system through a Restful json API,
- the storage system its self where the data are stored and manage.

The first part doesn't have any information of the data store in the system. It only knows the servers, thanks to the gossiping protocol.

## Communication system
All the internal communication are done with the UDP transport protocol, to avoid the overhead of the TCP and assuming a reliable network between the servers.

All the nodes, either the front-end and the storage one, use the gossip protocol to update the list of the servers involved in the file-system.
So that each node has two services running on different ports, one for the gossip protocol and one for receiving messages from the other nodes.

When a new request arrives to a front node it is sent to a random storage node from its list and than it wait 5 second for an acknowledgement that the request is correctly served or an error, otherwise it assume that something goes wrong.

## Storage protocol
All the storage nodes use consistent hashing to assign a key value to a given server with the following strategy:

- a server is master for all the keys with lower or equal hash value.
- each key is replicated to a fixed number of next server in the consistent hash.

The system use a single master storage protocol without consensus, so the value is written or read without waiting for an acknowledgement from the backup's servers.

Each time a new server turn on it immediately became master for the keys with a lower hash and a backup server for the keys owned by the previous servers. So after their neighbors discovered it, they either send the keys that it has to manage or the keys that it has to keep for backups.

Within the data it is also added a vector clock to keep trace of with server update the value. The vector clock is implemented using a map where the key is the server id and for the value a counter, in this way all the serves that don't have a key are considered zero.

So each time a server update a value as a master it increment the counter with its id inside the object, and foreword this new vector with the key value to its backups server.

This vector clock is used every time two version of a value are founded, after some key management, to decide with is the newer. If two unconfrontable version of the value are founded the node server create value with the two different version and put the `conflict` flag to true.

At this point where a user try to get that key, it receive all the conflict version and it can decide with one it consider the correct newer version by done an update operation.

After the update the server resolve the conflict and merge all the vector clock for of all the value, in this way if at same time one of this old values are founded it will be discarded.


# Project Structure
The system is structured in the following projects:

- **core**, it is a single storage node with all the structure and the essential algorithms.
- **api**, it is a single front-end node
- **app**, it implement a distributed file system with a single API server, where each node run on threads.
- **webapp**, this is a nodejs application that graphically show the state of the file-system.

![screen-shoot of the MonitorWebApp ](./img/webapp.png  "Project logical structure")

## Core
The main class in the core project are:

- **Messages**, and its children class - MessageManage, MessageRequest<T>, MessageResponse<T>, MessageStatus - they all represent a json message exchanged between the file-system nodes.
- **ConsistentHash<T>**, the implementation of a Consistent hash owned by each storage nodes.
- **Data<T>**, it represent a generic key value saved inside the system.
- **Node**, a server node of the system either a front-end or a backed server.
- **StorageNode**, it is a storage node object.
- **GossipResurce**, it is the resources use by the front-end to exploit the gossip protocol.
- **PersistentStorage**, a wrapper for the MapDB library used to persistently store data on the service node.
-  **VectorClock**, the implementation of the vector clock.

### Node class
The node class represents a server of the file-system with the id, the ip and the ports of either the gossip server and the management service. It also has an protected method `send(Node n, Message msg)` method to send a message to a given node with the `DatragramSocket`.

This class is extended by the `StorageNode` and the `GossipResurce` which implements a storage node and a front-end node.

The `StorageNode` can be instantiated with the constructor `NodeService(String id, String ipAddress, int port, List<GossipMember> gossipMembers)` that initialize all the needed structure such as:

- `PersistentStorage` class
- `DatagramSocketServer` with use to receive message from the other node
- `ConsistentHash` class,
- `GossipService` to maintain the consistent hash structure

It has a thread that continually check new messages from the other nodes, and for each one it distinguish two general case: if it is a new request from another node or if it is a `MessageManage` with some manage operation sent by the master to add or modified a backup key.

## Api
The Restful API, implemented with the Spring web framework, exposes the two end point `/api` and `/status`. The first is the public entry-point to operate with the file-system. The second is a monitoring tool used by the webapp to get a snapshot of all the node in the file-system with their data structures.

The following operation can be used on the `/api` resources:

- **get** a key, `method: GET, parameter: key`
- **add** a key, `method: POST, body: {"key": "..", "value": ".."}`
- **update** a key, `method: PUT, body: {"key": "..", "value": ".."}`
- **delete** a key, `method: DEL, parameter: key`

Each of the previous operations return a json object with the `status` field that can be either `ok` or `error` and the `data` field with optional return data.

# How to use
It is possible to use the distributed file-system in the thread version for a single machine, the multi server for a cluster of machine or using the docker container either in a single machine or in cluster.

The simplest way to use the file-system is to download the last release and run it with the only requirement of Java8. In this way the application can be used either in the thread version with the `app-<version>.jar` or in the cluster version with the storage node `core-<version>.jar` and the front node `api-<version>.jar`.
In the release is also possible to find the the MonitorWebApp for Linux, MacOS and Windows.


### Requirements

- **java8**
- Nodejs/npm (optional only for the MonitorWebApp)
- Docker>=10 (optional only for the docker version of the file-system)


## Thread version
It can be build with:
```
./gradlew app:build
```
and run with:
```
java -jar app-<version>.jar
```
Optional parameters:

- `-N <number>` number of servers to start
- `-n <number>` number of seeds servers
- `-gport <number>` the port number used by the gossip protocol
- `-mport <number>` the port number of the management server

example:
```
java -jar -N 10 -n 2 -gport 3000 -mport 2000"
```

## Single server
It can be build with:
```
./gradlew core:build api:build
```

To start a storage node run:
```
./java -jar core-<version>.jar
```

To start a front server run:
```
./java -jar api-<version>.jar
```

optional parameters:

- `-id <string>` the port of the server
- `-ip <string>` the ip of the server
- `-p <number>` the port of the server (two successive port are used)
- `-m <id>:<ip>:<port>` a seed server to start the gossip protocol (use this parameters for each seed server)
- `-h <id>:<ip>:<port>` all the server configuration as a single string

example:
```
./java -jar core-<version>.jar \
    -h server1:192.0.0.5:2000 -m server2:192.0.0.2 -m server3:192.0.0.3
```

## Docker version
To build the docker image of the front node and the storage node run
```
./gradlew core:build core:docker api:build api:docker
```

Now is possible to execute a demo by run the following perl script:
```
perl start-docker.pl <number of storage node :default 5>
```

To manually run a file-system node you have to create a new docker network with the command:
```
docker network create --subnet=172.18.0.1/16 fs-net
```
then to start a two node file-system run
```
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
```
docker run -d \
    -p 8080:8080 \
    --net fs-net \
    --ip 172.18.0.20 pad-fs/api:0.1 \
    -h rest:172.18.0.20:2000 -m server2:172.18.0.2:2000
```

## MonitorWebApp
The webapp can be used with one of the release version for the different OS or run with the nodejs interpreter with the following command:
```
./gradlew webapp:run
```
