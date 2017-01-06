package test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;

import org.la4j.Matrix;
import org.la4j.Vector;
import org.la4j.matrix.SparseMatrix;
import org.la4j.matrix.dense.Basic2DMatrix;
import org.la4j.matrix.sparse.CCSMatrix;
class TPair{
	
	public TPair(double ds, int vt) {
		super();
		this.ds = ds;
		this.vt = vt;
	}
	public double ds;
	public int vt;
	@Override
	public String toString() {
		return "TPair [ds=" + ds + ", vt=" + vt + "]";
	}
	
}
public class CF {
	private MatrixRatingMovie matrixRating;
	private double [][] x;
	private int N=5;
	private int tN=200;
	private void storeSparseMatrix(SparseMatrix x){
		String fileName= "SparseMatrix.txt";
	    FileOutputStream fos;
		try {
			fos = new FileOutputStream(fileName);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
		    oos.writeObject(x.toBinary());
		    oos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	}
	private SparseMatrix readMatrixSparse(String fileName){
		//String fileName= "Test.txt";
		FileInputStream fin;
		SparseMatrix m=null;
		try {
			fin = new FileInputStream(fileName);
			ObjectInputStream ois = new ObjectInputStream(fin);
			byte[] l= (byte[]) ois.readObject();
			ois.close();
			m=CCSMatrix.fromBinary(l);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return m;
	}
	private void makeCollaborativeMatrix(){
		double a[][]={{0,1,2},{0,3,4},{4,0,5}};
		//Matrix x = new Basic2DMatrix(a);
		//Matrix x=null;
		SparseMatrix x=null;
		MatrixRatingMovie movie = new MatrixRatingMovie();
		try {
			movie.readInitMatrix("Data2/Movie_ratings.txt");
			x= movie.getSparseMatrix();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		/*try {
			x=matrixRating.readMatrixRating("Data/Movie_ratings.txt", 0, 669);
			System.out.println("This is matrix:"+x.rows()+" "+x.columns());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		//storeSparseMatrix(x);
		//x=readMatrixSparse("SparseMatrix.txt");
		System.out.println("Done read file");
		int r=x.rows();
		int c=x.columns();
		double[] t= new double[r];
		for(int i=0;i<x.rows();i++){
			Vector v= x.getRow(i);
			t[i]=v.euclideanNorm();
		}
		System.out.println("Done cal Norm");
		//System.out.println(x.getRow(x.rows()-1));
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
		System.out.println("Done split "+cc);
		
		//System.out.println(new Basic2DMatrix(testRa));
		ArrayList<ArrayList<TPair>> S= new ArrayList<ArrayList<TPair>>();
		for(int i=0;i<tN;i++){
			System.out.println("Cal S"+i);
			int it=x.rows()-tN+i;
			Vector u=x.getRow(it);
			ArrayList<TPair> mi= new ArrayList<TPair>();
			mi.add(new TPair(-100000, -1));
			for(int j=0;j<x.rows();j++)
				if (it!=j){
				Vector v=x.getRow(j);
				double mu=u.innerProduct(v)/(t[it]*t[j]);
				if (mu>mi.get(mi.size()-1).ds){
					if(mi.size()+1>N)
						mi.remove(mi.size()-1);
					int xd=0;
					for(int ii=mi.size()-1;ii>0;ii--)
						if(mi.get(ii).ds>mu){ 
							mi.add(ii+1, new TPair(mu,j));
							xd=1;
							break;
						}
					if(xd==0) mi.add(0, new TPair(mu,j));
				}
			}
			S.add(mi);
		}
		System.out.println("Done cal S");
		double raPre[][]= new double[tN][tN];
		for(int i=0;i<tN;i++){
			//Vector v=x.getRow(x.rows()-tN+i);
			ArrayList<TPair> v= S.get(i);
			
			for(int j=0;j<tN;j++)
			if (testRa[i][j]!=0 ){
				//testRa[i][j]=v.get();
				double rate=0.0;
				double sumDs=0;
				for(int jj=0;jj<v.size();jj++){
					TPair p=v.get(jj);
					rate +=p.ds*x.get(p.vt, j);
					sumDs+=p.ds;
				}
				raPre[i][j]=rate/sumDs;
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
					rex+=Math.pow(raPre[i][j]-testRa[i][j], 2)*(1/cc)*(1/cc);
				}
		System.out.println(count);
		System.out.println(rex);
		System.out.println(Math.sqrt(rex));
	}
	private void init(){
		matrixRating = new MatrixRatingMovie();
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		CF c= new CF();
		c.init();
		c.makeCollaborativeMatrix();
	}

}
