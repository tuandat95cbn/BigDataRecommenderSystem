package test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;

import org.la4j.Matrix;
import org.la4j.Vector;
import org.la4j.decomposition.SingularValueDecompositor;
import org.la4j.matrix.SparseMatrix;
import org.la4j.matrix.dense.Basic2DMatrix;
import org.la4j.matrix.sparse.CRSMatrix;
import org.la4j.vector.dense.BasicVector;

class Pair {
	int movie_id;
	float ratingofMovie;
	
	public Pair(int a,float b) {
		movie_id = a;
		ratingofMovie  = b;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		StringBuilder strings = new StringBuilder();
		strings.append("(" + movie_id + ", " + ratingofMovie + ")" );
		return strings.toString();
	}
}


public class MatrixRatingMovie {
	//private int n_user=671;
	private int n_user=138493;
	private int m_movie = 27278;
	private int k = 200;
	//private int m_movie = 9125;
	private Map<Integer, ArrayList<Pair>> user_movies;
	private Map<Integer,Integer> movieId_index;
	private double matrix_user_movie[];
	private BufferedReader bufferedReader;
	private BufferedWriter bufferedWriter;
	private double matrix[][];
	private SparseMatrix sparseMatrix;
	private SparseMatrix P; // P = V*D'
	private SparseMatrix S;
	
	public MatrixRatingMovie(){
		user_movies = new HashMap<Integer, ArrayList<Pair>>();
		movieId_index = new HashMap<>();
		matrix_user_movie = new double[m_movie];
		sparseMatrix = new CRSMatrix(n_user,m_movie);
		P = new CRSMatrix(k,m_movie);
		S = new CRSMatrix(n_user,k);
		/*for(int i = 0;i < n_user;i++){
			for(int j = 0;j < m_movie;j++) {
				matrix_user_movie[i][j] = new Float(0);
			}
		}*/
		//matrix = new double[m_movie][n_user];
	}
	public void init() {
		for(int i = 0;i < m_movie;i++) {
			matrix_user_movie[i] = 0.0;
		}
	}
	/*/
	 * read matrix thua
	 */
	public void readFileRating(String filename) throws IOException {
		bufferedReader = new BufferedReader(new FileReader(new File(filename)));
		bufferedReader.readLine();
		//int count = 0;
		String line = bufferedReader.readLine();
		while(line != null) {
			String line_index[] = line.split(",");
			Integer user_id = Integer.parseInt(line_index[0]);
			Pair movieAndRating = new Pair(Integer.parseInt(line_index[1]),Float.parseFloat(line_index[2]));
			if(user_movies.containsKey(user_id)) {
				user_movies.get(user_id).add(movieAndRating);
			}else {
				ArrayList<Pair> list = new ArrayList<Pair>();
				list.add(movieAndRating);
				user_movies.put(user_id, list);
			}
			line = bufferedReader.readLine();
			System.out.println("Running ...");
		}
		bufferedReader.close();
		System.out.println("READ FILE DONE!");
	}
	
	public void readFileMoives(String filename) throws IOException{
		bufferedReader = new BufferedReader(new FileReader(new File(filename)));
		String line = bufferedReader.readLine();
		int count = 0;
		while(line != null) {
			String line_one[] = line.split("\t");
			movieId_index.put(Integer.parseInt(line_one[0]), count);
			count++;
			line = bufferedReader.readLine();
		}
		bufferedReader.close();
	}
	
	public void readInitMatrix(String filename) throws IOException {
		//readFileMoives("Data/Movies_Title.txt");
		bufferedReader = new BufferedReader(new FileReader(new File(filename)));
		String line = bufferedReader.readLine();
		int count = 0;
		double a[];
		while(line != null && count < n_user ) {
			a = new double[m_movie];
			String strings[] = line.split(" ");
			for(int i = 0;i < strings.length;i++) {
				a[i] = Double.parseDouble(strings[i]);
			}
			Vector vector = new BasicVector(a);
			sparseMatrix.setRow(count, vector);
			line = bufferedReader.readLine();
			if (count %10000==0) System.out.println("Reading count is: " + count + " length is: " + strings.length);
			count++;
		}
		bufferedReader.close();
	}
	
	public void readSparseMatrix(String filename) throws IOException {
		bufferedReader = new BufferedReader(new FileReader(new File(filename)));
		String line = bufferedReader.readLine();
		int count = 0;
		while(line != null) {
			String strings[] = line.split("\t");
			int user_id = Integer.parseInt(strings[0]) - 1;
			int movie_id = Integer.parseInt(strings[1]) - 1;
			double rating  =  Double.parseDouble(strings[2]);
			sparseMatrix.set(user_id, movie_id, rating);
			line = bufferedReader.readLine();
			if(count % 1000000 == 0){
				System.out.println("Reading row is: " + count);
			}
			count++;
		}
		bufferedReader.close();
	}
	
	public void countUser(String filename) throws IOException {
		bufferedReader = new BufferedReader(new FileReader(new File(filename)));
		bufferedReader.readLine();
		String line = bufferedReader.readLine();
		int count = 0;
		int user_id = 1;
		while(line != null) {
			String strings[] = line.split(",");
			if(user_id != Integer.parseInt(strings[0])){
				user_id = Integer.parseInt(strings[0]);
//				System.out.println("count is: " + "(" +user_id +", "+ count+")");
				count++;
			}
			line = bufferedReader.readLine();
		}
		count++;
		System.out.println("user is: " + count);
		bufferedReader.close();
	}
	
	public void sortUserId() {
		List<Map.Entry<Integer, ArrayList<Pair>>> list =  new LinkedList<>(user_movies.entrySet());
		Collections.sort(list,new Comparator<Map.Entry<Integer,
				ArrayList<Pair>>>() {
			@Override
			public int compare(Entry<Integer, ArrayList<Pair>> o1,
					Entry<Integer, ArrayList<Pair>> o2) {
				// TODO Auto-generated method stub
				return (o1.getKey()).compareTo(o2.getKey());
			}
		});
		user_movies.clear();
		user_movies = new LinkedHashMap<>();
		for (Map.Entry<Integer, ArrayList<Pair>> entry : list) {
            user_movies.put(entry.getKey(), entry.getValue());
	    }
		System.out.println("SORTED DONE!");
	}
	
	public void writeMatrix(String filename) throws IOException {
		sortUserId();
		bufferedWriter = new BufferedWriter(new FileWriter(new File(filename)));
		StringBuilder strings = new StringBuilder();
		Set<Map.Entry<Integer, ArrayList<Pair>>> set = user_movies.entrySet();
		for(Map.Entry<Integer, ArrayList<Pair>> rate:set) {
			init();
			ArrayList<Pair> list = rate.getValue();
			int size = list.size();
			for(int i = 0;i < size;i++) {
				int movie_id = movieId_index.get(list.get(i).movie_id);
				matrix_user_movie[movie_id] = list.get(i).ratingofMovie;
			}
			for(int i = 0;i < m_movie;i++) {
				strings.append(matrix_user_movie[i]);
				strings.append(" ");
			}
			strings.append("\n");
			bufferedWriter.write(strings.toString());
			strings.delete(0, strings.length());
		}
		bufferedWriter.flush();
		bufferedWriter.close();
		System.out.println("WRITE FILE DONE!");
	}
	
	public void writeSparseMatrix(String filename) throws IOException {
		sortUserId();
		bufferedWriter = new BufferedWriter(new FileWriter(new File(filename)));
		Set<Map.Entry<Integer, ArrayList<Pair>>> set = user_movies.entrySet();
		for(Map.Entry<Integer, ArrayList<Pair>> rate:set) {
			ArrayList<Pair> list = rate.getValue();
			int size = list.size();
			int user_id = rate.getKey();
			for(int i = 0;i < size;i++) {
				StringBuffer strings = new StringBuffer();
				int movie_id = movieId_index.get(list.get(i).movie_id);
				strings.append(user_id);
				strings.append("\t");
				strings.append(movie_id + 1);
				strings.append("\t");
				strings.append(list.get(i).ratingofMovie);
				strings.append("\n");
				bufferedWriter.write(strings.toString());
			}
		}
		bufferedWriter.flush();
		bufferedReader.close();
	}
	
	public void readMatrixS(String filename) throws IOException {
		bufferedReader = new BufferedReader(new FileReader(new File(filename)));
		String line = bufferedReader.readLine();
		int i = 0;
		while(line != null) {
			String strings[] = line.split(" ");
			//System.out.println("Length is: " + strings.length);
			for(int j = 0;j < strings.length;j++) {
				S.set(i, j,Double.parseDouble(strings[j]));
			}
			line  = bufferedReader.readLine();
			i++;
		}
		bufferedReader.close();
	}
	
	public void readMatrixP(String filename) throws IOException {
		bufferedReader = new BufferedReader(new FileReader(new File(filename)));
		String line = bufferedReader.readLine();
		int i = 0;
		while(line != null) {
			String strings[] = line.split(" ");
			for(int j = 0;j < strings.length;j++) {
				P.set(i, j,Double.parseDouble(strings[j]));
			}
			line  = bufferedReader.readLine();
			i++;
		}
		bufferedReader.close();
	}
	
	public void check(String filename) throws IOException {
		bufferedReader = new BufferedReader(new FileReader(new File(filename)));
		String line = bufferedReader.readLine();
		String rate[] = line.split(" ");
		int size = rate.length;
		for(int i = 0;i < size;i++) {
			if(Float.parseFloat(rate[i])!= 0.0) {
				System.out.println("(" + i + ", " + rate[i]+")");
			}
		}
		bufferedReader.close();
	}
	
	public void initMatrix() {
		matrix = new double[n_user][m_movie];
		for(int i = 0;i < n_user ;i++)
			for(int j = 0;j < m_movie;j++) {
				matrix[i][j] = 0;
			}
	}
	
	public void readMatrix(String filename,int startIndex,int n_user) throws IOException {
		this.n_user = n_user;
		initMatrix();
		bufferedReader = new BufferedReader(new FileReader(new File(filename)));
		int count = 0;
		String line = bufferedReader.readLine();
		while(line != null && count < startIndex){
			count++;
			line = bufferedReader.readLine();
		}
		
		count = 1;
		while(line != null && count <= n_user){
			String rating[] = line.split(" ");
		//	System.out.println(rating.length);
			for(int i = 0;i < rating.length;i++) {
				if(Double.parseDouble(rating[i]) >= 4) {
					matrix[count - 1][i] = 1;
				}else if (Double.parseDouble(rating[i]) > 0){
					matrix[count - 1][i] = -1;
				}else {
					matrix[count - 1][i] = 0;
				}
			}
			count++;
			line = bufferedReader.readLine();
		}
		bufferedReader.close();
	}
	
	public Matrix readMatrixRating(String filename,int start,int end) throws IOException {
		//initMatrix();
		
		Scanner scanner = new Scanner(new File(filename));
		int count = 0;
		String line=scanner.nextLine();
		String x[]= line.split(" ");
		System.out.println(x.length);
		double matrix [][]= new double[end-start+1][x.length];
		while (count <= start){
			scanner.nextLine();
			count++;
		}
		
		for(int i=start;i<end;i++){
			String li=scanner.nextLine();
			x= li.split(" ");
			for(int j=0;j<x.length;j++)
				matrix[i-start][j]= Double.parseDouble(x[j]);
		}
		Matrix ma= new Basic2DMatrix(matrix);
		return ma;
		
	}
	
	public void checkMatrix(){
		for(int i = 0;i < n_user;i++) {
			int count = 0;
			for(int j = 0;j < m_movie;j++) {
				if(matrix[i][j] != 0){
					System.out.println("(" + i+", "+j+") = " + matrix[i][j]);
					count++;
				}
			}
			System.out.println("Count is : " + count);
		}
		
	}

	public static void main(String args[]) throws IOException {
		MatrixRatingMovie movie = new MatrixRatingMovie();

		//movie.readFileRating("ml-latest-small/ratings.csv");
		//movie.readFileMoives("Data/Movies_Title.txt");
		//movie.writeSparseMatrix("Data/Movie_ratings.txt");
		/*long time = System.currentTimeMillis();
		movie.readSparseMatrix("Data/Movie_ratings_1.txt");
		long endTime = System.currentTimeMillis();
		System.out.println("Time is: " + (endTime - time)/1000);*/
		movie.readMatrixS("S1.txt");
		movie.readMatrixP("D1.txt");
		//Vector b = movie.getS().getRow(0); 
		//System.out.println(b);
		//System.out.println("DONE!!");

	}
	
	
	
	public SparseMatrix getSparseMatrix() {
		return sparseMatrix;
	}
	public void setSparseMatrix(SparseMatrix sparseMatrix) {
		this.sparseMatrix = sparseMatrix;
	}
	public double[] getMatrix_user_movie() {
		return matrix_user_movie;
	}
	
	
	public int getK() {
		return k;
	}
	public void setK(int k) {
		this.k = k;
	}
	public SparseMatrix getP() {
		return P;
	}
	public void setP(SparseMatrix p) {
		P = p;
	}
	public SparseMatrix getS() {
		return S;
	}
	public void setS(SparseMatrix s) {
		S = s;
	}
	public void setMatrix_user_movie(double[] matrix_user_movie) {
		this.matrix_user_movie = matrix_user_movie;
	}
	public double[][] getMatrix() {
		return matrix;
	}
	public void setMatrix(double[][] matrix) {
		this.matrix = matrix;
	}
	public int getN_user() {
		return n_user;
	}

	public void setN_user(int n_user) {
		this.n_user = n_user;
	}

	public int getM_movie() {
		return m_movie;
	}

	public void setM_movie(int m_movie) {
		this.m_movie = m_movie;
	}

	public Map<Integer, ArrayList<Pair>> getUser_movies() {
		return user_movies;
	}

	public void setUser_movies(Map<Integer, ArrayList<Pair>> user_movies) {
		this.user_movies = user_movies;
	}

	/*public float[][] getMatrix_user_movie() {
		return matrix_user_movie;
	}

	public void setMatrix_user_movie(float[][] matrix_user_movie) {
		this.matrix_user_movie = matrix_user_movie;
	}*/

	
	
	public BufferedReader getBufferedReader() {
		return bufferedReader;
	}

	public Map<Integer, Integer> getMovieId_index() {
		return movieId_index;
	}
	public void setMovieId_index(Map<Integer, Integer> movieId_index) {
		this.movieId_index = movieId_index;
	}
	public void setBufferedReader(BufferedReader bufferedReader) {
		this.bufferedReader = bufferedReader;
	}

	public BufferedWriter getBufferedWriter() {
		return bufferedWriter;
	}

	public void setBufferedWriter(BufferedWriter bufferedWriter) {
		this.bufferedWriter = bufferedWriter;
	}

	
}
