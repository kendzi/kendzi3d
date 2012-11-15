/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.math.formater;


import java.text.DecimalFormat;
import java.util.Formatter;
import java.util.Locale;

import org.ejml.data.DenseMatrix64F;
import org.ejml.simple.SimpleMatrix;

public class SimpleMatrixUtil {

	public static String matrixToString(SimpleMatrix m) {
		DenseMatrix64F mat = m.getMatrix();
		StringBuilder sb = new StringBuilder();
		// Send all output to the Appendable object sb
		Formatter formatter = new Formatter(sb, Locale.US);

		// Explicit argument indices may be used to re-order output.

		int numChar = 6;
		int precision = 3;
		sb.append("DenseMatrix64F  numRows = " + mat.numRows + " numCols = " + mat.numCols + "\n");

		String format = "%" + numChar + "." + precision + "f ";

		for (int y = 0; y < mat.numRows; y++) {
			for (int x = 0; x < mat.numCols; x++) {

				formatter.format(format, mat.get(y, x));
				// sb.append("\n");
			}
			sb.append("\n");
		}
		return sb.toString();

	}

	public static String matrixToMatlab(SimpleMatrix m) {
		if (m == null) {
			return " [ ]; ";
		}
		if ((m.numRows() == 0) || (m.numCols() ==0)) {
			return " [ ]; ";
		}

		DenseMatrix64F mat = m.getMatrix();
		StringBuilder sb = new StringBuilder();
		// Send all output to the Appendable object sb
		Formatter formatter = new Formatter(sb, Locale.US);

		String space = "        ";

		// Explicit argument indices may be used to re-order output.

		int numChar = 6;
		int precision = 3;
		//sb.append("DenseMatrix64F  numRows = " + mat.numRows + " numCols = " + mat.numCols + "\n");

		String format = "%" + numChar + "." + precision + "f ";
		sb.append(space);
		sb.append("[ ");
		for (int y = 0; y < mat.numRows; y++) {
			for (int x = 0; x < mat.numCols; x++) {
				if (x != 0) {
					sb.append(", ");
				}

				sb.append(mat.get(y, x));

				//				formatter.format(format, mat.get(y, x));
				// sb.append("\n");
			}
			sb.append(" ; \n  ");
			sb.append(space);

		}
		sb.append("];");
		return sb.toString();


	}
	public static String ff(Integer n) {
		return ff(new Double(n));
	}
	public static String ff(Double n) {
		StringBuilder sb = new StringBuilder();
		Formatter formatter = new Formatter(sb, Locale.US);
		int numChar = 6;
		int precision = 3;

		String format = "%" + numChar + "." + precision + "f ";

		formatter.format(format,  n);

		return sb.toString();

	}

	public static String ff2(Number n) {
		String pattern = "0.000E0";
		DecimalFormat myFormatter = new DecimalFormat(pattern);
		String output = myFormatter.format(n);
		return output;
	}
}
