# devlos-kafka-cluster 

## 1. 프로젝트 소개

### 1.1. Overview

Docker copmose 기반 동작하는 Kafka 인프라를 구축한다.
* Zookeeper cluster (3 nodes) 
* Kafka cluster (3 nodes)
* JMX-kafka (3 nodes)
* CMAC
* Kowl
* prometheus 
* grafana

### 1.2. 설정 방법

* .env 파일의 로그인 정보 수정
    
    ```yml
    # .env
    ID=devlos
    PW=devlos0322@
    ```

* 카프카 Advertised listners 주소 설정
    
    ```yml
    # .env
    SERVER_URI=localhost
    ```



### 1.3. 실행 방법

docker-compose 파일 실행

```shell
docker-compose up -d
```

### 1.4. 관리툴 접근

* CMAC (http://host-ip:9100/)
* Kowl (http://host-ip:9200/)

### 1.5. 모니터링 툴 접근

* Prometheus (http://host-ip:9090/)
* Grafana (http://host-ip:3000/)

### 1.6. Log 관리

* 카프카 로그, 주키퍼 로그, 프로메테우스 matric 정보는 호스트에 저장된다.
* 로그 : pwd /logs
* 데이터 : pwd /data

---

## 2. 컨테이너 설명

### 2.1. Overview 

주키퍼 클러스터 3대

### 2.2. Zookeeper cluster (3 nodes) 

주키퍼 클러스터 3대

### 2.3. Kafka cluster (3 nodes)

카프카 클러스터 3대

### 2.4. JMX-kafka (3 nodes)

카프카의 JMX를 이용하여 Prometheus로 matric 정보를 전달하는 역할을 담당한다. 각 노드 별로 연결되어 있기 때문에 브로커 수와 동일하게 맞춰줘야한다. 

(Kafka cluster node count = JMX-kafka node count)

### 2.5. CMAC

카프카 management tool 이다. 클러스터 및 토픽 정보를 알 수 있다. docker-compose를 이용하여 초기 실행하게 되면 클러스터 정보를 등록해야한다.

#### 2.5.1. 환경설정

1. Add Cluster 클릭

2. Cluster Name : example-name

3. Cluster Zookeeper Hosts : zookeeper-1:2181,zookeeper-2:2182,zookeeper-3:2183

4. Enable JMX Polling (Set JMX_PORT env variable before starting kafka server) 체크

5. Enable Logkafka 체크

6. 나머지 조건은 필요에 따라 설정

#### 2.5.2. 토픽 생성

운영환경의 카프카 토픽은 관리 차원에서 자동 생성기능을 끄고 시작한다. 그러므로 토픽을 테스트하거나 새로운 토픽을 추가하려면 다음과 같이 추가한다.

1. Topic 탭 클릭

2. Create 클릭

3. Topic : example-topic (참고: https://flashy-cephalopod-9ec.notion.site/Kafka-856a62c169004c24afb6b9f063412f4a)

4. Partitions: 필요에 맞게 설정 (테스트시 1, 자원 사용량을 확인하며 점진적으로 늘리는 것을 추천)

5. Replication Factor: 3 (브로커 갯수 이하로 설정. 본 docker-compose 환경에서 최댓값은 3개)

6. 기타 설정은 상황에 맞게 설정

7. Create 클릭

### 2.6. Kowl

카프카 management tool 이다. 브로커가 수신한 Topic 정보를 확인할 수 있다.

### 2.7 Prometheus

JMX-kafka exporter를 이용해 카프카 브로커들의 matric 정보를 수집한다. 

### 2.8 grafana

호스트 및 브로커 Health check 및 리소스 사용량을 모니터링한다.

확인할 수 있는 정보는 다음과 같다.

* Kafka Cluster / Replication
* Kafka / Hard Disk Usage
* Kafka Consumers / Fetch Rate & Records Lag
* Kafka Broker / Zookeeper Connection
* Kafka Broker / Performance & Latency
* Kafka / Brokers JVM & OS
* Kafka Topics / Logs
* Kafka Cluster / Global HealthCheck

Grafana 데이터 소스 등록시 서버의 외부 IP를 이용한다. 클라이언트에서 API를 다이렉트로 연결하는 방식이기 때문에, Localhost로 설정해 놓으면 데이터를 조회할 수 없다.