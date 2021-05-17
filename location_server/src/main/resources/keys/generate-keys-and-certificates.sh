# Generate KeyStores with Private RSA 2048 bit keys
keytool -genkeypair -alias location_server_8080_key_store -keyalg RSA -keysize 2048 -dname "CN=Baeldung" -validity 365 -storetype PKCS12 -keystore location_server_8080_key_store.p12 -storepass location_server_8080_pwd
keytool -genkeypair -alias location_server_8081_key_store -keyalg RSA -keysize 2048 -dname "CN=Baeldung" -validity 365 -storetype PKCS12 -keystore location_server_8081_key_store.p12 -storepass location_server_8081_pwd
keytool -genkeypair -alias location_server_8082_key_store -keyalg RSA -keysize 2048 -dname "CN=Baeldung" -validity 365 -storetype PKCS12 -keystore location_server_8082_key_store.p12 -storepass location_server_8082_pwd
keytool -genkeypair -alias user1_key_store -keyalg RSA -keysize 2048 -dname "CN=Baeldung" -validity 365 -storetype PKCS12 -keystore user1_key_store.p12 -storepass user1_pwd
keytool -genkeypair -alias user2_key_store -keyalg RSA -keysize 2048 -dname "CN=Baeldung" -validity 365 -storetype PKCS12 -keystore user2_key_store.p12 -storepass user2_pwd
keytool -genkeypair -alias user3_key_store -keyalg RSA -keysize 2048 -dname "CN=Baeldung" -validity 365 -storetype PKCS12 -keystore user3_key_store.p12 -storepass user3_pwd
keytool -genkeypair -alias user4_key_store -keyalg RSA -keysize 2048 -dname "CN=Baeldung" -validity 365 -storetype PKCS12 -keystore user4_key_store.p12 -storepass user4_pwd
keytool -genkeypair -alias user5_key_store -keyalg RSA -keysize 2048 -dname "CN=Baeldung" -validity 365 -storetype PKCS12 -keystore user5_key_store.p12 -storepass user5_pwd

# Publish Certificates with Public Keys
keytool -exportcert -alias location_server_8080_key_store -storetype PKCS12 -keystore location_server_8080_key_store.p12 -file location_server_8080_certificate.cer -rfc -storepass location_server_8080_pwd
keytool -exportcert -alias location_server_8081_key_store -storetype PKCS12 -keystore location_server_8081_key_store.p12 -file location_server_8081_certificate.cer -rfc -storepass location_server_8081_pwd
keytool -exportcert -alias location_server_8082_key_store -storetype PKCS12 -keystore location_server_8082_key_store.p12 -file location_server_8082_certificate.cer -rfc -storepass location_server_8082_pwd
keytool -exportcert -alias user1_key_store -storetype PKCS12 -keystore user1_key_store.p12 -file user1_certificate.cer -rfc -storepass user1_pwd
keytool -exportcert -alias user2_key_store -storetype PKCS12 -keystore user2_key_store.p12 -file user2_certificate.cer -rfc -storepass user2_pwd
keytool -exportcert -alias user3_key_store -storetype PKCS12 -keystore user3_key_store.p12 -file user3_certificate.cer -rfc -storepass user3_pwd
keytool -exportcert -alias user4_key_store -storetype PKCS12 -keystore user4_key_store.p12 -file user4_certificate.cer -rfc -storepass user4_pwd
keytool -exportcert -alias user5_key_store -storetype PKCS12 -keystore user5_key_store.p12 -file user5_certificate.cer -rfc -storepass user5_pwd

# Import Public Keys of Users and location servers 8081, 8082 from certificates into the Key Store of the Location Server 8080
keytool -importcert -alias user1_key_store -storetype PKCS12 -keystore location_server_8080_key_store.p12 -file user1_certificate.cer -rfc -storepass location_server_8080_pwd -noprompt
keytool -importcert -alias user2_key_store -storetype PKCS12 -keystore location_server_8080_key_store.p12 -file user2_certificate.cer -rfc -storepass location_server_8080_pwd -noprompt
keytool -importcert -alias user3_key_store -storetype PKCS12 -keystore location_server_8080_key_store.p12 -file user3_certificate.cer -rfc -storepass location_server_8080_pwd -noprompt
keytool -importcert -alias user4_key_store -storetype PKCS12 -keystore location_server_8080_key_store.p12 -file user4_certificate.cer -rfc -storepass location_server_8080_pwd -noprompt
keytool -importcert -alias user5_key_store -storetype PKCS12 -keystore location_server_8080_key_store.p12 -file user5_certificate.cer -rfc -storepass location_server_8080_pwd -noprompt
keytool -importcert -alias location_server_8081_key_store -storetype PKCS12 -keystore location_server_8080_key_store.p12 -file location_server_8081_certificate.cer -rfc -storepass location_server_8080_pwd -noprompt
keytool -importcert -alias location_server_8082_key_store -storetype PKCS12 -keystore location_server_8080_key_store.p12 -file location_server_8082_certificate.cer -rfc -storepass location_server_8080_pwd -noprompt

# Import Public Keys of Users and location servers 8080, 8082 from certificates into the Key Store of the Location Server 8081
keytool -importcert -alias user1_key_store -storetype PKCS12 -keystore location_server_8081_key_store.p12 -file user1_certificate.cer -rfc -storepass location_server_8081_pwd -noprompt
keytool -importcert -alias user2_key_store -storetype PKCS12 -keystore location_server_8081_key_store.p12 -file user2_certificate.cer -rfc -storepass location_server_8081_pwd -noprompt
keytool -importcert -alias user3_key_store -storetype PKCS12 -keystore location_server_8081_key_store.p12 -file user3_certificate.cer -rfc -storepass location_server_8081_pwd -noprompt
keytool -importcert -alias user4_key_store -storetype PKCS12 -keystore location_server_8081_key_store.p12 -file user4_certificate.cer -rfc -storepass location_server_8081_pwd -noprompt
keytool -importcert -alias user5_key_store -storetype PKCS12 -keystore location_server_8081_key_store.p12 -file user5_certificate.cer -rfc -storepass location_server_8081_pwd -noprompt
keytool -importcert -alias location_server_8080_key_store -storetype PKCS12 -keystore location_server_8081_key_store.p12 -file location_server_8080_certificate.cer -rfc -storepass location_server_8081_pwd -noprompt
keytool -importcert -alias location_server_8082_key_store -storetype PKCS12 -keystore location_server_8081_key_store.p12 -file location_server_8082_certificate.cer -rfc -storepass location_server_8081_pwd -noprompt

# Import Public Keys of Users and location servers 8081, 8082 from certificates into the Key Store of the Location Server 8080
keytool -importcert -alias user1_key_store -storetype PKCS12 -keystore location_server_8082_key_store.p12 -file user1_certificate.cer -rfc -storepass location_server_8082_pwd -noprompt
keytool -importcert -alias user2_key_store -storetype PKCS12 -keystore location_server_8082_key_store.p12 -file user2_certificate.cer -rfc -storepass location_server_8082_pwd -noprompt
keytool -importcert -alias user3_key_store -storetype PKCS12 -keystore location_server_8082_key_store.p12 -file user3_certificate.cer -rfc -storepass location_server_8082_pwd -noprompt
keytool -importcert -alias user4_key_store -storetype PKCS12 -keystore location_server_8082_key_store.p12 -file user4_certificate.cer -rfc -storepass location_server_8082_pwd -noprompt
keytool -importcert -alias user5_key_store -storetype PKCS12 -keystore location_server_8082_key_store.p12 -file user5_certificate.cer -rfc -storepass location_server_8082_pwd -noprompt
keytool -importcert -alias location_server_8080_key_store -storetype PKCS12 -keystore location_server_8082_key_store.p12 -file location_server_8080_certificate.cer -rfc -storepass location_server_8082_pwd -noprompt
keytool -importcert -alias location_server_8081_key_store -storetype PKCS12 -keystore location_server_8082_key_store.p12 -file location_server_8081_certificate.cer -rfc -storepass location_server_8082_pwd -noprompt

# Import Public Keys of Users 2, 3, 4, 5 and Location Servers From certificates into the Key Store of the User 1
keytool -importcert -alias location_server_8080_key_store -storetype PKCS12 -keystore user1_key_store.p12 -file location_server_8080_certificate.cer -rfc -storepass user1_pwd -noprompt
keytool -importcert -alias location_server_8081_key_store -storetype PKCS12 -keystore user1_key_store.p12 -file location_server_8081_certificate.cer -rfc -storepass user1_pwd -noprompt
keytool -importcert -alias location_server_8082_key_store -storetype PKCS12 -keystore user1_key_store.p12 -file location_server_8082_certificate.cer -rfc -storepass user1_pwd -noprompt
keytool -importcert -alias user2_key_store -storetype PKCS12 -keystore user1_key_store.p12 -file user2_certificate.cer -rfc -storepass user1_pwd -noprompt
keytool -importcert -alias user3_key_store -storetype PKCS12 -keystore user1_key_store.p12 -file user3_certificate.cer -rfc -storepass user1_pwd -noprompt
keytool -importcert -alias user4_key_store -storetype PKCS12 -keystore user1_key_store.p12 -file user4_certificate.cer -rfc -storepass user1_pwd -noprompt
keytool -importcert -alias user5_key_store -storetype PKCS12 -keystore user1_key_store.p12 -file user5_certificate.cer -rfc -storepass user1_pwd -noprompt

# Import Public Keys of Users 1, 3, 4, 5 and Location Servers From certificates into the Key Store of the User 2
keytool -importcert -alias location_server_8080_key_store -storetype PKCS12 -keystore user2_key_store.p12 -file location_server_8080_certificate.cer -rfc -storepass user2_pwd -noprompt
keytool -importcert -alias location_server_8081_key_store -storetype PKCS12 -keystore user2_key_store.p12 -file location_server_8081_certificate.cer -rfc -storepass user2_pwd -noprompt
keytool -importcert -alias location_server_8082_key_store -storetype PKCS12 -keystore user2_key_store.p12 -file location_server_8082_certificate.cer -rfc -storepass user2_pwd -noprompt
keytool -importcert -alias user1_key_store -storetype PKCS12 -keystore user2_key_store.p12 -file user1_certificate.cer -rfc -storepass user2_pwd -noprompt
keytool -importcert -alias user3_key_store -storetype PKCS12 -keystore user2_key_store.p12 -file user3_certificate.cer -rfc -storepass user2_pwd -noprompt
keytool -importcert -alias user4_key_store -storetype PKCS12 -keystore user2_key_store.p12 -file user4_certificate.cer -rfc -storepass user2_pwd -noprompt
keytool -importcert -alias user5_key_store -storetype PKCS12 -keystore user2_key_store.p12 -file user5_certificate.cer -rfc -storepass user2_pwd -noprompt

# Import Public Keys of Users 1, 2, 4, 5 and Location Server From certificates into the Key Store of the User 3
keytool -importcert -alias location_server_8080_key_store -storetype PKCS12 -keystore user3_key_store.p12 -file location_server_8080_certificate.cer -rfc -storepass user3_pwd -noprompt
keytool -importcert -alias location_server_8081_key_store -storetype PKCS12 -keystore user3_key_store.p12 -file location_server_8081_certificate.cer -rfc -storepass user3_pwd -noprompt
keytool -importcert -alias location_server_8082_key_store -storetype PKCS12 -keystore user3_key_store.p12 -file location_server_8082_certificate.cer -rfc -storepass user3_pwd -noprompt
keytool -importcert -alias user1_key_store -storetype PKCS12 -keystore user3_key_store.p12 -file user1_certificate.cer -rfc -storepass user3_pwd -noprompt
keytool -importcert -alias user2_key_store -storetype PKCS12 -keystore user3_key_store.p12 -file user2_certificate.cer -rfc -storepass user3_pwd -noprompt
keytool -importcert -alias user4_key_store -storetype PKCS12 -keystore user3_key_store.p12 -file user4_certificate.cer -rfc -storepass user3_pwd -noprompt
keytool -importcert -alias user5_key_store -storetype PKCS12 -keystore user3_key_store.p12 -file user5_certificate.cer -rfc -storepass user3_pwd -noprompt

# Import Public Keys of Users 1, 2, 3, 5 and Location Servers From certificates into the Key Store of the User 4
keytool -importcert -alias location_server_8080_key_store -storetype PKCS12 -keystore user4_key_store.p12 -file location_server_8080_certificate.cer -rfc -storepass user4_pwd -noprompt
keytool -importcert -alias location_server_8081_key_store -storetype PKCS12 -keystore user4_key_store.p12 -file location_server_8081_certificate.cer -rfc -storepass user4_pwd -noprompt
keytool -importcert -alias location_server_8082_key_store -storetype PKCS12 -keystore user4_key_store.p12 -file location_server_8082_certificate.cer -rfc -storepass user4_pwd -noprompt
keytool -importcert -alias user1_key_store -storetype PKCS12 -keystore user4_key_store.p12 -file user1_certificate.cer -rfc -storepass user4_pwd -noprompt
keytool -importcert -alias user2_key_store -storetype PKCS12 -keystore user4_key_store.p12 -file user2_certificate.cer -rfc -storepass user4_pwd -noprompt
keytool -importcert -alias user3_key_store -storetype PKCS12 -keystore user4_key_store.p12 -file user3_certificate.cer -rfc -storepass user4_pwd -noprompt
keytool -importcert -alias user5_key_store -storetype PKCS12 -keystore user4_key_store.p12 -file user5_certificate.cer -rfc -storepass user4_pwd -noprompt

# Import Public Keys of Users 1, 2, 3, 4 and Location Servers From certificates into the Key Store of the User 3
keytool -importcert -alias location_server_8080_key_store -storetype PKCS12 -keystore user5_key_store.p12 -file location_server_8080_certificate.cer -rfc -storepass user5_pwd -noprompt
keytool -importcert -alias location_server_8081_key_store -storetype PKCS12 -keystore user5_key_store.p12 -file location_server_8081_certificate.cer -rfc -storepass user5_pwd -noprompt
keytool -importcert -alias location_server_8082_key_store -storetype PKCS12 -keystore user5_key_store.p12 -file location_server_8082_certificate.cer -rfc -storepass user5_pwd -noprompt
keytool -importcert -alias user1_key_store -storetype PKCS12 -keystore user5_key_store.p12 -file user1_certificate.cer -rfc -storepass user5_pwd -noprompt
keytool -importcert -alias user2_key_store -storetype PKCS12 -keystore user5_key_store.p12 -file user2_certificate.cer -rfc -storepass user5_pwd -noprompt
keytool -importcert -alias user3_key_store -storetype PKCS12 -keystore user5_key_store.p12 -file user3_certificate.cer -rfc -storepass user5_pwd -noprompt
keytool -importcert -alias user4_key_store -storetype PKCS12 -keystore user5_key_store.p12 -file user4_certificate.cer -rfc -storepass user5_pwd -noprompt
