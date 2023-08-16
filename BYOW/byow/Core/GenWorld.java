package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.algs4.StdDraw;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import edu.princeton.cs.algs4.QuickUnionUF;

public class GenWorld {
    private TETile[][] tiles;
    public int keys = 0;
    public int width;
    public int height;
    private Random RANDOM;
    public Room[] rooms;
    private int[][] directions = new int[][]{{-1,-1}, {-1,0}, {-1,1},  {0,1}, {1,1},  {1,0},  {1,-1},  {0, -1}};
    public TERenderer ter = null;
    public long seed;
    public boolean is_Dark = true;
    public int mouseX;
    public int mouseY;
    public QuickUnionUF UF;
    public ArrayList <Integer> deleted_Keys = new ArrayList<>();
    public ArrayList <Integer> unlocked_Doors = new ArrayList<>();
    public int num_Of_Unlocked_Doors = 0;



    private class Room {
        private int numba;
        private int Tx;
        private int Ty;
        private int Bx;
        private int By;
        private int xkey;
        private int ykey;
        private Room (int a, int b, int c, int d) {
            Tx = a;
            Ty = b;
            Bx = c;
            By = d;
            xkey = RANDOM.nextInt(Bx - Tx - 1) + Tx;
            ykey = RANDOM.nextInt(Ty - By - 1) + By;
        }
    }
    public void setNumba(Room a, int i) {
        a.numba =  i;
    }
    public double distance(Room a, Room b) {
        int x = a.Tx - b.Tx;
        int y = a.Ty - b.Ty;
        return Math.sqrt((x * x) + (y * y));
    }
    public Room closest(Room a) {
        double smallest = width * height;
        Room small = a;
        for (Room x : rooms) {
            if (UF.connected(a.numba,x.numba)) {
                continue;
            }
            if (distance(a,x) < smallest && distance(a,x) > 0) {
                smallest = distance(a, x);
                small = x;
            }
        }
        return small;
    }
    public Room furthest(Room a) {
        double big = 0;
        Room bigg = a;
        for (Room x : rooms) {
            if (distance(a,x) > big) {
                big = distance(a, x);
                bigg = x;
            }
        }
        return bigg;
    }
    public Room findSpawnRoom(int x, int y) {
        for (Room a : rooms) {
            if (a.Tx <= x && a.Bx >= x && a.Ty >= y && a.By <= y) {
                return a;
            }
        }
        return null;
    }
    public int[] findSpawnCord() {
        int startx = 0;
        int starty = 0;
        int [] cord = new int[2];
        boolean stop = false;
        for (int i = 0; i < width && !stop; i++) {
            for (int j = 0; j < height && !stop; j++) {
                if (getTileName(getTiles(), i, j).equals("floor")) {
                    startx = i;
                    starty = j;
                    stop = true;
                    cord[0] = i;
                    cord[1] = j;
                    return cord;
                }
            }
        }
        return cord;
    }
    public void connectedCross(int x, int y, Room b) {
        for (Room a : rooms) {
            if (a.Tx <= x && a.Bx >= x && a.Ty >= y && a.By <= y) {
                UF.union(b.numba,a.numba);
            }
        }
    }
    public boolean overlap(Room a, Room b) {
        int gap = 4 + (width*height/3000);
        if (b == null) {
            return false;
        }
        if (a.Tx == a.Bx || a.Ty == a.By || b.Tx == b.Bx || b.Ty == b.By) {
            return false;
        }
        if ((a.Tx - b.Bx) >= gap || (b.Tx - a.Bx) >= gap) {
            return false;
        }
        if ((a.By - b.Ty) >= gap || (b.By - a.Ty) >= gap) {
            return false;
        }
        return true;
    }

    public GenWorld (int width, int height, long seed) {
        if ((width < 20 || height < 20)) {
            System.out.println("Your Height/Width is too small!");
            System.exit(0);
        }
        this.seed = seed;
        RANDOM = new Random(seed);
        this.width = width;
        this.height = height;
        tiles =  new TETile[width][height];
        for (int j = 0; j < width; j ++) {
            for (int i = 0; i < height; i++){
                this.tiles[j][i] = Tileset.NOTHING;
            }
        }
        makeRoom();
        drawLine();
        for (int i = 0; i < rooms.length - 1; i++) {
            int x = RANDOM.nextInt(2);
            if (x == 0) {
                connectRoomInXFirst(rooms[i],closest(rooms[i]));
                //UF.union(rooms[i].numba,closest(rooms[i]).numba);
            } else {
                connectRoomInYFirst(rooms[i],closest(rooms[i]));
                //UF.union(rooms[i].numba,closest(rooms[i]).numba);
            }
        }
        Room spawn = findSpawnRoom(findSpawnCord()[0],findSpawnCord()[1]);
        Room f = furthest(spawn);
        for (Room x : rooms) {
            if (UF.find(x.numba) != spawn.numba) { // i chose the 0th room in rooms to make sure they are all connected
                connectRoomInXFirst(x, closest(x)); // would work best if it is the room that GUY spawns in
                if (x != spawn) {
                    this.tiles[x.xkey][x.ykey] = Tileset.TREE;
                    if (x == f) {
                        this.tiles[x.xkey][x.ykey] = Tileset.CROWN;
                    }

                }
            }
        }
        makeWalls();
        for (int x = f.Tx - 1; x < f.Bx + 1; x++) {
            if (this.tiles[x][f.Ty + 1] == Tileset.FLOWER) {
                this.tiles[x][f.Ty + 1] = Tileset.LOCKED_DOOR;
            }
            if (this.tiles[x][f.By - 1] == Tileset.FLOWER) {
                this.tiles[x][f.By - 1] = Tileset.LOCKED_DOOR;
            }
        }
        for (int y = f.By - 1; y < f.Ty + 1; y++) {
            if (this.tiles[f.Bx + 1][y] == Tileset.FLOWER) {
                this.tiles[f.Bx + 1][y] = Tileset.LOCKED_DOOR;
            }
            if (this.tiles[f.Tx -1][y] == Tileset.FLOWER) {
                this.tiles[f.Tx -1][y] = Tileset.LOCKED_DOOR;
            }
        }
    }
    public void connectRoomInYFirst (Room a, Room b) {
        if (a == null || b == null) {
            return;
        }
        Room lowerRoom;
        Room upperRoom;
        if (a.Ty > b.Ty) {
            lowerRoom = b;
            upperRoom = a;
        } else {
            lowerRoom = a;
            upperRoom = b;
        }
        int startx = (lowerRoom.Bx + lowerRoom.Tx) / 2;
        int endy = (upperRoom.Ty+upperRoom.By) / 2;
        for (int i = lowerRoom.Ty; i < endy + 1; i++) {
            this.tiles[startx][i] = Tileset.FLOWER;
            connectedCross(startx,i, lowerRoom);
        }
//        if (startx == upperRoom.Tx) {
//            return;
//        }
        if (startx > upperRoom.Bx) {
            for (int i = upperRoom.Bx; i < startx;i++) {
                this.tiles[i][endy] = Tileset.FLOWER;
                connectedCross(i,endy, lowerRoom);
            }
        } else {
            for (int i = startx; i < upperRoom.Tx;i++) {
                this.tiles[i][endy] = Tileset.FLOWER;
                connectedCross(i,endy, lowerRoom);
            }
        }
    }
    public void connectRoomInXFirst (Room a, Room b) {
        if (a == null || b == null) {
            return;
        }
        Room leftRoom;
        Room rightRoom;
        if (b.Tx > a.Tx) {
            leftRoom = a;
            rightRoom = b;
        } else {
            leftRoom = b;
            rightRoom = a;
        }
        int starty = (leftRoom.By+leftRoom.Ty)/2;
        int endx = (rightRoom.Tx+rightRoom.Bx)/2;
        for (int i = leftRoom.Bx; i < endx + 1;i++) {
            this.tiles[i][starty] = Tileset.FLOWER;
            connectedCross(i,starty, leftRoom);
        }
//        if (starty == .Ty) {
//            return;
//        }
        if (starty > rightRoom.Ty) {
            for (int i = rightRoom.Ty; i < starty + 1; i++) {
                this.tiles[endx][i] = Tileset.FLOWER;
                connectedCross(endx,i, leftRoom);
            }
        } else {
            for (int i = starty; i < rightRoom.By + 1; i++) {
                this.tiles[endx][i] = Tileset.FLOWER;
                connectedCross(endx,i, leftRoom);
            }
        }

    }

    public TETile[][] getTiles() {
        return this.tiles;
    }
    public void makeWalls(){
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (isNothing(tiles[i][j]) && isNextToFloor(i, j)) {
                    tiles[i][j] = Tileset.WALL;
                }
            }
        }
    }
    public boolean isNextToFloor(int x, int y) {
        for (int[] direction : directions) {
            int cx = x + direction[0];
            int cy = y + direction[1];
            if(cy >= 0 && cy < height) {
                if(cx >= 0 && cx < width) {
                    if (tiles[cx][cy] == Tileset.FLOWER || tiles[cx][cy] == Tileset.TREE) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    public boolean isNothing(TETile x) {
        return x == Tileset.NOTHING;
    }
    public void makeRoom() {
        int numRooms = (int) Math.round((width * height) * 0.0065);
        UF = new QuickUnionUF(numRooms);
        numRooms = RANDOM.nextInt(numRooms / 2 + 1) + numRooms / 2;
        rooms = new Room[numRooms];
        int count = 0;
        while (count < numRooms) {
            boolean overlapped = false;
            int len = RANDOM.nextInt(4) + 3;
            int wid = RANDOM.nextInt(4) + 3;
            int topLeftx = RANDOM.nextInt(width - 2 - wid) + 1;
            int topLefty = RANDOM.nextInt(height - 2 - len) + len + 1;
            int botRightx = topLeftx + wid;
            int botRighty = topLefty - len;
            Room r = new Room(topLeftx, topLefty, botRightx, botRighty);
            for (Room x : rooms) {
                if (overlap(r, x)) {
                    overlapped = true;
                }
            }
            if (!overlapped) {
                rooms[count] = r;
                setNumba(r, count);
                //printRoom(r);
                count ++;
            }
        }
    }
    public void printRoom(Room r) {
        System.out.println("["+r.Tx+","+r.Ty+"]"+" "+"["+r.Bx+","+r.By+"]");
    }
    public void drawLine() {
        for (Room x : rooms) {
            for (int i = x.Tx; i < x.Bx; i++) {
                for (int j = x.Ty; j > x.By; j--) {
                    //System.out.println(i + " " + j);
                    this.tiles[i][j] = Tileset.FLOWER;
                }
            }
        }

    }
    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        System.out.print("Enter a seed: ");
        Engine x = new Engine();
        x.interactWithInputString(s.nextLine());
        System.exit(0);
    }
    public void start() {
        this.ter = new TERenderer();
        this.ter.initialize(width, height + 2);
        this.ter.renderFrame(this.tiles);
    }
    public void display(Guy mainGuy) {
        TETile[][] displayPanel = TETile.copyOf(this.tiles);
        if (tiles[mainGuy.curr.locX][mainGuy.curr.locY]== Tileset.TREE){
            tiles[mainGuy.curr.locX][mainGuy.curr.locY] = Tileset.FLOWER;
            keys ++;
            deleted_Keys.add(mainGuy.curr.locX);
            deleted_Keys.add(mainGuy.curr.locY);
        }
        if (tiles[mainGuy.curr.locX][mainGuy.curr.locY]== Tileset.LOCKED_DOOR && keys >= 5){
            tiles[mainGuy.curr.locX][mainGuy.curr.locY] = Tileset.UNLOCKED_DOOR;
            keys -= 5;
            unlocked_Doors.add(mainGuy.curr.locX);
            unlocked_Doors.add(mainGuy.curr.locY);
            num_Of_Unlocked_Doors += 1;
        }
        if (tiles[mainGuy.curr.locX][mainGuy.curr.locY]== Tileset.CROWN) {
            tiles[mainGuy.curr.locX][mainGuy.curr.locY] = Tileset.FLOWER;
            Menu x = new Menu(width, height);
            x.drawWinScreen();
        }
        displayPanel[mainGuy.curr.locX][mainGuy.curr.locY] = mainGuy.currTile;
        //to add more unique blocks update the display panel
        if (is_Dark) {
            displayPanel = applyDarkness(displayPanel, mainGuy);
        }
        String name = getTileName(displayPanel, mouseX,mouseY);
        String numkey = getKeys(keys);
        String time = "Date: " + java.time.LocalDate.now() + " | " + "Time: " + java.time.LocalTime.now().toString();
        time = time.substring(0, time.length()-4);
        this.ter.renderFrame(displayPanel);
        Font fontSmall = new Font("Monaco", Font.BOLD, (this.width + this.height) / 7);
        StdDraw.textRight(this.width - 1, this.height + 1, time);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.setFont(fontSmall);
        StdDraw.textRight(this.width - 1, this.height + 1, time);
        StdDraw.textLeft(1, this.height + 1, name);
        StdDraw.textLeft(20, this.height + 1, numkey);
        StdDraw.show();
    }
    public String getKeys(int count) {
        String numOfKey = "Number of keys needed: " + count + "/" + "5";
        return  numOfKey;
    }
    public String getTileName (TETile[][] temp, int x, int y) {
        String name;
        if (x >= temp.length || y >= temp[0].length) {
            name = "Out of Bounds";
            return name;
        } else {
            name = temp[x][y].description();
            return name;
        }
    }
    public Long getSeed () {
        return this.seed;
    }
    public void flipDarkMode() {
        is_Dark = !is_Dark;
    }
    public TETile[][] applyDarkness (TETile[][] displayPanel, Guy mainGuy) {
        int x = mainGuy.curr.locX;
        int y = mainGuy.curr.locY;
        for (int i = 0; i < displayPanel.length; i++) {
            for (int j = 0; j < displayPanel[0].length; j++ ) {
                long distance = Math.round(Math.sqrt((x - i) * (x - i) + (y - j) * (y - j)));
                if (distance > 3) {
                    displayPanel[i][j] = Tileset.NOTHING;
                }
            }
        }
        return displayPanel;
    }
    public void changeToFloor(int x, int y) {
        tiles[x][y] = Tileset.FLOWER;
    }
    public void changeToUnlockedDoor(int x, int y) {
        tiles[x][y] = Tileset.UNLOCKED_DOOR;
    }
}