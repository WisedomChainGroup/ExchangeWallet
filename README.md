# 方法说明

wallet_jar主要是提供普通转账事务的构造，签名，发送。

## 一、 基本说明

1）、区块确认完成

通过事务的哈希值查询确认区块数，并且确认是否已经完成，
我们认为往后确定2区块即可表示已经完成。
无论什么事务，都要等待至少2个区块确认才算完成

2）、返回格式
```
##### {"message":"","data":[],"statusCode":int}
* message：描述
* data   ：数据
* statusCode：      
   
{
    2000 正确
    2100 已确认
    2200 未确认
    5000 错误
    6000 格式错误
    7000 校验错误
    8000 异常
}
```

## 二、 wallet_jar文档

##### 下载Release里最新的wallet_jar，运行server-1.0.0.jar :
##### java -jar server-1.0.0.jar --server.port="your port" --nodeNet="node ip:port";

##### 例如：java -jar server-1.0.0.jar --server.port=8080 --nodeNet=00.0.0.000:0000;
##### 所有的调用均为普通rpc
##### Content-Type: application/json;charset=UTF-8

#### 1.0 生成keystore文件
```
 fromPassword(GET)
 参数：
 1）、password(String)
 返回类型：json
 返回值：
 {"message":"","data":[],"statusCode":int}
 data：keystore
```

#### 1.1 地址校验
```
 verifyAddress(GET)
 参数：
 1）、address(String)
 返回类型：json
 返回值：
 {"message":"","data":[],"statusCode":int}
```
#### 1.2 通过地址获得公钥哈希(调用此方法之前请先校验地址合法性！(调用verifyAddress方法))
```
 addressToPubkeyHash(GET)
 参数：
 1）、address(String)
 返回类型：json
 返回值：
 {"message":"","data":[],"statusCode":int}
 data：pubkeyHash（十六进制字符串）
```
#### 1.3 通过公钥哈希获得地址
```
 pubkeyHashToAddress(GET)
 参数：
 1）、pubkeyHash(String)
 2) 、type(int)
 	type: 1 不加前缀的老格式地址
	      2  加上“WX”前缀的新格式地址
 返回类型：json
 返回值：
 {"message":"","data":[],"statusCode":int}
 data：pubkeyHash(十六进制字符串)
```
#### 1.4 通过keystore获得地址
```
 keystoreToAddress(POST)
 参数：
 1）、keystoreJson(String)
 2）、password(String)
 返回类型：json
 返回值：
 {"message":"","data":[],"statusCode":int}
 data：address(String)
```
#### 1.5 通过keystore获得公钥哈希
```
 keystoreToPubkeyHash()
 参数：
 1)、keystoreJson(String)
 2)、password(String)
 返回类型：json
 返回值：
 {"message":"","data":[],"statusCode":int}
 data：pubkeyHash(十六进制字符串)
```
#### 1.6 通过keystore获得私钥
```
 obtainPrikey(POST)
 参数：
 1)、keystoreJson(String)
 2)、password(String)
 返回类型：json
 返回值：
 {"message":"","data":[],"statusCode":int}
 data：privateKey(十六进制字符串)
```
#### 1.7 通过keystore获得公钥
```
 keystoreToPubkey(POST)
 参数：
 1)、keystoreJson(String)
 2)、password(String)
 返回类型：json
 返回值：
 {"message":"","data":[],"statusCode":int}
 data：pubkey(十六进制字符串)
```
#### 1.8 修改KeyStore密码方法
```
 modifyPassword(POST)
 参数：
 1)、keystoreJson(String)
 2）、password(String)
 3）、newPassword(String)
 返回类型：json
 返回值：
 {"message":"","data":[],"statusCode":int}
 data：keystore(json)
```
#### 1.9 发起WDC转账申请
```
 ClientToTransferAccount(POST)
 参数：
 1）、fromPubkey(十六进制字符串)
 2）、toPubkeyHash(十六进制字符串)
 3）、amount（BigDecimal)
 4）、prikey（十六进制字符串)
 返回类型：Json
 返回值：
 {
 data :txHash(事务哈希，十六进制字符串)
 (int)statusCode:int
 (String)message:traninfo（已签名事务，十六进制字符串)
 }
```
* 注意，这里的成功或者失败，仅仅是指动作本身，真正看事务有没有最终成功，还需要通过事务哈希查询确认区块数


#### 2.0 发起其他资产转账申请
```
 CreateSignToDeployforRuleTransfer(POST)
 参数：
 1）、fromPubkey(十六进制字符串)
 2）、txHash(十六进制字符串，部署代币的事务哈希)
 3）、prikey（十六进制字符串)
 4）、payload_from（十六进制字符串，资产的发送者)
 5）、payload_to（十六进制字符串，资产的发送者）
 6）、value（BigDecimal，转账金额）
 返回类型：Json
 返回值：
 {
 data :txHash(事务哈希，十六进制字符串)
 (int)statusCode:int
 (String)message:traninfo（已签名事务，十六进制字符串)
 }
```
* 注意，这里的成功或者失败，仅仅是指动作本身，真正看事务有没有最终成功，还需要通过事务哈希查询确认区块数



