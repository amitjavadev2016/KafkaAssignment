
# Kafka Age Partitioning Application

This project demonstrates a **data streaming application** using **Apache Beam**, **Spring Boot**, and **Kafka**. The application consumes messages from a Kafka topic, processes the data to calculate age, and partitions the messages into two Kafka topics (`EVEN_TOPIC` and `ODD_TOPIC`) based on whether the age is even or odd.

---

## **Features**

- Consume messages from `SOURCE_TOPIC`.
- Process messages to calculate age from the date of birth.
- Publish messages to:
  - `EVEN_TOPIC`: For even age.
  - `ODD_TOPIC`: For odd age.

---

## **Prerequisites**

- **Java 17**
- **Maven 3.x**
- **Docker** and **Docker Compose**

---

## **Setup**

### **1. Clone the Repository**

```bash
git clone <repository-url>
cd KafkaAssignment
```

### **2. Build the Application**

```bash
mvn clean package
```

This will create a `target/app.jar` file.

---

## **Running with Docker**

### **1. Start Kafka and Zookeeper**

Use the `docker-compose.yml` file to start Kafka and Zookeeper services:

```bash
docker-compose up -d
```

This will start:
- **Zookeeper** on port `2181`.
- **Kafka** on port `9092`.

### **2. Verify Kafka Setup**

Check if the required Kafka topics are created:

```bash
docker exec -it kafka kafka-topics.sh --list --bootstrap-server localhost:9092
```

Expected Output:
```
SOURCE_TOPIC
EVEN_TOPIC
ODD_TOPIC
```

If topics are missing, create them manually:
```bash
docker exec -it kafka kafka-topics.sh --create --topic SOURCE_TOPIC --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
docker exec -it kafka kafka-topics.sh --create --topic EVEN_TOPIC --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
docker exec -it kafka kafka-topics.sh --create --topic ODD_TOPIC --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
```

### **3. Run the Application**

Run the Spring Boot application in Docker:
```bash
docker-compose up --build
```

Alternatively, run the application locally:
```bash
java -jar target/app.jar
```

---

## **Testing Kafka**

### **1. Produce Messages to `SOURCE_TOPIC`**

Send test messages to the `SOURCE_TOPIC`:
```bash
docker exec -it kafka kafka-console-producer.sh --topic SOURCE_TOPIC --bootstrap-server localhost:9092
```

Enter messages in JSON format (one per line):
```json
{"name": "Alice", "address": "123 Elm St", "dateOfBirth": "2000-06-15"}
{"name": "Bob", "address": "456 Oak St", "dateOfBirth": "1995-03-12"}
```

Press `Ctrl+D` or `Ctrl+C` to exit.

---

### **2. Consume Messages from `EVEN_TOPIC`**

Read processed messages from `EVEN_TOPIC`:
```bash
docker exec -it kafka kafka-console-consumer.sh --topic EVEN_TOPIC --from-beginning --bootstrap-server localhost:9092
```

### **3. Consume Messages from `ODD_TOPIC`**

Read processed messages from `ODD_TOPIC`:
```bash
docker exec -it kafka kafka-console-consumer.sh --topic ODD_TOPIC --from-beginning --bootstrap-server localhost:9092
```

---

## **Project Structure**

```
KafkaAssignment
├── src/main/java
│   ├── in/assignment
│   │   ├── MainApplication.java       # Entry point for Spring Boot
│   │   ├── service
│   │   │   ├── ApacheBeamKafkaService # Kafka processing logic
│   │   ├── dto
│   │   │   ├── KafkaMessage.java      # Message data model
├── Dockerfile                        # Docker image configuration
├── docker-compose.yml                # Docker Compose setup for Kafka
├── README.md                         # Project documentation
├── pom.xml                           # Maven dependencies
```

---

## **Key Dependencies**

- **Spring Boot**: Application framework.
- **Apache Beam**: Data processing pipeline.
- **Kafka**: Message broker.
- **Docker**: Containerization.

---