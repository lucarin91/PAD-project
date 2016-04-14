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
