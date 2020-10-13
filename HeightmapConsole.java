/**
 * @(#)Heightmap.java
 *
 *
 * @author 
 * @version 1.00 2018/4/16
 */
import java.io.*;
import java.util.*;

public class HeightmapConsole {
	
    /**
     * Creates a new instance of <code>Heightmap</code>.
     */
    static int[][] map; 
    static int size;
    static int scale; 
    static double volatility;
     
    public HeightmapConsole() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {	
    	System.out.println("Map will be of size (2^n+1,2^n+1).");
    	while(true){		
	        System.out.print("Enter dimension n: ");
	        Scanner scan = new Scanner(System.in);
	        int n = scan.nextInt();
	        System.out.print("Enter scale: ");
	        scale = scan.nextInt();
	        System.out.print("Enter volatility (0-1): ");
	        volatility = scan.nextDouble();
	        size = (int)(Math.pow(2,n))+1;
			map = new int[size][size];
			map[0][0] = (int)(Math.random()*scale);
			map[size-1][0] = (int)(Math.random()*scale);
			map[size-1][size-1] = (int)(Math.random()*scale);
			map[0][size-1] = (int)(Math.random()*scale);
			map = recursiveGen(map);
	        printMap(map);
	        System.out.println();
    	}
    }
    
    public static void printMap(int[][] map){
    	int largest = 0;
    	for(int r = 0; r < map.length; r++){
    		for(int c = 0; c < map.length; c++){
    			if (map[r][c] > largest){
    				largest = map[r][c];
    			}
    		}
    	}
    	int maxdigits = 1;
    	maxdigits = (int)(Math.log10(largest)+1);
    	for(int r = 0; r < map.length; r++){
    		for(int c = 0; c < map.length; c++){
    			int test = 1;
    			if (map[r][c] > 0){
    				test = (int)(Math.log10(map[r][c])+1);
    			}
    			System.out.print(map[r][c]);
    			while (test < maxdigits+1){
    				System.out.print(" ");
    				test++;
    			}
    		}
    		System.out.println();
    	}
    }
    
    public static int[][] matCopy(int[][] mat){
    	int[][] mat2 = new int[mat.length][mat.length];
    	for(int r = 0;r < mat.length; r++){
    		for(int c = 0;c < mat.length; c++){
    			mat2[r][c] = mat[r][c];
    		}
    	}
    	return mat2;
    }
    
    public static int[][] recursiveGen(int[][] map){
    	int length = map.length;
    	int[][] nmap = matCopy(map);
    	if(map.length > 2){
    		map[0][length/2] = (int)Math.abs(((map[0][0]+map[0][length-1])/2)+Math.random()*volatility*((map[0][0]+map[0][length-1])/2)*(Math.random()*2-1));
    		map[length/2][0] = (int)Math.abs(((map[0][0]+map[length-1][0])/2)+Math.random()*volatility*((map[0][0]+map[length-1][0])/2)*(Math.random()*2-1));
    		map[length-1][length/2] = (int)Math.abs(((map[length-1][0]+map[length-1][length-1])/2)+Math.random()*volatility*((map[length-1][0]+map[length-1][length-1])/2)*(Math.random()*2-1));
    		map[length/2][length-1] = (int)Math.abs(((map[0][length-1]+map[length-1][length-1])/2)+Math.random()*volatility*((map[0][length-1]+map[length-1][length-1])/2)*(Math.random()*2-1));
    		map[length/2][length/2] = (int)Math.abs(((map[0][0]+map[length-1][0]+map[0][length-1]+map[length-1][length-1])/4)+Math.random()*volatility*((map[0][0]+map[length-1][0]+map[0][length-1]+map[length-1][length-1])/4)*(Math.random()*2-1));
    		int[][] submap1 = new int[length/2+1][length/2+1];
    		int[][] submap2 = new int[length/2+1][length/2+1];
    		int[][] submap3 = new int[length/2+1][length/2+1];
    		int[][] submap4 = new int[length/2+1][length/2+1];
    		for(int r = 0; r <= length/2; r++){
    			for(int c = 0; c <= length/2; c++){
    				submap1[r][c] = map[r][c];
    			}
    		}
    		for(int r = length/2; r < length; r++){
    			for(int c = 0; c <= length/2; c++){
    				submap2[r-length/2][c] = map[r][c];
    			}
    		}
    		for(int r = 0; r <= length/2; r++){
    			for(int c = length/2; c < length; c++){
    				submap3[r][c-length/2] = map[r][c];
    			}
    		}
    		for(int r = length/2; r < length; r++){
    			for(int c = length/2; c < length; c++){
    				submap4[r-length/2][c-length/2] = map[r][c];
    			}
    		}
    		int[][][] submaps = new int[4][length/2+1][length/2+1];
    		submaps[0] = recursiveGen(submap1);
    		submaps[1] = recursiveGen(submap2);
    		submaps[2] = recursiveGen(submap3);
    		submaps[3] = recursiveGen(submap4);
    		return (stitchSubmaps(submaps));
    	} else {
    		return map;
    	}
    }
    
    public static int[][] stitchSubmaps(int[][][] submaps){
    	int sublength = submaps[0].length;
    	int length = (sublength-1)*2+1;
    	int[][] map = new int[length][length];
    	for(int r = 0; r < length; r++){
    		for(int c = 0; c < length; c++){
    			if ((r < length/2) && (c < length/2)){
    				map[r][c] = submaps[0][r][c];
    			}
    			if ((r >= length/2) && (c < length/2)){
    				map[r][c] = submaps[1][r-length/2][c];
    			}
    			if ((r < length/2) && (c >= length/2)){
    				map[r][c] = submaps[2][r][c-length/2];
    			}
    			if ((r >= length/2) && (c >= length/2)){
    				map[r][c] = submaps[3][r-length/2][c-length/2];
    			}
    		}
    	}
    	return map;
    }
}
