# PAD-project
The aim of the project is to create weak consistency distributed filesystem in java, with the use of gossiping, consistent hashing and vector clocks.

The project use the java socket mechanism to work and so it can be executed in different way: on a single machine with the use of threads, on a cluster of server or by using docker containers.

The file-system is implemented as a map with a string key and a number or string value, the implemented operation are:
- **add**(key, value), add only if not present
- **get**(key), get the value of the key if present
- **update**(key, value), update the key with the new value only if the key is already present
- **remove**(key), remove the key if present

## Logical Structure
The file-system is composed by two fundamental parts the front-end, that provide the external access to the file-system through a Restful json API, and the storage node where the actual data are stored.

![Alt text](./img/pad-logic.png  "Project logical structure")

### Communication system
All the internal communication is done with the java `DatagramSocket`, that use the UDP transport packet. All the node either the front end one that the storage server use the gossip protocol to update the list of partecipant server in the file-system.

### Storage protocol
All the storage node manage a consistent hash table and use assign the data to be store to the correct server, in the following way:
- a server is master of all the data with an hash lower or equal of its.
- each data is replicated to a fixed number of server that have the hash greater of the one of the master

The system use a single master storage protocol without consensus, so the data are write or read without wait the server that have the backup version of the data.

When a new server arrive the server with a greater hash give all his data with a the hash lower or equal the one of the new server.

###


## Project Structure


## How use it
