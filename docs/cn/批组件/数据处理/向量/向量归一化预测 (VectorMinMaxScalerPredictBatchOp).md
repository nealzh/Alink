# 向量归一化预测 (VectorMinMaxScalerPredictBatchOp)
Java 类名：com.alibaba.alink.operator.batch.dataproc.vector.VectorMinMaxScalerPredictBatchOp

Python 类名：VectorMinMaxScalerPredictBatchOp


## 功能介绍

vector归一化是对vector数据进行归一化处理的组件, 将数据归一到minValue和maxValue之间，value最终结果为 (value - min) / (max - min) * (maxValue - minValue) + minValue，最终结果的范围为[minValue, maxValue]。

minValue和maxValue由用户指定，默认为0和1。

该组件为预测组件，加载模型后就可以处理数据

## 参数说明

| 名称 | 中文名称 | 描述 | 类型 | 是否必须？ | 默认值 |
| --- | --- | --- | --- | --- | --- |
| outputCol | 输出结果列 | 输出结果列列名，可选，默认null | String |  | null |
| numThreads | 组件多线程线程个数 | 组件多线程线程个数 | Integer |  | 1 |



## 代码示例
### Python 代码
```python
from pyalink.alink import *

import pandas as pd

useLocalEnv(1)

df = pd.DataFrame([
    ["a", "10.0, 100"],
    ["b", "-2.5, 9"],
    ["c", "100.2, 1"],
    ["d", "-99.9, 100"],
    ["a", "1.4, 1"],
    ["b", "-2.2, 9"],
    ["c", "100.9, 1"]
])

data = BatchOperator.fromDataframe(df, schemaStr="col string, vec string")

trainOp = VectorMinMaxScalerTrainBatchOp()\
           .setSelectedCol("vec")
model = trainOp.linkFrom(data) 

batchPredictOp = VectorMinMaxScalerPredictBatchOp()
batchPredictOp.linkFrom(model, data).collectToDataframe()
```
### Java 代码
```java
import org.apache.flink.types.Row;

import com.alibaba.alink.operator.batch.BatchOperator;
import com.alibaba.alink.operator.batch.dataproc.vector.VectorMinMaxScalerPredictBatchOp;
import com.alibaba.alink.operator.batch.dataproc.vector.VectorMinMaxScalerTrainBatchOp;
import com.alibaba.alink.operator.batch.source.MemSourceBatchOp;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class VectorMinMaxScalerPredictBatchOpTest {
	@Test
	public void testVectorMinMaxScalerPredictBatchOp() throws Exception {
		List <Row> df = Arrays.asList(
			Row.of("a", "10.0, 100"),
			Row.of("b", "-2.5, 9"),
			Row.of("c", "100.2, 1"),
			Row.of("d", "-99.9, 100"),
			Row.of("a", "1.4, 1"),
			Row.of("b", "-2.2, 9"),
			Row.of("c", "100.9, 1")
		);
		BatchOperator <?> data = new MemSourceBatchOp(df, "col string, vec string");
		BatchOperator <?> trainOp = new VectorMinMaxScalerTrainBatchOp()
			.setSelectedCol("vec");
		BatchOperator <?> model = trainOp.linkFrom(data);
		BatchOperator <?> batchPredictOp = new VectorMinMaxScalerPredictBatchOp();
		batchPredictOp.linkFrom(model, data).print();
	}
}
```
### 运行结果

col1|vec
----|---
a|0.5473107569721115,1.0
b|0.4850597609561753,0.08080808080808081
c|0.9965139442231076,0.0
d|0.0,1.0
a|0.5044820717131474,0.0
b|0.4865537848605578,0.08080808080808081
c|1.0,0.0


