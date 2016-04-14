# Project Structure
The system is structured in the following projects:

- **core**, it is a single storage node with all the structure and the essential algorithms.
- **api**, it is a single front-end node
- **app**, it implement a distributed file system with a single API server, where each node run on threads.
- **webapp**, this is a nodejs application that graphically show the state of the file-system.

![screen-shoot of the MonitorWebApp](./img/webapp.png  "Project logical structure")


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

## Tests
In the project core there are the following test classes:

- `ConsistentHashTest`, test for the `ConsistentHash` class.
- `DataTest`, test for the `Data` class.
- `FSTest`, test of the threaded version of the file-system with 10 nodes, it checks all the implemented operation (get, add, update, delete).
- `ModServerTest`, check if the procedure to add or remove a node in the file system work.
- `StorageTest`, test the `PersistenStorage` class.
- `VectorClockTest`, test the `VectorClock` class.

During the testing  are done same coverage analysis to see if all the code is correctly tested.

Here there is the whole coverage of the test in the `core` project:

| % Class        | %	Method        | %	Line           |
|----------------|------------------|------------------|
| 94.1% (16/ 17) | 84.4% (135/ 160) |	83.3% (509/ 611) |


Here there is the coverage statistics by classes:

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
