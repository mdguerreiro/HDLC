export SERVER_NAME="location_server_8080" && PORT="8080" && BYZANTINE_USERS=1 && cd location_server && ./mvnw compile quarkus:dev -DdebugHost=0.0.0.0 &
sleep 1
export SERVER_NAME="location_server_8081" && PORT="8081" && BYZANTINE_USERS=1 && cd location_server && ./mvnw compile quarkus:dev -DdebugHost=0.0.0.0 &
sleep 1
export SERVER_NAME="location_server_8082" && PORT="8082" && BYZANTINE_USERS=1 && cd location_server && ./mvnw compile quarkus:dev -DdebugHost=0.0.0.0 &
sleep 1
export USERNAME="user1" && export PORT="8091" && export IS_BYZANTINE="true" && cd user_node && ./mvnw compile quarkus:dev -DdebugHost=0.0.0.0 &
sleep 1
export USERNAME="user2" && export PORT="8092" && export IS_BYZANTINE="false" && cd user_node && ./mvnw compile quarkus:dev -DdebugHost=0.0.0.0 &
sleep 1
export USERNAME="user3" && export PORT="8093" && export IS_BYZANTINE="false" && cd user_node && ./mvnw compile quarkus:dev -DdebugHost=0.0.0.0 &
sleep 1
export USERNAME="user4" && export PORT="8094" && export IS_BYZANTINE="false" && cd user_node && ./mvnw compile quarkus:dev -DdebugHost=0.0.0.0 &
sleep 1
export USERNAME="user5" && export PORT="8095" && export IS_BYZANTINE="false" && cd user_node && ./mvnw compile quarkus:dev -DdebugHost=0.0.0.0 &
