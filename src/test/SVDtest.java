package test;

import org.la4j.Matrix;
import org.la4j.Vector;
import org.la4j.decomposition.SingularValueDecompositor;
import org.la4j.matrix.SparseMatrix;
import org.la4j.matrix.dense.Basic2DMatrix;
import org.la4j.matrix.sparse.CRSMatrix;
import org.la4j.vector.dense.BasicVector;

public class SVDtest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		double []x= {0,0,5,0,7,0,7,8,9,0};
		double []y={0,0,0,0,0,0,1,0,0,0};
		Vector v = new  BasicVector(x);
		Vector z = new  BasicVector(y);
		SparseMatrix a = new CRSMatrix(2,10);
		a.setRow(0, v);
		a.setRow(1, z);
		
		//SingularValueDecompositor dm = new SingularValueDecompositor(a);
		//Matrix[] e = dm.decompose();
		//System.out.println(e[0]);
		//System.out.println(e[1]);
		//System.out.println(a.c);
	}
}
