#!/bin/bash

## Example of script to invoke can-i-deploy

## Variables"
PACT_CONSUMER="user_provider"

PACT_BROKER_URL="http://localhost:8500"
PACT_VERSION="0.0.1-SNAPSHOT"

PACT_CLI_HOME="../../cli/pact/bin"

##
echo "Executing can-i-deploy..."
echo "Consumer: $PACT_CONSUMER, version: $PACT_VERSION, broker: $PACT_BROKER_URL"
#echo "Consumer: $PACT_CONSUMER_PACT_CONSUMER_MESSAGING, version: $PACT_VERSION, broker: $PACT_BROKER_URL"
#$PACT_CLI_HOME/pact-broker can-i-deploy -a=$PACT_CONSUMER -e=$PACT_VERSION -a=$PACT_CONSUMER --latest -b=$PACT_BROKER_URL -o=table

$PACT_CLI_HOME/pact-broker can-i-deploy -a=$PACT_CONSUMER -e=$PACT_VERSION -b=$PACT_BROKER_URL -o=table


#echo "Converting $DOT_FILE to $DOT_OUTPUT_FILE"
#dot $DOT_FILE -o$DOT_OUTPUT_FILE -T$DOT_OUTPUT_TYPE

