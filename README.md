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

### Thread version
```
./gradlew app:run
```
il you want also to start the management tools run
```
./graadlew webapp:run app:run
```

### Single server
to start a sotage server run
```
./gradlew core:run
```
to start a front server run
```
./gradlew api:run
```

### Docker version
to build the docker iimage for the front node and the storage node run
```
./gradlew core:build core:docker api:build api:docker
```

Now iis possible to execute a demo by run the following perl script:
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
