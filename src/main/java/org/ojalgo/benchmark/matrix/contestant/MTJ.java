/*
 * Copyright 1997-2017 Optimatika (www.optimatika.se)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.ojalgo.benchmark.matrix.contestant;

import org.ojalgo.benchmark.matrix.BenchmarkContestant;

import no.uib.cipr.matrix.DenseMatrix;
import no.uib.cipr.matrix.Matrix;
import no.uib.cipr.matrix.NotConvergedException;
import no.uib.cipr.matrix.SVD;
import no.uib.cipr.matrix.SymmDenseEVD;

/**
 * Matrix Toolkits Java
 */
public class MTJ extends BenchmarkContestant<Matrix> {

	@Override
	protected double[][] convertFrom(final Matrix matrix) {
		final double[][] retVal = new double[matrix.numRows()][matrix.numColumns()];
		for (int i = 0; i < retVal.length; i++) {
			final double[] tmpRow = retVal[i];
			for (int j = 0; j < tmpRow.length; j++) {
				tmpRow[j] = matrix.get(i, j);
			}
		}
		return retVal;
	}

	@Override
	protected Matrix convertTo(final double[][] raw) {
		return new DenseMatrix(raw);
	}

	@Override
	public BenchmarkContestant<Matrix>.EigenDecomposer getEigenDecomposer() {
		return new EigenDecomposer() {

			@Override
			public Matrix apply(final Matrix matrix) {
				try {
					return SymmDenseEVD.factorize(matrix).getEigenvectors();
				} catch (final NotConvergedException nce) {
					throw new RuntimeException(nce);
				}
			}
		};
	}

	@Override
	public GeneralSolver getGeneralSolver() {
		return new GeneralSolver() {

			@Override
			public Matrix apply(final Matrix body, final Matrix rhs) {

				final DenseMatrix result = new DenseMatrix(body.numColumns(), rhs.numColumns());

				body.solve(rhs, result);

				return result;
			}

		};
	}

	@Override
	public HermitianSolver getHermitianSolver() {
		return new HermitianSolver() {

			@Override
			public Matrix apply(final Matrix body, final Matrix rhs) {

				final DenseMatrix result = new DenseMatrix(body.numColumns(), rhs.numColumns());

				body.solve(rhs, result);

				return result;
			}

		};
	}

	@Override
	public LeastSquaresSolver getLeastSquaresSolver() {
		return new LeastSquaresSolver() {

			@Override
			public Matrix apply(final Matrix body, final Matrix rhs) {

				final DenseMatrix result = new DenseMatrix(body.numColumns(), rhs.numColumns());

				body.solve(rhs, result);

				return result;
			}

		};
	}

	@Override
	public BenchmarkContestant<Matrix>.MatrixBuilder getMatrixBuilder(final int numberOfRows,
			final int numberOfColumns) {
		return new MatrixBuilder() {

			private final DenseMatrix myMatrix = new DenseMatrix(numberOfRows, numberOfColumns);

			public Matrix get() {
				return myMatrix;
			}

			@Override
			public void set(final int row, final int col, final double value) {
				myMatrix.set(row, col, value);
			}

		};
	}

	@Override
	public MatrixMultiplier getMatrixMultiplier() {
		return new MatrixMultiplier() {

			@Override
			public Matrix apply(final Matrix left, final Matrix right) {

				final DenseMatrix retVal = new DenseMatrix(left.numRows(), right.numColumns());

				return left.mult(right, retVal);
			}

		};
	}

	@Override
	public BenchmarkContestant<Matrix>.SingularDecomposer getSingularDecomposer() {
		return new SingularDecomposer() {

			@Override
			public Matrix apply(final Matrix matrix) {

				final no.uib.cipr.matrix.SVD svd = new no.uib.cipr.matrix.SVD(matrix.numRows(), matrix.numColumns());
				final DenseMatrix tmp = new DenseMatrix(matrix);

				DenseMatrix U = null;
				double[] S = null;
				DenseMatrix Vt = null;

				try {

					tmp.set(matrix);
					final SVD s = svd.factor(tmp);
					U = s.getU();
					S = s.getS();
					Vt = s.getVt();
				} catch (final NotConvergedException e) {
					throw new RuntimeException(e);
				}

				return Vt;
			}

		};
	}

	@Override
	public TransposedMultiplier getTransposedMultiplier() {
		return new TransposedMultiplier() {

			@Override
			public Matrix apply(final Matrix left, final Matrix right) {

				final DenseMatrix retVal = new DenseMatrix(left.numRows(), right.numColumns());

				return left.transBmult(right, retVal);
			}

		};
	}

}