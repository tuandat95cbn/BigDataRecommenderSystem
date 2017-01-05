package test;

import org.la4j.Matrix;
import org.la4j.Vector;
import org.la4j.decomposition.SingularValueDecompositor;
import org.la4j.matrix.dense.Basic2DMatrix;
import org.la4j.vector.dense.BasicVector;

public class SVDtest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		double []x= {3,4};
		double []y={5,6};
		Vector v = new  BasicVector(x);
		Vector z = new  BasicVector(y);
		System.out.println(v.euclideanNorm());
	}
}
