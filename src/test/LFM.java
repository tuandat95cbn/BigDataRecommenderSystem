package test;

import java.io.IOException;
import java.util.ArrayList;

import org.la4j.Matrix;
import org.la4j.Vector;
import org.la4j.decomposition.SingularValueDecompositor;
import org.la4j.matrix.SparseMatrix;
import org.la4j.matrix.dense.Basic2DMatrix;

public class LFM {
	SparseMatrix S,D;
	int tN=50;
	public void solution(){
		//double a[][]={{0,1,2,0},{0,3,4,6},{4,0,5,0}};
		//Matrix x = new Basic2DMatrix(a);
		//Matrix x=null;
		SparseMatrix x=null;
		MatrixRatingMovie movie = new MatrixRatingMovie();
		try {
			movie.readSparseMatrix("Data/Movie_ratings.txt");
			x= movie.getSparseMatrix();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		int cc=0;
		double testRa[][]= new double[tN][tN];
		for(int i=0;i<tN;i++){
			Vector v=x.getRow(x.rows()-tN+i);
			for(int j=0;j<tN;j++){
				testRa[i][j]=v.get(j);
				if(testRa[i][j]!=0) cc++;
				x.set(x.rows()-tN+i,j, 0);
			}
		}
		System.out.println(cc);
		try {
			movie.readMatrixS("S1.txt");
			movie.readMatrixP("D1.txt");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		S=movie.getS();
		D=movie.getP();
		//System.out.println(D.getRow(0));
		double raPre[][]= new double[tN][tN];
		for(int i=0;i<tN;i++){
			
			Vector v= S.getRow(x.rows()-tN+i);
			for(int j=0;j<tN;j++)
				if (testRa[i][j]!=0 ){
					Vector u= D.getColumn(x.columns()-tN+j);
					raPre[i][j]=v.innerProduct(u);
				} else{
					raPre[i][j]=0;
				}
		}
		System.out.println("Done pre rating");
		double rex=0;
		int count=0;
		for(int i=0;i<tN;i++)
			for(int j=0;j<tN;j++)
				if(testRa[i][j]!=0 ){
					count++;
					System.out.println(raPre[i][j]+" "+testRa[i][j]);
					rex+=Math.pow(raPre[i][j]-testRa[i][j], 2);
					if(Double.isNaN(rex))
						System.exit(0);
					System.out.println(i+" "+j+" "+rex+" "+count);
				}
		System.out.println(count);
		System.out.println(rex);
		System.out.println(Math.sqrt(rex)/(cc*1.0));
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		LFM l= new LFM();
		l.solution();
	}

}
