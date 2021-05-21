## Demonstration

Just run ./start.sh to see a demo running. After the execution, please run stop.sh to shutdown the services. The demo will emulate the behaviour of a grid defined in user_node/src/main/resources/json/grid.json. The keys of this dictionary represent the epochs.

Each epoch will have the duration of 30 seconds. At epoch 0, all the users are apart in the grid. At epoch 1, user1  will get close to user2, however, since we are using byzantine tolerance and there are not enough witnesses around, the request will be denied.

### User Nodes Byzantine Tolerance
At epoch 2, all users will be close to each other. The system will be set to tolerate one byzantine user which will be user1 which will be always denying proof requests in a byzantine behaviour style. Everything will be alright since there will be enough people to witness and the locations will be confirmed. You can check this by accesing each node at http://localhost:809x/location/2, where the x stands for the user node {1, 2, 3, 4, 5}. Here, you will be able to see that everything went ok when the nodes got together at epoch 2.

Script start2.sh is a script made to show that our system is denying requests if they don't respect the byzantine quorum. There will be 2 byzantine users denying the requests, hence, there will not be enough proof to accept a location request and the server will request. If you access the link once again, you will notice that the nodes 3, 4, 5 have location not confirmed at the server. Only one and two will have.


### Location Servers Byzantine Tolerance

Similarly to user nodes, server nodes are also tolerating byzantine behavior. When running ./start.sh, the first demo will start. In this configuration, three servers will start. One of the servers, localhost:8082 is byzantine. It manifest its byzantine behavior in its read and write replies, by sending false acknowledgement or empty read data. In this experience you will be able to see byzantine consesus between the servers and the correct behavior of the system, since one byzantine server is tolerated for the implemented Byzantine Regular Register (1, N) algorithm.

When running ./start2.sh, the second demo will start. Same as above, three servers start. Two servers are byzantine in that case: localhost:8081, localhost:8082 are byzantine and with the same behavior as the byzantine server in ./start.sh. In this case there will be no byzantine consensus between the servers, because the there is too much byzantine servers. In that case, it is not possible to retrieve any location report, in other words, no server will retrieve the data because there is no guarantee about the consistency between them. 
