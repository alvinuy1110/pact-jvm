#!/bin/bash

## Variables
PACT_BROKER_URL_PATHS="http://localhost:8500/pacts/latest"
DOT_FILE="latest.dot"
DOT_OUTPUT_TYPE="svg"
DOT_OUTPUT_FILE="test.svg"

##
echo "Extracting pacts..."
echo "digraph { ranksep=3; ratio=auto; overlap=false; node [  shape = plaintext, fontname = "Helvetica" ];" > $DOT_FILE
curl -v $PACT_BROKER_URL_PATHS | jq '.pacts[]._embedded | select(.consumer.name) | .consumer.name + "&quot;->&quot;" + .provider.name ' |  sed 's/&quot;/"/g' | sed 's/-/_/g' |  sed 's/_>/->/g' >> $DOT_FILE; echo "}" >> $DOT_FILE

echo "Converting $DOT_FILE to $DOT_OUTPUT_FILE"
dot $DOT_FILE -o$DOT_OUTPUT_FILE -T$DOT_OUTPUT_TYPE

