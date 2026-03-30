package byow.lab12;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld {
    private static final int WIDTH = 50;
    private static final int HEIGHT = 50;
    private static final int S = 4;
    private static final int[] START = {(WIDTH - S - 2)/2, HEIGHT};

    public static TERenderer initialTERender(int width, int height){
        TERenderer ter = new TERenderer();
        ter.initialize(width,height);
        return ter;
    }

    public static TETile[][] initialBoard(int width, int height){
        TETile[][] board = new TETile[width][height];
        for(int x = 0; x < width; x++){
            for(int y = 0; y < height; y++){
                board[x][y] = Tileset.NOTHING;
            }
        }
        return board;
    }

    public static ArrayList<int[]> allPosition(int width, int height,int s){
        int tesselationWidth =  11 * s -6;
        int tesselationHeight = 10 * s;
        int wideBlank = (width - tesselationWidth) / 2;
        int heighBlank = (height - tesselationHeight) / 2;
        int[] start = new int[]{wideBlank + s, heighBlank + 2 * s};

        ArrayList<int[]> pos = new ArrayList<>();
        pos.add(start);
        pos.add(new int[]{start[0]+2*s-1,start[1]-s});
        pos.add(new int[]{start[0]+4*s-2,start[1]-2*s});
        pos.add(new int[]{start[0]+(2*s-1)*3,start[1]-s});
        pos.add(new int[]{start[0]+(2*s-1)*4,start[1]});
        for(int i = 0; i<5; i++){
            int index = i;
            int[] cur = Arrays.copyOf(pos.get(index),2);
            int num = 0;
            if(i>2) {
                if (i == 3) index = 1;
                if (i == 4) index = 0;
            }
            while(num != 2+index){
                pos.add(new int[]{cur[0],cur[1] + 2*s*(num+1)});
                num += 1;
            }
        }
        return pos;
    }

    public static TETile randomTETile(){
        Random re = new Random();
        int x = re.nextInt(5);
        switch (x){
            case 0 : return Tileset.FLOWER;
            case 1 : return Tileset.WALL;
            case 2 : return Tileset.FLOOR;
            case 3 : return Tileset.GRASS;
            case 4 : return Tileset.TREE;
        }
        return null;
    }
    /**
     * 在世界给定位置处绘制一个边长为s的六边形，确保六边形必须有两排厚的中间层，这是六边形进行嵌套的关键。
     * @param s 六边形的边长。
     * @param position 六边形的位置。
     * @param thing 六边形内部填充的位置。
     * @param board 画板。
     */
    public static TETile[][] addHexagon(int s, int[]position, TETile thing, TETile[][] board){
        int gap = 2*s;
        for(int y = position[1], yTimes = 0; yTimes != s; y++, yTimes++){
            for(int x = (position[0] - yTimes), xTimes = s + yTimes*2; xTimes != 0;x++, xTimes--){
                board[x][y] = thing;
                board[x][y + gap - 1] = thing;
            }
            gap -= 2;
        }
        return board;
    }

    public static void main(String[] args){
        TERenderer ter = initialTERender(WIDTH,HEIGHT);
        TETile[][] board = initialBoard(WIDTH,HEIGHT);

        ArrayList<int[]> allPos = allPosition(WIDTH,HEIGHT,S);
        for(int[] pos : allPos){
            addHexagon(S,pos,randomTETile(),board);
        }

        ter.renderFrame(board);
    }
}
