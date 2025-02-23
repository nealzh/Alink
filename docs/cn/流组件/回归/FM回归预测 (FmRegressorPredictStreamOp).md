# FM回归预测 (FmRegressorPredictStreamOp)
Java 类名：com.alibaba.alink.operator.stream.regression.FmRegressorPredictStreamOp

Python 类名：FmRegressorPredictStreamOp


## 功能介绍

* 使用 Fm 回归模型对流数据进行预测
* 算法支持稀疏、稠密两种数据格式

## 参数说明


| 名称 | 中文名称 | 描述 | 类型 | 是否必须？ | 默认值 |
| --- | --- | --- | --- | --- | --- |
| predictionCol | 预测结果列名 | 预测结果列名 | String | ✓ |  |
| predictionDetailCol | 预测详细信息列名 | 预测详细信息列名 | String |  |  |
| reservedCols | 算法保留列名 | 算法保留列 | String[] |  | null |
| vectorCol | 向量列名 | 向量列对应的列名，默认值是null | String |  | null |
| numThreads | 组件多线程线程个数 | 组件多线程线程个数 | Integer |  | 1 |
| modelStreamFilePath | 模型流的文件路径 | 模型流的文件路径 | String |  | null |
| modelStreamScanInterval | 扫描模型路径的时间间隔 | 描模型路径的时间间隔，单位秒 | Integer |  | 10 |
| modelStreamStartTime | 模型流的起始时间 | 模型流的起始时间。默认从当前时刻开始读。使用yyyy-mm-dd hh:mm:ss.fffffffff格式，详见Timestamp.valueOf(String s) | String |  | null |

## 代码示例
### Python 代码
```python
from pyalink.alink import *

import pandas as pd

useLocalEnv(1)

df = pd.DataFrame([
    ["1:1.1 3:2.0", 1.0],
    ["2:2.1 10:3.1", 1.0],
    ["1:1.2 5:3.2", 0.0],
    ["3:1.2 7:4.2", 0.0]])

input = BatchOperator.fromDataframe(df, schemaStr='kv string, label double')
test = StreamOperator.fromDataframe(df, schemaStr='kv string, label double')

fm = FmRegressorTrainBatchOp()\
        .setVectorCol("kv")\
        .setLabelCol("label")

model = input.link(fm)

predictor = FmRegressorPredictStreamOp(model)\
        .setPredictionCol("pred")

predictor.linkFrom(test).print()

StreamOperator.execute()
```
### Java 代码
```java
import org.apache.flink.types.Row;

import com.alibaba.alink.operator.batch.BatchOperator;
import com.alibaba.alink.operator.batch.regression.FmRegressorTrainBatchOp;
import com.alibaba.alink.operator.batch.source.MemSourceBatchOp;
import com.alibaba.alink.operator.stream.StreamOperator;
import com.alibaba.alink.operator.stream.regression.FmRegressorPredictStreamOp;
import com.alibaba.alink.operator.stream.source.MemSourceStreamOp;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class FmRegressorPredictStreamOpTest {
	@Test
	public void testFmRegressorPredictStreamOp() throws Exception {
		List <Row> df = Arrays.asList(
			Row.of("1:1.1 3:2.0", 1.0),
			Row.of("2:2.1 10:3.1", 1.0),
			Row.of("1:1.2 5:3.2", 0.0)
		);
		BatchOperator <?> input = new MemSourceBatchOp(df, "kv string, label double");
		StreamOperator <?> test = new MemSourceStreamOp(df, "kv string, label double");
		BatchOperator <?> fm = new FmRegressorTrainBatchOp()
			.setVectorCol("kv")
			.setLabelCol("label");
		BatchOperator <?> model = input.link(fm);
		StreamOperator <?> predictor = new FmRegressorPredictStreamOp(model)
			.setPredictionCol("pred");
		predictor.linkFrom(test).print();
		StreamOperator.execute();
	}
}
```
### 运行结果
kv|label|pred
---|-----|----
1:1.1 3:2.0|1.0000|0.3900
1:1.2 5:3.2|0.0000|-0.0030
2:2.1 10:3.1|1.0000|0.7205





