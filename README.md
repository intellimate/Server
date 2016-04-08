# Izou-Server

[![Build Status](https://travis-ci.org/intellimate/Server.svg?branch=master)](https://travis-ci.org/intellimate/Server)

## Requirements

 * Java 8
 * MySQL 5.6
 * Gradle (optional, but recommended)
 
## Running the Izou-Server (not yet possible)

you easily can run the Worker-Service via docker:

```bash
docker pull intellimate/server:latest
docker run -p 4567:4567 --link mysqldb:db -d intellimate/server:latest -Ddatabase.url=jdbc:mysql:url -Ddatabase.username=user -Ddatabase.password=password
```

where:
* `database.url` is the jdbc-url
* `database.username` username of the database account
* `database.password` the password of the database account
* `os.url` is the url of the object-service

this will bind the Server on post 4567 on 127.0.0.1 of the hosts machine.

The configuration files are located under `/conf` on the image and named `configuration.properties` and `logging.xml`.
Please see below for more details.
 
## Installation

```bash
# Clone repository and change into its directory.
git clone https://github.com/intellimate/server && cd server

# Import database schema from ./src/main/resources/db.sql

# Install all dependencies and compile sources.
# Use gradle instead of ./gradlew if you have Gradle installed.
./gradlew jar
```

## Configuration
 
In the git, the configuration is detailed in `./conf/configuration.properties`. You can alter the 
configuration-file to permanently change properties. Every property can be overridden by setting a global-property via
`-D{key}={value}`.

You can also set the config-file location with the system-property `server.config`, e.g. `-server.config=location`.
If none passed the app will always look for the configuration file in `./conf/`.

The logging is specified in the logging-file `./conf/logging.xml`. You can alter the logging-file to permanently change properties.

You can also set the config-file location with the system-property `logback.configurationFile`, e.g. `-Dlogback.configurationFile=location`.
If none passed the app will always look for the logging file in `./conf/`.

For testing purposes it is recommended to pass `-Dcaching.enabled=false` to disable caching (some values are cached for 5 minutes), this can also be activated in the command line.

## Database

To initialise the Database it is recommended to use the `db.sql` script located in `src/main/resources`. 
 


