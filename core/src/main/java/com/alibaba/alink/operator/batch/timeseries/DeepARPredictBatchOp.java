package com.alibaba.alink.operator.batch.timeseries;

import org.apache.flink.ml.api.misc.param.Params;

import com.alibaba.alink.operator.batch.utils.ModelMapBatchOp;
import com.alibaba.alink.operator.common.timeseries.DeepARModelMapper;
import com.alibaba.alink.params.timeseries.DeepARPredictParams;

public class DeepARPredictBatchOp extends ModelMapBatchOp <DeepARPredictBatchOp>
	implements DeepARPredictParams <DeepARPredictBatchOp> {

	public DeepARPredictBatchOp() {
		this(new Params());
	}

	public DeepARPredictBatchOp(Params params) {
		super(DeepARModelMapper::new, params);
	}
}
