# 動作確認用テストデータ

## データ作成
- curl

```
curl -H "Accept: application/json" -H "Content-type: application/json" -X POST -d "{ \"productId\": \"1\",\"quantity\": \"1000\",\"reservedQuantity\": \"0\"}" http://localhost:8080/stock
```

- grpcurl

```
grpcurl --plaintext -d "{ \"stock\": { \"productId\": \"1\",\"quantity\": \"100\",\"reservedQuantity\": \"0\"}}" localhost:9002 stock.StockService/Create
```


## データ更新

- curl

```
curl -H "Accept: application/json" -H "Content-type: application/json" -X PUT -d "{ \"id\": \"1\", \"productId\": \"10\",\"quantity\": \"10000\",\"reservedQuantity\": \"10\"}" http://localhost:8080/stock
```

- grpcurl

```
grpcurl --plaintext -d "{ \"stock\": { \"id\": \"1\", \"productId\": \"1\",\"quantity\": \"100\",\"reservedQuantity\": \"0\"}}" localhost:9002 stock.StockService/Update
```


## 検索

- curl

```
curl -H "Accept: application/json" -X GET http://localhost:8080/stock/
```

- grpcurl

```
grpcurl --plaintext localhost:9002 stock.StockService/FindAll
```

## ID指定データ取得

- curl

```
curl -H "Accept: application/json" -X GET http://localhost:8080/stock/1
```

- grpcurl

```
grpcurl --plaintext -d "{\"stockId\": \"1\"}" localhost:9002 stock.StockService/GetByStockId
```

## データ削除

- curl

```
curl -H "Accept: application/json" -X DELETE http://localhost:8080/stock/1
```

- grpcurl

```
grpcurl --plaintext -d "{\"stockId\": \"1\"}" localhost:9002 stock.StockService/DeleteByStockId
```


## Dynamodb（localstack)のデータ確認

- 事前にプロファイルを作成する「/.aws/config」

```
[profile localstack]
region = ap-northeast-1
output = json
```

- 事前にプロファイルを作成する「/.aws/credentials」

```
[localstack]
aws_access_key_id = test-key
aws_secret_access_key = test-secret
```

- Orderテーブルの取得

```
aws dynamodb scan --table-name=StockAllocateHistory --profile localstack --endpoint-url=http://localhost:4566
aws dynamodb scan --table-name=Stock --profile localstack --endpoint-url=http://localhost:4566
```

## Cosmondb(Monogodb)のデータ確認

- http://localhost:8081/ にアクセス
