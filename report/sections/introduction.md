# Introduction
The aim of the project is to create a weakly consist distributed file-system by the use of gossiping, consistent hashing and vector clocks.

The communication between node exploit the Java socket mechanism so it can be execute in different ways: on a single machine with threads, on a cluster of servers or in virtual containers using *docker*.

The file-system is implemented as a map with a string key and a number or string value with the following operations:

- **add**(key, value), add only if the key is not present.
- **get**(key), get the value of the key if present.
- **update**(key, value), update the key with the new value only if the key already exists.
- **remove**(key), remove the key if present.
