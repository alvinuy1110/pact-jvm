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
* see [PactBroker.sql](./sql/PactBroker.sql)

## Docker Images

* Use pactfoundation/pact-broker for managed environment like kubernetes and such
* Use dius/pact-broker for standalone

Refer to https://github.com/pact-foundation/pact-broker-docker


## Visualization of Pacts

### Dependencies

```
sudo apt install docker-compose
sudo apt install jq
sudo apt install graphviz
```

### Script

See [extractGraphViz.sh](./scripts/extractGraphViz.sh)

Need more customizations, see https://graphviz.org/doc/info/lang.html
