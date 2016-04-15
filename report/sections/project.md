# Project Structure
The system is structured in the following projects:

- **core**, it is a single storage node with all the structure and the essential algorithms.
- **api**, it is a single front-end node.
- **app**, it implements a distributed file system with a single API server, where each node run on threads.
- **webapp**, this is a nodejs application that graphically shows the state of the file system.

![screen-shoot of the MonitorWebApp](./img/webapp.png  "Project logical structure")


## Core
The main classes in the core project are:

- `Messages`, and its children classes -- `MessageManage`, `MessageRequest<T>`, `MessageResponse<T>`, `MessageStatus` -- that represent JSON messages exchanged between the file system nodes.
- `ConsistentHash<T>`, the implementation of a Consistent Hashing owned by each storage nodes.
- `Data<T>`, represent a generic key-value stored inside the system.
- `Node`, a server node of the system either a front-end or a backed server.
- `StorageNode`, it is a storage node object.
- `GossipResource`, it is the resources use by the front-end to exploit the gossip protocol.
- `PersistentStorage`, a wrapper for the `MapDB` library used to persistently store data on the service node.
-  `VectorClock`, the implementation of the vector clock.

### Node class
The node class represents a server of the file system with the id, the IP and the ports both the gossip server and the management service. It also has a `protected` method `send(Node n, Message msg)` method to send a message to a given node through the `DatagramSocket`.

This class is extended by the `StorageNode` and the `GossipResource` which implements a storage node and a front-end node.

The `StorageNode` can be instantiated with the constructor `NodeService(String id, String ipAddress, int port, List<GossipMember> gossipMembers)` that initializes all the needed structures, i.e.:

- `PersistentStorage` class,
- `DatagramSocketServer` to receive message from other nodes,
- `ConsistentHash` class,
- `GossipService` to maintain the consistent hash structure,

It has a thread that continuously checks new messages from the other nodes, and for each one it distinguishes two general cases: either it is a new request or a `MessageManage` with some operation sent by the master to add or modify a backup key.

### PersistentStorage
This class implements the storage on file using a B-Tree Map $\langle Long, Data \rangle$ -- implemented by the library `MapDB` -- this is because it is possible to retrieve in $O(\log n)$ all the keys that have the hash in a certain subset.

This operation can be done with the method `getInterval(Long hash1, Long hash2)` and it used to easily understand who is the master of which keys. In this way, when a new node is found on the network thanks to the gossip protocol, it is simpler to search the keys that it has to manage as master or the keys that it has to store as a backup node.

## Api
The Restful API, implemented with the Spring web framework, exposes the two end point `/api` and `/status`. The first is the public entry-point to operate with the file system. The second is a monitoring tool used by the MonitorWebApp to get a snapshot of all the node in the file system with their data structures.

The following operation can be used on the `/api` resources:

- **get** a key, `method: GET, parameter: key`
- **add** a key, `method: POST, body: {"key": "..", "value": ".."}`
- **update** a key, `method: PUT, body: {"key": "..", "value": ".."}`
- **delete** a key, `method: DEL, parameter: key`

Each of the previous operations return a JOSN object with the `status` field that can be either `ok` or `error` and the `data` field with optional return data.

## Tests
In the project core there are the following test classes:

- `ConsistentHashTest`, tests the methods `add`, `get`, `getPrev`, `getNext` of the `ConsistentHash` class.
- `DataTest`, tests the methods to manage multiple conflicting versions of the same data in the `Data` class.
- `FSTest`, tests the threaded version of the file system with 10 nodes, it checks all the implemented operations (get, add, update, delete).
- `ModServerTest`, checks if the procedure to add or remove a node of the file system works.
- `StorageTest`, tests the `add`, `get`, `update`, `delete`, methods of the `PersistenStorage` class.
- `VectorClockTest`, tests the `increment`, `update` and `compareTo` methods of the `VectorClock` class.


Given the latter test classes also a coverage test analysis is done to understand if all the code is correctly tested.
The first statistics is the coverage of the test in the whole project.

| % Class        | %	Method        | %	Line           |
|----------------|------------------|------------------|
| 94.1% (16/ 17) | 84.4% (135/ 160) |	83.3% (509/ 611) |

It is also to consider that same of those not tested method are only a constructor or the `toString` methods of the classes, so we can consider a good pervasion of the testing.

Now there is a table showing in more detail the coverage of the most important classes of the project:

| Class    	  | %	Method        | %	Line           |
|-------------|-----------------|------------------|
| GossipResource	 | 100% (2/ 2)	| 90% (9/ 10)	| 70% (42/ 60) |
| Node             |	94.4% (17/ 18) | 92.2% (59/ 64) |
| StorageNode	     | 100% (2/ 2)	| 92.9% (13/ 14) | 83.9% (135/ 161) |
| ConsistentHash	 | 100% (1/ 1)	| 68.2% (15/ 22) | 82.9% (58/ 70) |
| Data	| 95.5% (21/ 22)	| 98.4% (60/ 61) |
| PersistentStorage	| 83.3% (10/ 12) | 79.7% (47/ 59) |
| VectorClock	| 86.7% (13/ 15)	| 86.1% (31/ 36) |
| Message	| 100% (6/ 6)	| 100% (9/ 9) |
| MessageManage	|	100% (10/ 10)	| 100% (21/ 21) |
| MessageRequest	| 92.3% (12/ 13)	| 90.9% (30/ 33) |
| MessageResponse	| 90% (9/ 10)	| 81% (17/ 21) |
| MessageStatus	| 0% (0/ 8) | 	0% (0/ 16) |
