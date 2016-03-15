# PAD-project

## How to run
### Requirements
- **java8**
- nodejs/npm (optional only for the management tools)
- docker>=10 (optional only for the docker version of the fiilesystem)

First of all download the last version of the project with github
```
git clone git@github.com:lucarin91/PAD-project.git
```

### Requirements
- **java8**
- nodejs/npm (optional only for the management tools)
- docker>=10 (optional only for the docker version of the file-system)

### Thread version
```
./gradlew app:run
```
if you want also to start also the management tools run
```
./graadlew webapp:run app:run
```

optional parameters (you can pass to gradle by `-Dexec.args="<parameters>"`):
- `-N <number>` number of servers to start
- `-n <number>` number of seeds servers
- `-gport <number>` the port number used by the gossip protocol
- `-mport <number>` the port number of the management server

example:
```
./gradlew webapp:run app:run -Dexec.args="-N 10 -n 2 -gport 3000 -mport 2000"
```

### Single server
to start a storage server run
```
./gradlew core:run
```
to start a front server run
```
./gradlew api:run
```

optional parameters (you can pass to gradle by `-Dexec.args="<parameters>"`):
- `-id <string>` the port of the server
- `-ip <string>` the ip of the server
- `-p <number>` the port of the server (two successive port are used)
- `-m <id>:<ip>:<port>` a seed server to start the gossip protocol (use this parameters for each seed server)
- `-h <id>:<ip>:<port>` all the server configuration as a single string

example:
```
./gradlew core:run -Dexec.args="-h server1:192.0.0.5:2000 -m server2:192.0.0.2 -m server3:192.0.0.3"
```

### Docker version
to build the docker image of the front node and the storage node run
```
./gradlew core:build core:docker api:build api:docker
```

Now is possible to execute a demo by run the following perl script:
```
perl start-docker.pl <number of storage node :default 5>
```

To manually run a filesystem node you have to create a new docker network with the command:
```
docker network create --subnet=172.18.0.1/16 fs-net
```
then to start a two node file system run
```
docker run -d --net fs-net --ip 172.18.0.1 pad-fs/core:0.1 -h server1:172.18.0.1:2000
docker run -d --net fs-net --ip 172.18.0.2 pad-fs/core:0.1 -h server2:172.18.0.2:2000  -m server1:172.18.0.1:2000

```
and a front end node with
```
docker run -d -p 8080:8080 --net fs-net --ip 172.18.0.20 pad-fs/api:0.1 -h rest:172.18.0.20:2000 -m server2:172.18.0.2:2000
```
now you can also start the management app with:
```
./gradlew webapp:run
```
