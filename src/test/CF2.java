package test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.zip.ZipEntry;

import org.la4j.Matrix;
import org.la4j.Vector;
import org.la4j.iterator.MatrixIterator;
import org.la4j.iterator.VectorIterator;
import org.la4j.matrix.SparseMatrix;
import org.la4j.matrix.dense.Basic2DMatrix;
import org.la4j.matrix.sparse.CCSMatrix;
import org.la4j.matrix.sparse.CRSMatrix;
import org.la4j.vector.SparseVector;
class TriPair{
	
	
	public TriPair(double ds, int c, int r) {
		super();
		this.ds = ds;
		this.c = c;
		this.r = r;
	}
	public double ds;
	public int c;
	public int r;
	@Override
	public String toString() {
		return "TriPair [ds=" + ds + ", c=" + c + ", r=" + r + "]";
	}
	
	
}
public class CF2 {
	private MatrixRatingMovie matrixRating;
	private double [][] x;
	private int N=5;
	private int tN=1000;
	private void storeSparseMatrix(SparseMatrix x){
		String fileName= "SparseMatrix.txt";
	    FileOutputStream fos;
		try {
			fos = new FileOutputStream(fileName);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
		    oos.writeObject(x.toBinary());
		    oos.close();
		    fos.close();
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
			fin.close();
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
			movie.readSparseMatrix("Data2/Movie_ratings.txt");
			x= movie.getSparseMatrix();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
//		SparseMatrix y=new CRSMatrix(27278,138493); 
//		for(int i=0;i<x.columns();i++){
//			System.out.println("row: "+i);
//			y.setRow(i, x.getColumn(i));
//			x.removeColumn(i);
//		}
//		x=null;
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
		int cc=0;
		Random rand= new Random();
		double e=0.3;
		//SparseMatrix testRa= new CRSMatrix(tN,tN);
		ArrayList<TriPair> testRa= new ArrayList<TriPair>();
		for(int i=0;i<tN;i++){
			Vector v=x.getRow(x.rows()-tN+i);
			MatrixIterator it = x.nonZeroIterator();
			while (testRa.size()<tN){
				if(rand.nextDouble()<0.01){
					testRa.add(new TriPair(it.get(), it.columnIndex(), it.rowIndex()));
					it.set(0);
					System.out.println(testRa.size());
				}
				it.next();
			}
		}
		System.out.println("Done split "+cc);
		double[] t= new double[r];
		for(int i=0;i<x.rows();i++){
			Vector v= x.getRow(i);
			t[i]=v.euclideanNorm();
		}
		System.out.println("Done cal Norm");
		//System.out.println(testRa);
		
		double nguy=x.sum()/(x.cardinality()*1.0);
		//System.out.println(new Basic2DMatrix(testRa));
		ArrayList<ArrayList<TPair>> S= new ArrayList<ArrayList<TPair>>();
		//SparseMatrix xT=x.toColumnMajorSparseMatrix();
		HashMap<Integer, ArrayList<TPair>> map= new HashMap<Integer, ArrayList<TPair>>();
		HashMap<Integer, Double> mapBias= new HashMap<Integer, Double>();
		HashMap<Integer, Double> mapBiasMovie= new HashMap<Integer, Double>();
		
		for(int i=0;i<tN;i++){
			TriPair p=testRa.get(i);
			if( !mapBiasMovie.containsKey(p.c)){
				Vector u=x.getColumn(p.c);
				SparseVector uS=u.toSparseVector();
				double bais=0;
				if(uS.cardinality()!=0) bais= u.sum()/(uS.cardinality()*1.0)-nguy;
				mapBiasMovie.put(p.c, bais);
			}
		}
		ArrayList<TriPair> raPre= new ArrayList<TriPair>();
		double rex=0;
		System.out.println("Done cal bais");
		for(int i=0;i<tN;i++){
			System.out.println("Cal S"+i);
			TriPair p=testRa.get(i);
			Vector u=x.getRow(p.r);
			if(!mapBias.containsKey(p.r)){
				SparseVector uS=u.toSparseVector();
				double bais=0;
				if(uS.cardinality()!=0) bais= u.sum()/(uS.cardinality()*1.0)-nguy;
				else System.out.println("0->"+p.r);
				mapBias.put(p.r, bais);
			}
				ArrayList<TPair> mi= new ArrayList<TPair>();
				
				for(int j=0;j<x.rows();j++)
					if (p.r!=j){
					Vector v=x.getRow(j);
					double mu=u.innerProduct(v)/(t[p.r]*t[j]);
					if ((mi.size()==0||mu>mi.get(mi.size()-1).ds)&& v.get(p.c)!=0){
						if(mi.size()+1>N)
							mi.remove(mi.size()-1);
						int xd=0;
						for(int ii=mi.size()-1;ii>0;ii--)
							if(mi.get(ii).ds>mu && u.get(p.c)!=0){ 
								mi.add(ii+1, new TPair(mu,j));
								xd=1;
								break;
							}
						if(xd==0) mi.add(0, new TPair(mu,j));
					}
				}
				//System.out.println(mi);
				for(int j=0;j<mi.size();j++)
					if(!mapBias.containsKey(mi.get(j).vt)){
						Vector uu=x.getRow(mi.get(j).vt);
						SparseVector uuS=uu.toSparseVector();
						double baisMi=0;
						if(uuS.cardinality()!=0) baisMi= uu.sum()/(uuS.cardinality()*1.0)-nguy;
						mapBias.put(mi.get(j).vt, baisMi);
					}
				//map.put(p.r, mi);
				
				
				double rate=0.0;
				double sumDs=0;
				
				for(int jj=0;jj<mi.size();jj++){
						TPair pp=mi.get(jj);
						rate +=pp.ds*(x.get(pp.vt, p.c)-nguy-mapBias.get(pp.vt)-mapBiasMovie.get(p.c));
						sumDs+=pp.ds;
					}
				double val=0;
				//if(rate==0) System.out.println("rate)->"+v);
				if(sumDs!=0) val=rate/(sumDs*1.0)+nguy+mapBias.get(p.r)+mapBiasMovie.get(p.c);//
				//else System.out.println(")->"+p.r);
				raPre.add(new TriPair(val,p.r,p.c ));
				rex+=Math.pow(val-p.ds, 2);
				//System.out.println(p.r+" "+p.ds+" "+val);
				//System.out.println("map push"+p.r);
			
		}
		System.out.println("Done cal S");
		System.out.println(rex);
		System.out.println(Math.sqrt(rex/(tN*1.0)));
	}
	private void init(){
		matrixRating = new MatrixRatingMovie();
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		CF2 c= new CF2();
		c.init();
		c.makeCollaborativeMatrix();
	}

}
