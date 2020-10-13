/**
 * @(#)Heightmap.java
 *
 *
 * @author 
 * @version 1.00 2018/4/16
 */
import java.io.*;
import java.util.*;

public class Heightmap {
	
    /**
     * Creates a new instance of <code>Heightmap</code>.
     */
    private int [][] map;
    int scale;
     
    public Heightmap() {
    	map = new int[1][1];
		map[0][0] = 0;
    }
    
    public Heightmap(int size,int scale,double volatility) {
    	map = new int[size][size];
    	this.scale = scale;
		map[0][0] = (int)((1-Math.sqrt(Math.random()))*scale*.5);
		map[size-1][0] = (int)((1-Math.sqrt(Math.random()))*scale*.5);
		map[size-1][size-1] = (int)((1-Math.sqrt(Math.random()))*scale*.5);
		map[0][size-1] = (int)((1-Math.sqrt(Math.random()))*scale*.5);
		map = recursiveGen(map, volatility, map.length-1,false);
    }
    
    /**
     * @param args the command line arguments
     */
    
    private int max(int a,int b,int c,int d){
    	return Math.max(a,Math.max(b,Math.max(c,d)));
    }
    
    private int min(int a,int b,int c,int d){
    	return Math.min(a,Math.min(b,Math.min(c,d)));
    }
    
    private int max(int a,int b,int c){
    	return Math.max(a,Math.max(b,c));
    }
    
    private int min(int a,int b,int c){
    	return Math.min(a,Math.min(b,c));
    }
    
    
    //1 0 0 0 1 0 0 0 1
    //0 0 0 0 0 0 0 0 0
    //0 0 1 0 0 0 1 0 0
    //0 0 0 0 0 0 0 0 0
    //1 0 0 0 1 0 0 0 1
    //0 0 0 0 0 0 0 0 0
    //0 0 1 0 0 0 1 0 0
    //0 0 0 0 0 0 0 0 0
    //1 0 0 0 1 0 0 0 1
    
    private int[][] recursiveGen(int[][] map, double volatility, int level, boolean printing){
    	if (level > 1){
    		for(int x = level/2;x < map.length;x+=level){
    			for(int y = level/2;y < map.length;y+=level){
    				map[x][y] = (int)(((map[x-level/2][y-level/2]+map[x+level/2][y-level/2]+map[x+level/2][y+level/2]+map[x-level/2][y+level/2])/4)+Math.random()*volatility*(Math.random()*2-1)*
    					(max(map[x-level/2][y-level/2],map[x+level/2][y-level/2],map[x+level/2][y+level/2],map[x-level/2][y+level/2])-min(map[x-level/2][y-level/2],map[x+level/2][y-level/2],map[x+level/2][y+level/2],map[x-level/2][y+level/2])));
    				if (map[x][y] > scale){
    					map[x][y] = scale;
    				}
    				else if (map[x][y] < 0){
    					map[x][y] = 0;
    				}
    			}
    		}
    		if (printing){
    			printMap(map);
    		}
    		for(int x = 0;x < map.length;x+=level){
    			for(int y = level/2;y < map.length;y+=level){
    				if (x-level/2 < 0){
    					map[x][y] = (int)(((map[x+level/2][y]+map[x][y+level/2]+map[x][y-level/2])/3)+Math.random()*volatility*(Math.random()*2-1)*
    						(max(map[x+level/2][y],map[x][y+level/2],map[x][y-level/2])-min(map[x+level/2][y],map[x][y+level/2],map[x][y-level/2])));
    				}
    				else if (x+level/2 >= map.length){
    					map[x][y] = (int)(((map[x-level/2][y]+map[x][y+level/2]+map[x][y-level/2])/3)+Math.random()*volatility*(Math.random()*2-1)*
    						(max(map[x-level/2][y],map[x][y+level/2],map[x][y-level/2])-min(map[x-level/2][y],map[x][y+level/2],map[x][y-level/2])));	
    				}
    				else if (y-level/2 < 0){
    					map[x][y] = (int)(((map[x-level/2][y]+map[x+level/2][y]+map[x][y+level/2])/3)+Math.random()*volatility*(Math.random()*2-1)*
    						(max(map[x-level/2][y],map[x+level/2][y],map[x][y+level/2])-min(map[x-level/2][y],map[x+level/2][y],map[x][y+level/2])));	
    				}
    				else if (y+level/2 >= map.length){
    					map[x][y] = (int)(((map[x-level/2][y]+map[x+level/2][y]+map[x][y-level/2])/3)+Math.random()*volatility*(Math.random()*2-1)*
    						(max(map[x-level/2][y],map[x+level/2][y],map[x][y-level/2])-min(map[x-level/2][y],map[x+level/2][y],map[x][y-level/2])));	
    				}
    				else {
    					map[x][y] = (int)(((map[x-level/2][y]+map[x+level/2][y]+map[x][y+level/2]+map[x][y-level/2])/4)+Math.random()*volatility*(Math.random()*2-1)*
    						(max(map[x-level/2][y],map[x+level/2][y],map[x][y+level/2],map[x][y-level/2])-min(map[x-level/2][y],map[x+level/2][y],map[x][y+level/2],map[x][y-level/2])));	
    				}
    				if (map[x][y] > scale){
    					map[x][y] = scale;
    				}
    				else if (map[x][y] < 0){
    					map[x][y] = 0;
    				}
    			}
    		}
    		for(int x = level/2;x < map.length;x+=level){
    			for(int y = 0;y < map.length;y+=level){
    				if (x-level/2 < 0){
    					map[x][y] = (int)(((map[x+level/2][y]+map[x][y+level/2]+map[x][y-level/2])/3)+Math.random()*volatility*(Math.random()*2-1)*
    						(max(map[x+level/2][y],map[x][y+level/2],map[x][y-level/2])-min(map[x+level/2][y],map[x][y+level/2],map[x][y-level/2])));	
    				}
    				else if (x+level/2 >= map.length){
    					map[x][y] = (int)(((map[x-level/2][y]+map[x][y+level/2]+map[x][y-level/2])/3)+Math.random()*volatility*(Math.random()*2-1)*
    						(max(map[x-level/2][y],map[x][y+level/2],map[x][y-level/2])-min(map[x-level/2][y],map[x][y+level/2],map[x][y-level/2])));
    				}
    				else if (y-level/2 < 0){
    					map[x][y] = (int)(((map[x-level/2][y]+map[x+level/2][y]+map[x][y+level/2])/3)+Math.random()*volatility*(Math.random()*2-1)*
    						(max(map[x-level/2][y],map[x+level/2][y],map[x][y+level/2])-min(map[x-level/2][y],map[x+level/2][y],map[x][y+level/2])));	
    				}
    				else if (y+level/2 >= map.length){
    					map[x][y] = (int)(((map[x-level/2][y]+map[x+level/2][y]+map[x][y-level/2])/3)+Math.random()*volatility*(Math.random()*2-1)*
    						(max(map[x-level/2][y],map[x+level/2][y],map[x][y-level/2])-min(map[x-level/2][y],map[x+level/2][y],map[x][y-level/2])));
    				}
    				else {
    					map[x][y] = (int)(((map[x-level/2][y]+map[x+level/2][y]+map[x][y+level/2]+map[x][y-level/2])/4)+Math.random()*volatility*(Math.random()*2-1)*
    						(max(map[x-level/2][y],map[x+level/2][y],map[x][y+level/2],map[x][y-level/2])-min(map[x-level/2][y],map[x+level/2][y],map[x][y+level/2],map[x][y-level/2])));
    				}
    				if (map[x][y] > scale){
    					map[x][y] = scale;
    				}
    				else if (map[x][y] < 0){
    					map[x][y] = 0;
    				}
    			}
    		}
    		if (printing){
	    		System.out.println();
	    		printMap(map);
	    		System.out.println();
    		}
    		return recursiveGen(map,volatility,level/2,printing);
    	}
    	return map;
    }
    
    public int[][] getMap(){
    	return map;
    }
    
    public void printMap(int[][] map){
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
}
