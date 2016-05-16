#!/bin/bash
echo The options are: $@
if [[ -z "$SSL" ]]; then
    echo ssl returned true
    if [[ ! -f keystore.jks ]]; then
        echo file does not exist
        echo generating keystore
        openssl pkcs12 -export -in /ssl/live/izou.info/fullchain.pem -inkey /ssl/live/izou.info/privkey.pem -out pkcs.p12 -name IZOU -passout pass:pass
        keytool -importkeystore -deststorepass XL750BK -destkeystore keystore.jks -srckeystore pkcs.p12 -srcstoretype PKCS12 -srcstorepass pass -alias IZOU
    fi
fi
java $@ -Drouter.port=4567 -jar bin/server.jar