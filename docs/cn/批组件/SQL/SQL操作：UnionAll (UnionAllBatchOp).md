# SQL操作：UnionAll (UnionAllBatchOp)
Java 类名：com.alibaba.alink.operator.batch.sql.UnionAllBatchOp

Python 类名：UnionAllBatchOp


## 功能介绍
提供sql的union all语句功能


## 参数说明
| 名称 | 中文名称 | 描述 | 类型 | 是否必须？ | 默认值 |
| --- | --- | --- | --- | --- | --- |


## 代码示例
### Python 代码
```python
from pyalink.alink import *

import pandas as pd

useLocalEnv(1)

df = pd.DataFrame([
    ['Ohio', 2000, 1.5],
    ['Ohio', 2001, 1.7],
    ['Ohio', 2002, 3.6],
    ['Nevada', 2001, 2.4],
    ['Nevada', 2002, 2.9],
    ['Nevada', 2003, 3.2]
])

schema = 'f1 string, f2 bigint, f3 double'
data1 = BatchOperator.fromDataframe(df, schemaStr=schema)
data2 = BatchOperator.fromDataframe(df, schemaStr=schema)

UnionAllBatchOp()\
    .linkFrom(data1, data2)\
    .print()
```
### Java 代码
```java
import org.apache.flink.types.Row;

import com.alibaba.alink.operator.batch.BatchOperator;
import com.alibaba.alink.operator.batch.source.MemSourceBatchOp;
import com.alibaba.alink.operator.batch.sql.UnionAllBatchOp;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class UnionAllBatchOpTest {
	@Test
	public void testUnionAllBatchOp() throws Exception {
		List <Row> df = Arrays.asList(
			Row.of("Ohio", 2000, 1.5),
			Row.of("Ohio", 2001, 1.7),
			Row.of("Ohio", 2002, 3.6),
			Row.of("Nevada", 2001, 2.4),
			Row.of("Nevada", 2002, 2.9),
			Row.of("Nevada", 2003, 3.2)
		);
		BatchOperator <?> data1 = new MemSourceBatchOp(df, "f1 string, f2 int, f3 double");
		BatchOperator <?> data2 = new MemSourceBatchOp(df, "f1 string, f2 int, f3 double");
		new UnionAllBatchOp()
			.linkFrom(data1, data2)
			.print();
	}
}
```

### 运行结果
f1|f2|f3
---|---|---
Ohio|2000|1.5000
Ohio|2000|1.5000
Ohio|2001|1.7000
Ohio|2001|1.7000
Ohio|2002|3.6000
Ohio|2002|3.6000
Nevada|2001|2.4000
Nevada|2001|2.4000
Nevada|2002|2.9000
Nevada|2002|2.9000
Nevada|2003|3.2000
Nevada|2003|3.2000
