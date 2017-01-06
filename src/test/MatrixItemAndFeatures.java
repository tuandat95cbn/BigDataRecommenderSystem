package test;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
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
import java.util.Set;

public class MatrixItemAndFeatures {
	
	private BufferedReader bufferedReader;
	private BufferedWriter bufferedWriter;
	private Map<String, Integer> listFeature;
	private Map<Integer, ArrayList<Integer>> listFilmOfFeature;
	private ArrayList<String> listMovie;
	private ArrayList<Integer> listMoiveId;
	private double[][] matrixItemAndFeatures;
	// n,m la tuong ung voi hang va cot
	// n tuong ung voi item 
	// m tuong ung voi feature
	private int n = 27278,m = 20;
	/*private int n = 9125,m = 20;*/
	
	public MatrixItemAndFeatures(){
		listFeature = new HashMap<String, Integer>();
		listFilmOfFeature = new HashMap<>();
		listMovie = new ArrayList<String>();
		listMoiveId = new ArrayList<Integer>();
		matrixItemAndFeatures = new double[n][m];
		for(int i = 0;i < n;i++){
			for(int j = 0;j < m;j++) {
				matrixItemAndFeatures[i][j] = 0;
			}
		}
	}
	
	public String[] checkStrings(String[] strings){
		String a[] = new String[3];
		if(strings.length == 3){
			return strings;
		}
		if(strings.length > 3){
			a[0] = strings[0];
			StringBuilder string = new StringBuilder();
			for(int i = 1;i < strings.length - 1;i++){
				string.append(strings[i]);
				if(i < strings.length - 2) {
					string.append(",");
				}
			}
			a[1] = string.toString();
			a[2] = strings[strings.length - 1];
			return a;
		}
		return null;
	}
	
	public void sortFeatureMap(){
		List<Map.Entry<String, Integer>> list =  new LinkedList<>(listFeature.entrySet());
		Collections.sort(list,new Comparator<Map.Entry<String, Integer>>() {
			@Override
			public int compare(Entry<String, Integer> o1,
					Entry<String, Integer> o2) {
				// TODO Auto-generated method stub
				return (o1.getValue()).compareTo(o2.getValue());
			}
		});
		listFeature.clear();
		listFeature = new LinkedHashMap<>();
		for (Map.Entry<String, Integer> entry : list) {
            listFeature.put(entry.getKey(), entry.getValue());
	    }
	}
	
	public void sortIdMovie() {
		List<Map.Entry<Integer, ArrayList<Integer>>> list =  new LinkedList<>(listFilmOfFeature.entrySet());
		Collections.sort(list,new Comparator<Map.Entry<Integer,
				ArrayList<Integer>>>() {
			@Override
			public int compare(Entry<Integer, ArrayList<Integer>> o1,
					Entry<Integer, ArrayList<Integer>> o2) {
				// TODO Auto-generated method stub
				return (o1.getKey()).compareTo(o2.getKey());
			}
		});
		listFilmOfFeature.clear();
		listFilmOfFeature = new LinkedHashMap<>();
		for (Map.Entry<Integer, ArrayList<Integer>> entry : list) {
            listFilmOfFeature.put(entry.getKey(), entry.getValue());
	    }
	}
	
	public void readFileMovie(String filename) throws IOException {
		bufferedReader = new BufferedReader(new FileReader(new File(filename)));
		String line = bufferedReader.readLine();
		line = bufferedReader.readLine();
		int count = 0;
		while(line != null) {
			String line_one[] = checkStrings(line.split(","));
			//System.out.println("Id is: " + line_one[0]);
			listMoiveId.add(Integer.parseInt(line_one[0]));
			listMovie.add(line_one[1]);
			ArrayList<Integer> list = new ArrayList<>();
			String features[] = line_one[2].split("\\|");
			for(int i = 0;i < features.length;i++) {
				if(!listFeature.containsKey(features[i])){
					listFeature.put(features[i], count);
					count++;
				}
				list.add(listFeature.get(features[i]));
			}
			listFilmOfFeature.put(Integer.parseInt(line_one[0]),list);
			line = bufferedReader.readLine();
		}
		bufferedReader.close();
	}
	
	public void initMatrixMovieFeature() {
		sortIdMovie();
		Set<Map.Entry<Integer, ArrayList<Integer>>> list = listFilmOfFeature.entrySet();
		int i = 0;
		for(Map.Entry<Integer, ArrayList<Integer>> matrix:list){
			ArrayList<Integer> list_features_of_movie = matrix.getValue();
			int size = list_features_of_movie.size();
			for(int j = 0;j < size;j++) {
				matrixItemAndFeatures[i][j] = 1;
			}
			i++;
		}
	}
	
	public void writeMovie(String filename) throws IOException {
		bufferedWriter = new BufferedWriter(new FileWriter(new File(filename)));
		StringBuilder strings = new StringBuilder();
		sortIdMovie();
		Set<Entry<Integer, ArrayList<Integer>>> set = listFilmOfFeature.entrySet();
		for(Entry<Integer, ArrayList<Integer>> moive:set) {
			strings.append(moive.getKey());
			strings.append("\t");
			ArrayList<Integer> list = moive.getValue();
			int size = list.size();
			for(int i = 0;i < size;i++) {
				strings.append(list.get(i));
				if(i < size - 1){
					strings.append(",");
				}
			}
			strings.append("\n");
			bufferedWriter.write(strings.toString());
			strings.delete(0, strings.length());
		}
		bufferedWriter.flush();
		bufferedWriter.close();
	}
	
	public void writeFeatureOfMovie(String filename) throws IOException {
		bufferedWriter = new BufferedWriter(new FileWriter(new File(filename)));
		StringBuilder strings = new StringBuilder();
		sortFeatureMap();
		Set<Entry<String, Integer>> set = listFeature.entrySet();
		for(Entry<String, Integer> feature:set) {
			strings.append(feature.getValue());
			strings.append("\t");
			strings.append(feature.getKey());
			strings.append("\n");
			bufferedWriter.write(strings.toString());
			strings.delete(0, strings.length());
		}
		bufferedWriter.flush();
		bufferedWriter.close();
	}
	
	public void writeMoiveAndId(String filename) throws IOException {
		bufferedWriter = new BufferedWriter(new FileWriter(new File(filename)));
		StringBuilder strings = new StringBuilder();
		int count = 0;
		for(String title:listMovie) {
			strings.append(listMoiveId.get(count));
			strings.append("\t");
			strings.append(title);
			strings.append("\n");
			bufferedWriter.write(strings.toString());
			strings.delete(0, strings.length());
			count++;
		}
		bufferedWriter.flush();
		bufferedWriter.close();
	}
	
	public void writeMatrix(String filename) throws IOException {
		bufferedWriter = new BufferedWriter(new FileWriter(new File(filename)));
		StringBuilder strings = new StringBuilder();
		for(int i = 0;i < n;i++) {
			for(int j = 0;j < m;j++) {
				strings.append(matrixItemAndFeatures[i][j]);
				strings.append(" ");
			}
			strings.append("\n");
			bufferedWriter.write(strings.toString());
			strings.delete(0, strings.length());
		}
		bufferedWriter.flush();
		bufferedWriter.close();
	}
	
	public BufferedReader getBufferedReader() {
		return bufferedReader;
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



	public Map<String, Integer> getListFeature() {
		return listFeature;
	}



	public void setListFeature(Map<String, Integer> listFeature) {
		this.listFeature = listFeature;
	}



	public Map<Integer, ArrayList<Integer>> getListFilmOfFeature() {
		return listFilmOfFeature;
	}



	public void setListFilmOfFeature(
			Map<Integer, ArrayList<Integer>> listFilmOfFeature) {
		this.listFilmOfFeature = listFilmOfFeature;
	}



	public ArrayList<String> getListMovie() {
		return listMovie;
	}



	public void setListMovie(ArrayList<String> listMovie) {
		this.listMovie = listMovie;
	}



	public double[][] getMatrixItemAndFeatures() {
		return matrixItemAndFeatures;
	}



	public void setMatrixItemAndFeatures(double[][] matrixItemAndFeatures) {
		this.matrixItemAndFeatures = matrixItemAndFeatures;
	}



	public int getN() {
		return n;
	}



	public void setN(int n) {
		this.n = n;
	}



	public int getM() {
		return m;
	}



	public void setM(int m) {
		this.m = m;
	}



	public static void main(String args[]) throws IOException {
		/*Map<String,Integer> listFeature = new HashMap<>();
		Map<Integer,ArrayList<Integer>> listFilmOfFeature = new HashMap<Integer, ArrayList<Integer>>();
		String line = "1,Toy Story (1995),Adventure|Animation|Children|Comedy|Fantasy";
		String line_one[] = line.split(",");
		System.out.println(line_one.length);
		int count = 0;
		ArrayList<Integer> list = new ArrayList<>();
		String features[] = line_one[2].split("\\|");
		System.out.println(features.length);
		for(int i = 0;i < features.length;i++) {
			if(!listFeature.containsKey(features[i])){
				listFeature.put(features[i], count);
				count++;
			}
			list.add(listFeature.get(features[i]));
		}
		listFilmOfFeature.put(Integer.parseInt(line_one[0]),list);
		System.out.println(listFeature);
		System.out.println(listFilmOfFeature);*/
		MatrixItemAndFeatures matrix = new MatrixItemAndFeatures();
		matrix.readFileMovie("ml-latest-small/movies.csv");
		matrix.writeFeatureOfMovie("Sdata/Features.txt");
		matrix.writeMovie("Sdata/Movie_Features.txt");
		matrix.writeMoiveAndId("Sdata/Movies_Title.txt");
		//matrix.initMatrixMovieFeature();
		matrix.writeMatrix("Data/Matrix_Content.txt");
		System.out.println("DONE!");
	}
	
}
