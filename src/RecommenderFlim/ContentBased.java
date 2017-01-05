package RecommenderFlim;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

import org.la4j.Matrix;
import org.la4j.Vector;
import org.la4j.decomposition.SingularValueDecompositor;
import org.la4j.matrix.DenseMatrix;
import org.la4j.matrix.SparseMatrix;
import org.la4j.matrix.dense.Basic2DMatrix;
import org.la4j.matrix.sparse.CRSMatrix;
import org.la4j.vector.dense.BasicVector;
import org.la4j.vector.functor.VectorProcedure;

import test.MatrixItemAndFeatures;
import test.MatrixRatingMovie;

class JacardOfFilm {
	
	int movie_index;
	double jacard;
	public JacardOfFilm(int movie_index,double jacard){
		this.movie_index = movie_index;
		this.jacard = jacard;
	}
	
}

public class ContentBased {
	
	private MatrixItemAndFeatures matrixContent;
	private MatrixRatingMovie matrixRating;
	private int n_user = 1;
	private int m_movie = 27278;
	private int m_test = 1000;
	private int m_pre = 10;
	private double movie_feature[][];
	private double user_movie[][];
	private double user_test[][];
	private DenseMatrix moive_f;
	private DenseMatrix user_m;
	private int moive_index[][];
	private Map<Integer, Integer> movieId_index;
	
	
	public ContentBased() throws IOException {
		matrixContent = new MatrixItemAndFeatures();
		matrixRating = new MatrixRatingMovie();
		moive_index = new int[n_user][m_pre];
		matrixRating.readFileMoives("Data/Movies_Title.txt");
		movieId_index = matrixRating.getMovieId_index();
	}
	
	public void initMovieFeature() throws IOException {
		matrixContent.readFileMovie("ml-20m/movies.csv");
		matrixContent.initMatrixMovieFeature();
		movie_feature = matrixContent.getMatrixItemAndFeatures();
		moive_f = new Basic2DMatrix(movie_feature); 
	}
	
	/*
	 * replace 100 movie first of users -> 0
	 */
	
	public void initUserMovieTest() throws IOException {
		matrixRating.readMatrix("Data/Movie_ratings.txt", 0, 1);
		user_test = matrixRating.getMatrix();
		for(int i = 0;i < n_user;i++) {
			for(int j = 0;j < m_test;j++) {
				user_test[i][j] = 0.0;
			}
		}
		user_m = new Basic2DMatrix(user_test);
	}
	
	
	public void initUserMovie() throws IOException{
		matrixRating.readMatrix("Data/Movie_ratings.txt", 0, 1);
		user_movie = matrixRating.getMatrix();
		user_m = new Basic2DMatrix(user_movie);
	}
	
	public DenseMatrix getFeatureOfUser() {
		DenseMatrix a = (DenseMatrix) user_m.multiply(moive_f);
		double array[][] = a.toArray();
		for(int i= 0;i < n_user;i++) {
			for(int j = 0;j < 20;j++) {
				if(array[i][j] <= 0) {
					array[i][j] = 0.0;
				}else {
					array[i][j] = 1.0;
				}
			}
		}
		return new Basic2DMatrix(array);
	}
	
	
	public double Jacard(Vector a,Vector b){
		int n = 0;
		int m = 0;
		int size = a.length();
		/*System.out.println(a.length());
		System.out.println(b.length());*/
		for(int i = 0;i < size ;i++) {
			if(a.get(i) == 1.0 && b.get(i) == 1.0){
				m++;
			}
			if(a.get(i) == 1.0 || b.get(i) == 1.0) {
				n++;
			}
		}
		return (double)m/n;	
	}
	
	public double Cosin(Vector a,Vector b) {
		int size = a.length();
		/*System.out.println("Vector a is: " + a);
		System.out.println("Vector b is: " + b);*/
		double s = 0,sqrtA = 0,sqrtB = 0;
		for(int i = 0;i < size;i++) {
			s += a.get(i)*b.get(i);
			sqrtA += a.get(i)*a.get(i);
			sqrtB += b.get(i)*b.get(i);
		}
		sqrtA = Math.sqrt(sqrtA);
		sqrtB = Math.sqrt(sqrtB);
		return (double)s/(sqrtA*sqrtB);
	}
	
	
	/*public void RecommandFilm(){	
		for(int i = 0;i < n_user;i++) {
			Vector a = getFeatureOfUser().getRow(i);
			double max = 0;
			int index = 0;
			for(int j = 0;j < m_test;j++) {
				Vector b = moive_f.getRow(j);
				double ja = Jacard(a, b);
				if(max < ja){
					max = ja;
					index = j;
				}
			}
			System.out.println("Movie is: " + index);
		} 
	}*/
	public void RecommandFilm(){	
		for(int i = 0;i < n_user;i++) {
			Vector a = getFeatureOfUser().getRow(i);
			int pre[] = FindMaxPre(a);
			for(int j =0;j < m_pre;j++) {
				moive_index[i][j] = pre[j];
			}
		}
		
	}
	
	void printPredict() {	
		for(int i = 0;i < n_user;i++) {
			System.out.print("\nUser " + i + ": ");
			for(int j = 0;j < m_pre;j++) {
				System.out.print(moive_index[i][j] + ",");
			}
		}
		
	}
	
	public int[] FindMaxPre(Vector a){
		//System.out.println("Vector a is: " + a);
		int jacard_max[] = new int[m_pre];
		ArrayList<JacardOfFilm> jacard = new ArrayList<JacardOfFilm>();
		for(int i = 0;i < m_test;i++) {
			Vector b = moive_f.getRow(i);
			JacardOfFilm jacard_index = new JacardOfFilm(i,Cosin(a, b));
			jacard.add(jacard_index);
		}
		Collections.sort(jacard, new Comparator<JacardOfFilm>() {
			@Override
			public int compare(JacardOfFilm o1, JacardOfFilm o2) {
				// TODO Auto-generated method stub
				Double a = new Double(o1.jacard);
				Double b = new Double(o2.jacard);
				return b.compareTo(a);
			}
		});
		System.out.println("\nJacard is: ");
		for(int i= 0;i < jacard.size();i++){
			System.out.print(jacard.get(i).jacard + " ");
		}
		for(int i =0;i < m_pre;i++) {
			jacard_max[i] = jacard.get(i).movie_index;
		}
		return jacard_max;
	}
	
	public static void main(String args[]) throws IOException{
		/*ContentBased contentBased = new ContentBased();
		contentBased.initMovieFeature();
		contentBased.initUserMovieTest();
		contentBased.RecommandFilm();
		contentBased.printPredict();*/
		double x[][] = {{0,1,2},{0,0,0},{1,0,1}};
		double v[] = {1,0,0,4};
	//	a.eachNonZeroInRow(3, (VectorProcedure) vector);
//		System.out.println(a);
		//SingularValueDecompositor dc = new SingularValueDecompositor(a);
//		System.out.println("DONE");
	//	System.out.println(a);
		/*double a[][] = {{1,0,0,0,2},{0,0,3,0,0},{0,0,0,0,0},{0,2,0,0,0}};		
		Matrix matrix = new Basic2DMatrix(a);
		System.out.println("\nMatrix a is: ");
		System.out.println(matrix);
		SingularValueDecompositor decomp = new  SingularValueDecompositor(matrix);
		Matrix[] result = decomp.decompose();
		Matrix U = result[0];
		Matrix D = result[1];
		Matrix V = result[2];
		System.out.println("Maxtrix S is: \n");
		System.out.println(U);
		System.out.println("Maxtrix V is: \n");
		System.out.println(V);
		System.out.println("Maxtrix D is: \n");
		System.out.println(D);
		Matrix s = U.multiply(D).multiply(V.transpose());
		System.out.println("matrix s is: ");
		System.out.println(s);*/
		
	}
	
}
