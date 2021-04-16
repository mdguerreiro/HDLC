export BYZANTINE_USERS=1 && cd location_server && ./mvnw compile quarkus:dev &
export USERNAME="user1" && export PORT="8091" && export isByzantine="true" && cd user_node && ./mvnw compile quarkus:dev &
export USERNAME="user2" && export PORT="8092" && export isByzantine="true" && cd user_node && ./mvnw compile quarkus:dev &
export USERNAME="user3" && export PORT="8093" && export isByzantine="false" && cd user_node && ./mvnw compile quarkus:dev &
export USERNAME="user4" && export PORT="8094" && export isByzantine="false" && cd user_node && ./mvnw compile quarkus:dev &
export USERNAME="user5" && export PORT="8095" && export isByzantine="false" && cd user_node && ./mvnw compile quarkus:dev &
