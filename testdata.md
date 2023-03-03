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

## AWS
aws cliで以下を実行する
```bash
aws dynamodb put-item --table-name Stock --item '{ "Id": { "N": "0" }, "ProductId": { "N": "0" }, "Quantity": { "N": "10000000" }, "ReservedQuantity": { "N": "0" }}'
aws dynamodb put-item --table-name Stock --item '{ "Id": { "N": "1" }, "ProductId": { "N": "1" }, "Quantity": { "N": "10000000" }, "ReservedQuantity": { "N": "0" }}'
aws dynamodb put-item --table-name Stock --item '{ "Id": { "N": "2" }, "ProductId": { "N": "2" }, "Quantity": { "N": "10000000" }, "ReservedQuantity": { "N": "0" }}'
aws dynamodb put-item --table-name Stock --item '{ "Id": { "N": "3" }, "ProductId": { "N": "3" }, "Quantity": { "N": "10000000" }, "ReservedQuantity": { "N": "0" }}'
aws dynamodb put-item --table-name Stock --item '{ "Id": { "N": "4" }, "ProductId": { "N": "4" }, "Quantity": { "N": "10000000" }, "ReservedQuantity": { "N": "0" }}'
aws dynamodb put-item --table-name Stock --item '{ "Id": { "N": "5" }, "ProductId": { "N": "5" }, "Quantity": { "N": "10000000" }, "ReservedQuantity": { "N": "0" }}'
aws dynamodb put-item --table-name Stock --item '{ "Id": { "N": "6" }, "ProductId": { "N": "6" }, "Quantity": { "N": "10000000" }, "ReservedQuantity": { "N": "0" }}'
aws dynamodb put-item --table-name Stock --item '{ "Id": { "N": "7" }, "ProductId": { "N": "7" }, "Quantity": { "N": "10000000" }, "ReservedQuantity": { "N": "0" }}'
aws dynamodb put-item --table-name Stock --item '{ "Id": { "N": "8" }, "ProductId": { "N": "8" }, "Quantity": { "N": "10000000" }, "ReservedQuantity": { "N": "0" }}'
aws dynamodb put-item --table-name Stock --item '{ "Id": { "N": "9" }, "ProductId": { "N": "9" }, "Quantity": { "N": "10000000" }, "ReservedQuantity": { "N": "0" }}'
aws dynamodb put-item --table-name Stock --item '{ "Id": { "N": "10" }, "ProductId": { "N": "10" }, "Quantity": { "N": "10000000" }, "ReservedQuantity": { "N": "0" }}'
aws dynamodb put-item --table-name Stock --item '{ "Id": { "N": "11" }, "ProductId": { "N": "11" }, "Quantity": { "N": "10000000" }, "ReservedQuantity": { "N": "0" }}'
aws dynamodb put-item --table-name Stock --item '{ "Id": { "N": "12" }, "ProductId": { "N": "12" }, "Quantity": { "N": "10000000" }, "ReservedQuantity": { "N": "0" }}'
aws dynamodb put-item --table-name Stock --item '{ "Id": { "N": "13" }, "ProductId": { "N": "13" }, "Quantity": { "N": "10000000" }, "ReservedQuantity": { "N": "0" }}'
aws dynamodb put-item --table-name Stock --item '{ "Id": { "N": "14" }, "ProductId": { "N": "14" }, "Quantity": { "N": "10000000" }, "ReservedQuantity": { "N": "0" }}'
```

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
  { "_id": 14,"ProductId":14,"Quantity":10000000,"ReservedQuantity":0}
  ])
```