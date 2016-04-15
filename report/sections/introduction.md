# Introduction
The aim of the project is to create a weakly consistent distributed file system by using gossiping, consistent hashing and vector clocks.

The communication between nodes exploits the Java socket mechanism, thus it can be executed in different ways: on a single machine with threads, on a cluster of servers or in virtual containers using *Docker*.

The file system is implemented as a key value map of type $\langle string, string \cup number \rangle$ with the following operations:

- **add**(key, value) that adds the pair only if the key is not present;
- **get**(key) returning the value of the key if present;
- **update**(key, value) that updates the key with the new value only if the key already exists;
- **remove**(key) that removes the key if present;
