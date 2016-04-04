use strict;
use warnings;
my $N = shift || 5;
my $IP = shift || '172.18.0';

my $res = `docker network create --subnet=$IP.0/16 fs-net`;
print $res;

for my $i (1..$N){
$res = `docker stop server$i`;
print $res;
$res = `docker rm server$i`;
print $res;
$res = `docker run -d --name server$i --net fs-net --ip $IP.$i pad-fs/core:0.1 -h server$i:$IP.$i:2000 -m server3:$IP.3:2000`;
print $res;
}

$res = `docker stop api`;
print $res;
$res = `docker rm api`;
print $res;
$res = `docker run -d --name api -p 8080:8080 --net fs-net --ip $IP.20 pad-fs/api:0.1 -h rest:$IP.20:2000 -m server3:$IP.3:2000`;
print $res;

`./gradlew webapp:run`;
