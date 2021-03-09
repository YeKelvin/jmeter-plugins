# common-util
## 项目说明
- 平时写脚本时常用的方法

## 方法说明
### 1 `org.apache.jmeter.common.utils.Randoms`
#### 1.1 `getNumber(int length)`
获取随机数。

- `getNumber(int length)`
- `getNumber(String str, int length)`
- `getNumber(int length, String str)`
- `getNumber(int length1, String str, int length2)`
- `getNumber(String str1, int length, String str2)`

#### 1.2 `getCurrentTime()`
获取当前时间戳。

#### 1.3 `getTraceLogId()`
获取日志ID随机数。

#### 1.4 `getIDCard()`、`getHKIDCard()`、`getMacaoIDCard()`、`getTWIDCard()`
获取身份证ID随机数。

#### 1.5 `getIDCard15()`
获取15位身份证ID随机数。

#### 1.6 `getBankCard(String cardBin, int cardLength)`
根据卡bin和卡长度随机生成银行卡卡号，卡号无需减去卡bin长度。

#### 1.7 `getMobileNumber()`、`getCMCCMobileNumber()`、`getCUCCMobileNumber()`、`getTelecomMobileNumber()`
获取 移动/联通/电信 手机号码随机数。

...