cd location_server && ./mvnw compile quarkus:dev &
export USERNAME="user1" && export PORT="8091" && cd user_node && ./mvnw compile quarkus:dev &
export USERNAME="user2" && export PORT="8092" && cd user_node && ./mvnw compile quarkus:dev &
export USERNAME="user3" && export PORT="8093" && cd user_node && ./mvnw compile quarkus:dev &
