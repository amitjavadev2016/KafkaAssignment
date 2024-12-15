#!/bin/bash

# Wait for Kafka to be ready
sleep 10

# Create topics
kafka-topics.sh --create --topic SOURCE_TOPIC --bootstrap-server kafka:29092 --partitions 1 --replication-factor 1
kafka-topics.sh --create --topic EVEN_TOPIC --bootstrap-server kafka:29092 --partitions 1 --replication-factor 1
kafka-topics.sh --create --topic ODD_TOPIC --bootstrap-server kafka:29092 --partitions 1 --replication-factor 1
