Just run ./start.sh to see a demo running. After the execution, please run stop.sh to shutdown the services. The demo will emulate the behaviour of a grid defined in user_node/src/main/resources/json/grid.json. The keys of this dictionary represent the epochs.

Each epoch will have the duration of 30 seconds. At epoch 0, all the users are apart in the grid. At epoch 1, user1  will get close to user2, however, since we are using byzantine tolerance and there are not enough witnesses around, the request will be denied.

At epoch 2, all users will be close to each other. The system will be set to tolerate one byzantine user which will be user1 which will be always denying proof requests in a byzantine behaviour style. Everything will be alright since there will be enough people to witness and the locations will be confirmed. You can check this by accesing each node at http://localhost:809x/location/2, where the x stands for the user node {1, 2, 3, 4, 5}. Here, you will be able to see that everything went ok when the nodes got together at epoch 2.

Script start2.sh is a script made to show that our system is denying requests if they don't respect the byzantine quorum. There will be 2 byzantine users denying the requests, hence, there will not be enough proof to accept a location request and the server will request. If you access the link once again, you will notice that the nodes 3, 4, 5 have location not confirmed at the server. Only one and two will have.


