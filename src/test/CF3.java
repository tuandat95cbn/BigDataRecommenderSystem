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

public class CF3 {
	private MatrixRatingMovie matrixRating;
	private double [][] x;
	private int N=10;
	private int tN=100;
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
			movie.readSparseMatrix("Data/Movie_ratings.txt");
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
				if(rand.nextDouble()<0.005){
					testRa.add(new TriPair(it.get(), it.columnIndex(), it.rowIndex()));
					it.set(0);
				}
				it.next();
			}
		}
		System.out.println("Done split "+cc);
		
		System.out.println("Done cal Norm");
		System.out.println(testRa);
		
		double nguy=x.sum()/(x.cardinality()*1.0);
		//System.out.println(new Basic2DMatrix(testRa));
		ArrayList<ArrayList<TPair>> S= new ArrayList<ArrayList<TPair>>();
		SparseMatrix xT=x.toColumnMajorSparseMatrix();
		HashMap<Integer, ArrayList<TPair>> map= new HashMap<Integer, ArrayList<TPair>>();
		HashMap<Integer, Double> mapBias= new HashMap<Integer, Double>();
		for(int i=0;i<tN;i++){
			TriPair p=testRa.get(i);
			if( !mapBias.containsKey(p.r)){
				Vector u=x.getRow(p.r);
				SparseVector uS=u.toSparseVector();
				double bais=0;
				if(uS.cardinality()!=0) bais= u.sum()/(uS.cardinality()*1.0)-nguy;
				mapBias.put(p.r, bais);
			}
		}
		double[] t= new double[c];
		for(int i=0;i<xT.columns();i++){
			Vector v= xT.getColumn(i);
			t[i]=v.euclideanNorm();
		}
		HashMap<Integer, Double> mapBiasMovie= new HashMap<Integer, Double>();
		for(int i=0;i<tN;i++){
			System.out.println("Cal S"+i);
			TriPair p=testRa.get(i);
			if(!map.containsKey(p.c)){
				Vector u=xT.getColumn(p.c);
				SparseVector uS=u.toSparseVector();
				double bais=0;
				if(uS.cardinality()!=0) bais= u.sum()/(uS.cardinality()*1.0)-nguy;
				ArrayList<TPair> mi= new ArrayList<TPair>();
				mi.add(new TPair(-100000, -1));
				for(int j=0;j<xT.columns();j++)
					if (p.c!=j){
					
					Vector v=xT.getColumn(j);
					double mu=0;
					if((t[p.c]*t[j])!=0)
						mu=u.innerProduct(v)/(t[p.c]*t[j]);
					
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
				System.out.println("mi"+mi);
				for(int j=0;j<mi.size();j++)
					if(!mapBiasMovie.containsKey(mi.get(j).vt)){
						
						Vector uu=xT.getColumn(mi.get(j).vt);
						SparseVector uuS=uu.toSparseVector();
						double baisMi=0;
						if(uuS.cardinality()!=0) baisMi= uu.sum()/(uuS.cardinality()*1.0)-nguy;
						mapBiasMovie.put(mi.get(j).vt, baisMi);
					}
				map.put(p.c, mi);
				mapBiasMovie.put(p.c, bais);
				System.out.println("map push"+p.c);
			}
		}
		System.out.println("Done cal S");
		
		
		
		
		double rex=0;
		ArrayList<TriPair> raPre= new ArrayList<TriPair>();
		for(int i=0;i<tN;i++){
			//Vector v=x.getRow(x.rows()-tN+i);
			TriPair tP=testRa.get(i);
			double rate=0.0;
			double sumDs=0;
			ArrayList<TPair> v= map.get(tP.c);
			for(int jj=0;jj<v.size();jj++){
					TPair p=v.get(jj);
					rate +=p.ds*(x.get(tP.r, p.vt)-nguy-mapBias.get(tP.r)-mapBiasMovie.get(p.vt));//
					sumDs+=p.ds;
				}
			double val=0;
			if(sumDs!=0) val=rate/(sumDs*1.0)+nguy+mapBias.get(tP.r)+mapBiasMovie.get(tP.c);
			raPre.add(new TriPair(val,tP.r,tP.c ));
			rex+=Math.pow(val-tP.ds, 2);
			System.out.println(tP.ds+" "+val);
		}
		
		System.out.println(rex);
		System.out.println(Math.sqrt(rex/(tN*1.0)));
	}
	private void init(){
		matrixRating = new MatrixRatingMovie();
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		CF3 c= new CF3();
		c.init();
		c.makeCollaborativeMatrix();
	}

}
