# Logical Structure
![Project logical structure](./img/pad-logic.png)

The file system is composed by two fundamental parts:

- the front-end, that provides the external access to the file system through a Restful JSON API;
- the storage system itself where the data are stored and managed.

The former component does not keep any track of the data stored in the system. It only knows the servers, thanks to the gossiping protocol.


## Communication system
All the internal communication relies upon the UDP transport protocol to avoid the overhead of the TCP and assuming a reliable network between the servers exists.

All the nodes, either the front-end and the storage one, exploit gossiping to update the list of the servers involved in the file system.
According to that, each node runs two services on different ports: one in charge of the gossiping protocol, the other for receiving messages from the other nodes.

When a new request arrives to a front-end node it is sent to a random storage node from its list and then it waits 5 seconds for an acknowledgement that the request has been correctly served or not. If no message is received, it is assumed that something went wrong.

## Storage protocol
All the storage nodes use consistent hashing to assign a key-value pair to a given server with the following strategy:

- a server is master for all the keys with lower or equal hash value;
- each key is replicated to a fixed number of subsequence servers in the consistent hash ring;

The system use a single master storage protocol without consensus, so that the value is written or read without waiting for an acknowledgement from the backup servers.

Each time a new server turns on it immediately become master for the keys with a lower hash and the backup server for the keys owned by the previous server. So after its neighbors discovered it, they either send the keys that it has to manage or the keys that it has to keep for backup.

Within the data it is also added a vector clock to keep track of which server updated the value. The vector clock is implemented using a map where the key is the server id and the value is a counter; the servers that are not present in the map are considered with a 0 counter.

So each time a server updates a value as a master it increments the counter with its id inside the object, and forwards this new vector with the key-value to its backup servers.

This vector clock is used every time two version of a value must be compared to decide which is the most recent. If two uncomparable versions of a value are found, the server node creates a value with the two different versions and sets the `conflict` flag.

At this point when a user attempts to get a key, he/she receives all the conflicting versions and can decide which one is the correct version by performing an update operation.

After the update the server resolves the conflict and merges all the vector clocks together. For so, subsequent incoming old values for a key will be discarded.
