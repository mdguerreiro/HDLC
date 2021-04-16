# Generate KeyStores with Private RSA 2048 bit keys
keytool -genkeypair -alias locationServerKeyStore -keyalg RSA -keysize 2048 -dname "CN=Baeldung" -validity 365 -storetype PKCS12 -keystore location_server_key_store.p12 -storepass changeit
keytool -genkeypair -alias user1KeyStore -keyalg RSA -keysize 2048 -dname "CN=Baeldung" -validity 365 -storetype PKCS12 -keystore user1_key_store.p12 -storepass changeit
keytool -genkeypair -alias user2KeyStore -keyalg RSA -keysize 2048 -dname "CN=Baeldung" -validity 365 -storetype PKCS12 -keystore user2_key_store.p12 -storepass changeit
keytool -genkeypair -alias user3KeyStore -keyalg RSA -keysize 2048 -dname "CN=Baeldung" -validity 365 -storetype PKCS12 -keystore user3_key_store.p12 -storepass changeit
keytool -genkeypair -alias user4KeyStore -keyalg RSA -keysize 2048 -dname "CN=Baeldung" -validity 365 -storetype PKCS12 -keystore user4_key_store.p12 -storepass changeit
keytool -genkeypair -alias user5KeyStore -keyalg RSA -keysize 2048 -dname "CN=Baeldung" -validity 365 -storetype PKCS12 -keystore user5_key_store.p12 -storepass changeit

# Publish Certificates with Public Keys
keytool -exportcert -alias locationServerKeyStore -storetype PKCS12 -keystore location_server_key_store.p12 -file location_server_certificate.cer -rfc -storepass changeit
keytool -exportcert -alias user1KeyStore -storetype PKCS12 -keystore user1_key_store.p12 -file user1_certificate.cer -rfc -storepass changeit
keytool -exportcert -alias user2KeyStore -storetype PKCS12 -keystore user2_key_store.p12 -file user2_certificate.cer -rfc -storepass changeit
keytool -exportcert -alias user3KeyStore -storetype PKCS12 -keystore user3_key_store.p12 -file user3_certificate.cer -rfc -storepass changeit
keytool -exportcert -alias user4KeyStore -storetype PKCS12 -keystore user4_key_store.p12 -file user4_certificate.cer -rfc -storepass changeit
keytool -exportcert -alias user5KeyStore -storetype PKCS12 -keystore user5_key_store.p12 -file user5_certificate.cer -rfc -storepass changeit

# Import Public Keys of Users From certificates into the Key Store of the Location Server
keytool -importcert -alias user1KeyStore -storetype PKCS12 -keystore location_server_key_store.p12 -file user1_certificate.cer -rfc -storepass changeit -noprompt
keytool -importcert -alias user2KeyStore -storetype PKCS12 -keystore location_server_key_store.p12 -file user2_certificate.cer -rfc -storepass changeit -noprompt
keytool -importcert -alias user3KeyStore -storetype PKCS12 -keystore location_server_key_store.p12 -file user3_certificate.cer -rfc -storepass changeit -noprompt
keytool -importcert -alias user4KeyStore -storetype PKCS12 -keystore location_server_key_store.p12 -file user4_certificate.cer -rfc -storepass changeit -noprompt
keytool -importcert -alias user5KeyStore -storetype PKCS12 -keystore location_server_key_store.p12 -file user5_certificate.cer -rfc -storepass changeit -noprompt

# Import Public Keys of Users 2, 3, 4, 5 and Location Server From certificates into the Key Store of the User 1
keytool -importcert -alias locationServerKeyStore -storetype PKCS12 -keystore user1_key_store.p12 -file location_server_certificate.cer -rfc -storepass changeit -noprompt
keytool -importcert -alias user2KeyStore -storetype PKCS12 -keystore user1_key_store.p12 -file user2_certificate.cer -rfc -storepass changeit -noprompt
keytool -importcert -alias user3KeyStore -storetype PKCS12 -keystore user1_key_store.p12 -file user3_certificate.cer -rfc -storepass changeit -noprompt
keytool -importcert -alias user4KeyStore -storetype PKCS12 -keystore user1_key_store.p12 -file user4_certificate.cer -rfc -storepass changeit -noprompt
keytool -importcert -alias user5KeyStore -storetype PKCS12 -keystore user1_key_store.p12 -file user5_certificate.cer -rfc -storepass changeit -noprompt

# Import Public Keys of Users 1, 3, 4, 5 and Location Server From certificates into the Key Store of the User 2
keytool -importcert -alias locationServerKeyStore -storetype PKCS12 -keystore user2_key_store.p12 -file location_server_certificate.cer -rfc -storepass changeit -noprompt
keytool -importcert -alias user1KeyStore -storetype PKCS12 -keystore user2_key_store.p12 -file user1_certificate.cer -rfc -storepass changeit -noprompt
keytool -importcert -alias user3KeyStore -storetype PKCS12 -keystore user2_key_store.p12 -file user3_certificate.cer -rfc -storepass changeit -noprompt
keytool -importcert -alias user4KeyStore -storetype PKCS12 -keystore user2_key_store.p12 -file user4_certificate.cer -rfc -storepass changeit -noprompt
keytool -importcert -alias user5KeyStore -storetype PKCS12 -keystore user2_key_store.p12 -file user5_certificate.cer -rfc -storepass changeit -noprompt

# Import Public Keys of Users 1, 2, 4, 5 and Location Server From certificates into the Key Store of the User 3
keytool -importcert -alias user1KeyStore -storetype PKCS12 -keystore user3_key_store.p12 -file user1_certificate.cer -rfc -storepass changeit -noprompt
keytool -importcert -alias user2KeyStore -storetype PKCS12 -keystore user3_key_store.p12 -file user2_certificate.cer -rfc -storepass changeit -noprompt
keytool -importcert -alias user4KeyStore -storetype PKCS12 -keystore user3_key_store.p12 -file user4_certificate.cer -rfc -storepass changeit -noprompt
keytool -importcert -alias user5KeyStore -storetype PKCS12 -keystore user3_key_store.p12 -file user5_certificate.cer -rfc -storepass changeit -noprompt
keytool -importcert -alias locationServerKeyStore -storetype PKCS12 -keystore user3_key_store.p12 -file location_server_certificate.cer -rfc -storepass changeit -noprompt

# Import Public Keys of Users 1, 2, 3, 5 and Location Server From certificates into the Key Store of the User 4
keytool -importcert -alias user1KeyStore -storetype PKCS12 -keystore user4_key_store.p12 -file user1_certificate.cer -rfc -storepass changeit -noprompt
keytool -importcert -alias user2KeyStore -storetype PKCS12 -keystore user4_key_store.p12 -file user2_certificate.cer -rfc -storepass changeit -noprompt
keytool -importcert -alias user3KeyStore -storetype PKCS12 -keystore user4_key_store.p12 -file user3_certificate.cer -rfc -storepass changeit -noprompt
keytool -importcert -alias user5KeyStore -storetype PKCS12 -keystore user4_key_store.p12 -file user5_certificate.cer -rfc -storepass changeit -noprompt
keytool -importcert -alias locationServerKeyStore -storetype PKCS12 -keystore user4_key_store.p12 -file location_server_certificate.cer -rfc -storepass changeit -noprompt

# Import Public Keys of Users 1, 2, 3, 4 and Location Server From certificates into the Key Store of the User 3
keytool -importcert -alias user1KeyStore -storetype PKCS12 -keystore user5_key_store.p12 -file user1_certificate.cer -rfc -storepass changeit -noprompt
keytool -importcert -alias user2KeyStore -storetype PKCS12 -keystore user5_key_store.p12 -file user2_certificate.cer -rfc -storepass changeit -noprompt
keytool -importcert -alias user3KeyStore -storetype PKCS12 -keystore user5_key_store.p12 -file user3_certificate.cer -rfc -storepass changeit -noprompt
keytool -importcert -alias user4KeyStore -storetype PKCS12 -keystore user5_key_store.p12 -file user4_certificate.cer -rfc -storepass changeit -noprompt
keytool -importcert -alias locationServerKeyStore -storetype PKCS12 -keystore user5_key_store.p12 -file location_server_certificate.cer -rfc -storepass changeit -noprompt
