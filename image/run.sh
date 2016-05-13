#!/bin/sh
echo The options are: $@
if [ -z "$SSL" ]; then
    openssl pkcs12 -export -in /ssl/live/izou.info/fullchain.pem -inkey /ssl/live/izou.info/privkey.pem -out pkcs.p12 -name IZOU -passout pass: pass
    keytool -importkeystore -deststorepass IZOU -destkeypass IZOU -destkeystore keystore.jks -srckeystore pkcs.p12 -srcstoretype PKCS12 -srcstorepass pass -alias IZOU
fi
java $@ -Drouter.port=4567 -jar bin/server.jar