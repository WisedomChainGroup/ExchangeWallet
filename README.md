# SDK-PHP方法说明

SDK-PHP主要是提供给实现普通转账事务的构造，签名，发送以及孵化器相关的操作，对于RPC来说，提供若干的接口，对于客户端来说，需要提供若干的实现方法，如下所示：

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

## 二、 JAVA-SDK文档

##### clone当前项目，导入jar运行。
1.0 生成keystore文件
```
* fromPassword(GET)
* 参数：
* 1）、password(String)
* 返回类型：json
* 返回值：
* {"message":"","data":[],"statusCode":int}
* data：keystore
```

1.1 地址校验
```
* verifyAddress(GET)
* 参数：
* 1）、address(String)
* 返回类型：json
* 返回值：
* {"message":"","data":[],"statusCode":int}
```
1.2 通过地址获得公钥哈希(调用此方法之前请先校验地址合法性！(调用verifyAddress方法))
```
* addressToPubkeyHash(GET)
* 参数：
* 1）、address(String)
* 返回类型：json
* 返回值：
* {"message":"","data":[],"statusCode":int}
* data：pubkeyHash（十六进制字符串）
```
1.3 通过公钥哈希获得地址
```
* pubkeyHashToAddress(GET)
* 参数：
* 1）、pubkeyHash(String)
* 返回类型：json
* 返回值：
* {"message":"","data":[],"statusCode":int}
* data：pubkeyHash(十六进制字符串)
```
1.4 通过keystore获得地址
```
* keystoreToAddress(POST)
* 参数：
* 1）、keystoreJson(String)
* 2）、password(String)
* 返回类型：json
* 返回值：
* {"message":"","data":[],"statusCode":int}
* data：address(String)
```
1.5 通过keystore获得公钥哈希
```
* keystoreToPubkeyHash()
* 参数：
* 1)、keystoreJson(String)
* 2)、password(String)
* 返回类型：json
* 返回值：
* {"message":"","data":[],"statusCode":int}
* data：pubkeyHash(十六进制字符串)
```
1.6 通过keystore获得私钥
```
* obtainPrikey(POST)
* 参数：
* 1)、keystoreJson(String)
* 2)、password(String)
* 返回类型：json
* 返回值：
* {"message":"","data":[],"statusCode":int}
* data：privateKey(十六进制字符串)
```
1.7 通过keystore获得公钥
```
* keystoreToPubkey(POST)
* 参数：
* 1)、keystoreJson(String)
* 2)、password(String)
* 返回类型：json
* 返回值：
* {"message":"","data":[],"statusCode":int}
* data：pubkey(十六进制字符串)
```
1.8 修改KeyStore密码方法
```
* modifyPassword(POST)
* 参数：
* 1)、keystoreJson(String)
* 2）、password(String)
* 3）、newPassword(String)
* 返回类型：json
* 返回值：
* {"message":"","data":[],"statusCode":int}
* data：keystore(json)
```
1.9 发起转账申请
```
* ClientToTransferAccount(POST)
* 参数：
* 1）、fromPubkey(十六进制字符串)
* 2）、toPubkeyHash(十六进制字符串)
* 3）、amount（BigDecimal)
* 4）、prikey（十六进制字符串)
* 5）、Nonce(Long)
* 返回类型：Json
* 返回值：
* {
* data :txHash(事务哈希，十六进制字符串)
* (int)statusCode:int
* (String)message:traninfo（已签名事务，十六进制字符串)
* }
```
* 注意，这里的成功或者失败，仅仅是指动作本身，真正看事务有没有最终成功，还需要通过事务哈希查询确认区块数

## 三、节点RPC接口

#### 连接节点，ip+端口+调用方法+参数

1.0 获取Nonce
```
*   方法：sendNonce     
*	参数：pubkeyhash(String)  
*	返回：
*	{"message":"","data":[],"statusCode":int}
*	data:Noce(Long)
```

1.1 获取余额
```
*   方法：sendBalance(POET)   
*	参数：address(String) 	
* 	返回:
* 	{"message":"","data":[],"statusCode":int}
*	data:balance(Long)
```

1.2 广播事务
```
*   方法： sendTransaction(POST)	
*	参数：traninfo(String)
*	返回：
* 	{"message":"","data":[],"statusCode":int}
```
        
1.3 查询当前区块高度
```
*   方法：height(GET)
*	返回：
*	{"message":"","data":0,"statusCode":int}
*	data:height(Long)
```
		
1.4 根据事务哈希获得所在区块哈希以及高度
```
*   方法：blockHash(GET)
*	参数：txHash(String)
*	返回：
*	{
*	data :定义如下;
*   statusCode(int):int
*	message(String):""
*    }
*    data:
*   {
*   "blockHash":区块哈希(十六进制字符串), 
*   "height":区块高度(Long)
*   }
```

1.5 根据事务哈希获得区块确认状态(GET)
```
*   方法：transactionConfirmed
*	参数：txHash(String)
*	返回： 
*   {"message":"","data":[],"statusCode":int}
*   statusCode: status(int)
```

1.6 根据区块高度获取事务列表
```
*   方法: getTransactionHeight(POST) 
*   参数: int height 区块高度
*   返回格式:{"message":"SUCCESS","data":[],"statusCode":1}
*   data格式:
*	String block_hash; 区块哈希16进制字符串
*	long height; 区块高度
*	int version; 版本号
*	String tx_hash; 事务哈希16进制字符串
*	int type;  事务类型
*	long nonce;nonce
*	String from;  发起者公钥16进制字符串
*	long gas_price; 事务手续费单价
*	long amount; 金额
*	String payload; payload数据
*	String signature; 签名16进制字符串
*	String to;  接受者公钥哈希16进制字符串
```

1.7 通过区块哈希获取事务列表
```
*   方法：getTransactionBlcok(POST)
*   参数 String blockhash 区块哈希16进制字符串
*   返回格式:{"message":"SUCCESS","data":[],"statusCode":1}
*   data格式:
*	String block_hash; 区块哈希16进制字符串
*	long height; 区块高度
*	int version; 版本号
*	String tx_hash; 事务哈希16进制字符串
*	int type;  事务类型
*	long nonce;nonce
*	String from;  发起者公钥16进制字符串
*	long gas_price; 事务手续费单价
*	long amount; 金额
*	String payload; payload数据
*	String signature; 签名16进制字符串
*	String to;  接受者公钥哈希16进制字符串
```

## 四、如何调用

引用curl扩展库，配置：extension=curl;

1.0 GET 请求
```
function do_get($url, $params) {
    $url = "{$url}?" . http_build_query( $params );
    //初始化
    $curl = curl_init();
    //设置抓取的url
    curl_setopt($curl, CURLOPT_URL, $url);
    //设置头文件的信息作为数据流输出
    curl_setopt($curl, CURLOPT_HEADER, 1);
    //设置获取的信息以文件流的形式返回，而不是直接输出。
    curl_setopt($curl, CURLOPT_RETURNTRANSFER, 1);
    //执行命令
    $result = curl_exec($curl);
    //关闭URL请求
    curl_close($curl);
    return $result;
}
```

例子：
```
$address = "1CRXnUJx9Tq4ZpNkkueeKFxCbYg1E4uTCt";
$url="http://localhost:8080/verifyAddress";
$params=array('address'=>$address);
$result=do_get($url,$params); 
```

2.0 POST 请求
```
function do_post($url,$params){
    $curl = curl_init();
    //设置抓取的url
    curl_setopt($curl, CURLOPT_URL, $url);
    //设置头文件的信息作为数据流输出
    curl_setopt($curl, CURLOPT_HEADER, 1);
    //设置获取的信息以文件流的形式返回，而不是直接输出。
    curl_setopt($curl, CURLOPT_RETURNTRANSFER, 1);
    //设置post方式提交
    curl_setopt($curl, CURLOPT_POST, 1);
    //设置post数据
    $post_data = $params;
    curl_setopt($curl, CURLOPT_POSTFIELDS, $post_data);
    //执行命令
    $data = curl_exec($curl);
    //关闭URL请求
    curl_close($curl);
    //显示获得的数据
    return $result;
}
```
例子：
```
   $url="http://localhost:8080/keystoreToPubkey";
   $params= array(
        'keystoreJson' => '{"address":"1NpJrJ22GueGg9yWbff9cCZ6hUmwrHqU8S","kdfparams":{"salt":"dfc293cce368d840d74238b6510a7d9aa49949d89fdbd73e8363ba2e3f1c5616","memoryCost":20480,"parallelism":2,"timeCost":4},"id":"8a011ac8-fc9e-49a2-a4b4-85adec08d3ee","kdf":"argon2id","version":"1","mac":"1476bcfe7560faf8adbf0cbd5204d75c62470ecec616e47ab4ba2dd92341f447","crypto":{"cipher":"aes-256-ctr","ciphertext":"f8354fdab0b4c4c354f5dc9b502900e6b79cda251ac23c1d0751aaa26421904b","cipherparams":{"iv":"1066dd6d6f29eaa99748c9e126f4bbe8"}}}',
       'password' => "12345678"
);
   $result = do_post($url,$params);
   ```
	



