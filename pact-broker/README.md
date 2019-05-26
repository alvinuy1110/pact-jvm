Pact Broker
===========

Local Broker
------------

This should be used for local testing.  For sharing with other teams, the database/ server must be setup separately instead.

* Start the broker

```
docker-compose up -d
```

* Go to browser

```
http:\\localhost:8500
```

* Stop the broker

```
docker-compose down
```


## SQL

* To browse the database, install a client such as Squirrel
* see [PactBroker.sql](.\pact-broker\sql\PactBroker.sql)



