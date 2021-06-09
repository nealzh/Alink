package com.alibaba.alink.params.feature.featuregenerator;

import org.apache.flink.ml.api.misc.param.ParamInfo;
import org.apache.flink.ml.api.misc.param.ParamInfoFactory;

import com.alibaba.alink.params.ParamUtil;
import com.alibaba.alink.params.dataproc.HasClause;

public interface BaseWindowParams <T> extends
	HasTimeCol <T>,
	HasClause <T> {

	//The metric is second, and the minimum granularity is millisecond.
	//Time less than 1 millisecond will be rounded.
	ParamInfo <Double> LATENCY = ParamInfoFactory
		.createParamInfo("latency", Double.class)
		.setDescription("latency for watermark")
		.setHasDefaultValue(0.0)
		.build();

	default Double getLatency() {
		return get(LATENCY);
	}

	default T setLatency(Double value) {
		return set(LATENCY, value);
	}

	default T setLatency(Integer value) {
		return set(LATENCY, value.doubleValue());
	}


	ParamInfo <WatermarkType> WATERMARK_TYPE = ParamInfoFactory
		.createParamInfo("watermarkType", WatermarkType.class)
		.setDescription("watermark type : Period or Punctuated.")
		.setHasDefaultValue(WatermarkType.PERIOD)
		.build();

	default WatermarkType getWatermarkType() {
		return get(WATERMARK_TYPE);
	}

	default T setWatermarkType(WatermarkType value) {
		return set(WATERMARK_TYPE, value);
	}

	default T setWatermarkType(String value) {
		return set(WATERMARK_TYPE, ParamUtil.searchEnum(WATERMARK_TYPE, value));
	}

	enum WatermarkType {
		/**
		 * Period type.
		 */
		PERIOD,

		/**
		 * Punctuated type.
		 */
		PUNCTUATED
	}
}
