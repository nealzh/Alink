package com.alibaba.alink.common.linalg;

import com.alibaba.alink.common.linalg.VectorUtil.VectorSerialType;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Random;

/**
 * A dense vector represented by a values array.
 */
public class DenseVector extends Vector {
	private static final long serialVersionUID = -5932985883649890536L;
	/**
	 * The array holding the vector data.
	 * <p>
	 * Package private to allow access from {@link MatVecOp} and {@link BLAS}.
	 */
	double[] data;

	/**
	 * Create a zero size vector.
	 */
	public DenseVector() {
		this(0);
	}

	/**
	 * Create a size n vector with all elements zero.
	 *
	 * @param n Size of the vector.
	 */
	public DenseVector(int n) {
		this.data = new double[n];
	}

	/**
	 * Create a dense vector with the user provided data.
	 *
	 * @param data The vector data.
	 */
	public DenseVector(double[] data) {
		this.data = data;
	}

	/**
	 * Get the data array.
	 */
	public double[] getData() {
		return this.data;
	}

	/**
	 * Set the data array.
	 */
	public void setData(double[] data) {
		this.data = data;
	}

	/**
	 * Create a dense vector with all elements one.
	 *
	 * @param n Size of the vector.
	 * @return The newly created dense vector.
	 */
	public static DenseVector ones(int n) {
		DenseVector r = new DenseVector(n);
		Arrays.fill(r.data, 1.0);
		return r;
	}

	/**
	 * Create a dense vector with all elements zero.
	 *
	 * @param n Size of the vector.
	 * @return The newly created dense vector.
	 */
	public static DenseVector zeros(int n) {
		DenseVector r = new DenseVector(n);
		Arrays.fill(r.data, 0.0);
		return r;
	}

	/**
	 * Create a dense vector with random values uniformly distributed in the range of [0.0, 1.0].
	 *
	 * @param n Size of the vector.
	 * @return The newly created dense vector.
	 */
	public static DenseVector rand(int n) {
		Random random = new Random();
		DenseVector v = new DenseVector(n);
		for (int i = 0; i < n; i++) {
			v.data[i] = random.nextDouble();
		}
		return v;
	}

	@Override
	public DenseVector clone() {
		return new DenseVector(this.data.clone());
	}

	@Override
	public String toString() {
		return VectorUtil.toString(this);
	}

	@Override
	public int size() {
		return data.length;
	}

	@Override
	public double get(int i) {
		return data[i];
	}

	@Override
	public void set(int i, double d) {
		data[i] = d;
	}

	@Override
	public void add(int i, double d) {
		data[i] += d;
	}

	@Override
	public double normL1() {
		double d = 0;
		for (double t : data) {
			d += Math.abs(t);
		}
		return d;
	}

	@Override
	public double normL2() {
		double d = 0;
		for (double t : data) {
			d += t * t;
		}
		return Math.sqrt(d);
	}

	@Override
	public double normL2Square() {
		double d = 0;
		for (double t : data) {
			d += t * t;
		}
		return d;
	}

	@Override
	public double normInf() {
		double d = 0;
		for (double t : data) {
			d = Math.max(Math.abs(t), d);
		}
		return d;
	}

	@Override
	public DenseVector slice(int[] indices) {
		double[] values = new double[indices.length];
		for (int i = 0; i < indices.length; ++i) {
			if (indices[i] >= data.length) {
				throw new IllegalArgumentException("Index is larger than vector size.");
			}
			values[i] = data[indices[i]];
		}
		return new DenseVector(values);
	}

	@Override
	public DenseVector prefix(double d) {
		double[] data = new double[this.size() + 1];
		data[0] = d;
		System.arraycopy(this.data, 0, data, 1, this.data.length);
		return new DenseVector(data);
	}

	@Override
	public DenseVector append(double d) {
		double[] data = new double[this.size() + 1];
		System.arraycopy(this.data, 0, data, 0, this.data.length);
		data[this.size()] = d;
		return new DenseVector(data);
	}

	@Override
	public void scaleEqual(double d) {
		BLAS.scal(d, this);
	}

	@Override
	public DenseVector plus(Vector other) {
		DenseVector r = this.clone();
		if (other instanceof DenseVector) {
			BLAS.axpy(1.0, (DenseVector) other, r);
		} else {
			BLAS.axpy(1.0, (SparseVector) other, r);
		}
		return r;
	}

	@Override
	public DenseVector minus(Vector other) {
		DenseVector r = this.clone();
		if (other instanceof DenseVector) {
			BLAS.axpy(-1.0, (DenseVector) other, r);
		} else {
			BLAS.axpy(-1.0, (SparseVector) other, r);
		}
		return r;
	}

	@Override
	public DenseVector scale(double d) {
		DenseVector r = this.clone();
		BLAS.scal(d, r);
		return r;
	}

	@Override
	public double dot(Vector vec) {
		if (vec instanceof DenseVector) {
			return BLAS.dot(this, (DenseVector) vec);
		} else {
			return vec.dot(this);
		}
	}

	@Override
	public void standardizeEqual(double mean, double stdvar) {
		int size = data.length;
		for (int i = 0; i < size; i++) {
			data[i] -= mean;
			data[i] *= (1.0 / stdvar);
		}
	}

	@Override
	public void normalizeEqual(double p) {
		double norm = 0.0;
		if (Double.isInfinite(p)) {
			norm = normInf();
		} else if (p == 1.0) {
			norm = normL1();
		} else if (p == 2.0) {
			norm = normL2();
		} else {
			for (double datum : data) {
				norm += Math.pow(Math.abs(datum), p);
			}
			norm = Math.pow(norm, 1 / p);
		}
		for (int i = 0; i < data.length; i++) {
			data[i] /= norm;
		}
	}

	/**
	 * Set the data of the vector the same as those of another vector.
	 */
	public void setEqual(DenseVector other) {
		assert this.size() == other.size() : "Size of the two vectors mismatched.";
		System.arraycopy(other.data, 0, this.data, 0, this.size());
	}

	/**
	 * Plus with another vector.
	 */
	public void plusEqual(Vector other) {
		if (other instanceof DenseVector) {
			BLAS.axpy(1.0, (DenseVector) other, this);
		} else {
			BLAS.axpy(1.0, (SparseVector) other, this);
		}
	}

	/**
	 * Minus with another vector.
	 */
	public void minusEqual(Vector other) {
		if (other instanceof DenseVector) {
			BLAS.axpy(-1.0, (DenseVector) other, this);
		} else {
			BLAS.axpy(-1.0, (SparseVector) other, this);
		}
	}

	/**
	 * Convert to a sparse vector.
	 */
	public SparseVector toSparseVector() {
		int nnz = 0;
		for (double datum : this.data) {
			if (datum != 0) {
				nnz++;
			}
		}
		int[] indices = new int[nnz];
		double[] vals = new double[nnz];
		int iter = 0;
		for (int i = 0; i < this.data.length; ++i) {
			if (data[i] != 0) {
				indices[iter] = i;
				vals[iter++] = data[i];
			}
		}
		return new SparseVector(data.length, indices, vals);
	}
	/**
	 * Plus with another vector scaled by "alpha".
	 */
	public void plusScaleEqual(Vector other, double alpha) {
		if (other instanceof DenseVector) {
			BLAS.axpy(alpha, (DenseVector) other, this);
		} else {
			BLAS.axpy(alpha, (SparseVector) other, this);
		}
	}

	@Override
	public DenseMatrix outer() {
		return this.outer(this);
	}

	/**
	 * Compute the outer product with another vector.
	 *
	 * @return The outer product matrix.
	 */
	public DenseMatrix outer(DenseVector other) {
		int nrows = this.size();
		int ncols = other.size();
		double[] data = new double[nrows * ncols];
		int pos = 0;
		for (int j = 0; j < ncols; j++) {
			for (int i = 0; i < nrows; i++) {
				data[pos++] = this.data[i] * other.data[j];
			}
		}
		return new DenseMatrix(nrows, ncols, data, false);
	}


	/**
	 * encode this vector to a byte[]:
	 * The format of the byte[] is: "vectorType value1 value2 value3..."
	 * @return byte array.
	 */
	@Override
	public byte[] toBytes() {
		byte[] bytes = new byte[data.length * 8 + 1];
		ByteBuffer wrapper = ByteBuffer.wrap(bytes);
		wrapper.put(VectorSerialType.DENSE_VECTOR);
		for (double datum : data) {
			wrapper.putDouble(datum);
		}
		return bytes;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		DenseVector that = (DenseVector) o;
		return Arrays.equals(data, that.data);
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(data);
	}

	@Override
	public VectorIterator iterator() {
		return new DenseVectorIterator();
	}

	private class DenseVectorIterator implements VectorIterator {
		private static final long serialVersionUID = 8061929971904422473L;
		private int cursor = 0;

		@Override
		public boolean hasNext() {
			return cursor < data.length;
		}

		@Override
		public void next() {
			cursor++;
		}

		@Override
		public int getIndex() {
			if (cursor >= data.length) {
				throw new RuntimeException("Iterator out of bound.");
			}
			return cursor;
		}

		@Override
		public double getValue() {
			if (cursor >= data.length) {
				throw new RuntimeException("Iterator out of bound.");
			}
			return data[cursor];
		}
	}
}
