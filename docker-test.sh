#!/bin/bash

docker network create --subnet=172.18.0.0/16 fs-net

IP="172.18.0"
docker run -d --net fs-net --ip ${IP}.2 pad-fs/core:0.1 -h test2:${IP}.2:2000 -m test3:${IP}.3:2000

docker run -d --net fs-net --ip ${IP}.3 pad-fs/core:0.1 -h test3:${IP}.3:2000 -m test3:${IP}.3:2000

docker run --net fs-net --ip ${IP}.4 pad-fs/core:0.1 -h test4:${IP}.4:2000 -m test3:${IP}.3:2000