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

# マスターデータ登録

## Azure
Azure CosmosDBコンソール＞データエクスプローラー＞Stock選択＞NewShell
以下を実行する
```
db.Stock.insertMany([
  { "_id": 0,"ProductId":0,"Quantity":10000000,"ReservedQuantity":0},
  { "_id": 1,"ProductId":1,"Quantity":10000000,"ReservedQuantity":0},
  { "_id": 2,"ProductId":2,"Quantity":10000000,"ReservedQuantity":0},
  { "_id": 3,"ProductId":3,"Quantity":10000000,"ReservedQuantity":0},
  { "_id": 4,"ProductId":4,"Quantity":10000000,"ReservedQuantity":0},
  { "_id": 5,"ProductId":5,"Quantity":10000000,"ReservedQuantity":0},
  { "_id": 6,"ProductId":6,"Quantity":10000000,"ReservedQuantity":0},
  { "_id": 7,"ProductId":7,"Quantity":10000000,"ReservedQuantity":0},
  { "_id": 8,"ProductId":8,"Quantity":10000000,"ReservedQuantity":0},
  { "_id": 9,"ProductId":9,"Quantity":10000000,"ReservedQuantity":0},
  { "_id": 10,"ProductId":10,"Quantity":10000000,"ReservedQuantity":0},
  { "_id": 11,"ProductId":11,"Quantity":10000000,"ReservedQuantity":0},
  { "_id": 12,"ProductId":12,"Quantity":10000000,"ReservedQuantity":0},
  { "_id": 13,"ProductId":13,"Quantity":10000000,"ReservedQuantity":0},
  { "_id": 14,"ProductId":14,"Quantity":10000000,"ReservedQuantity":0},
  { "_id": 15,"ProductId":15,"Quantity":10000000,"ReservedQuantity":0}
  ])
```