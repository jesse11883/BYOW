package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.HashMap;

public class Guy{
    public HashMap<Character, int[]> directions = new HashMap<>();
    public GenWorld genWorld;
    public location curr;
    public TETile currTile;
    public class location {
        int locX;
        int locY;
        public location (int x, int y) {
            this.locX = x;
            this.locY = y;
        }
    }
    public Guy (int x, int y, GenWorld genWorld) {
        this.curr = new location(x, y);
        this.genWorld = genWorld;
        this.currTile = Tileset.AVATAR;
        makeDirections();
        //System.out.println("startx: " + x + " starty: " + y);
    }
    public void makeDirections() {
        directions.put('W', new int[]{0, 1});
        directions.put('w', new int[]{0, 1});
        directions.put('S', new int[]{0, -1});
        directions.put('s', new int[]{0, -1});
        directions.put('A', new int[]{-1, 0});
        directions.put('a', new int[]{-1, 0});
        directions.put('D', new int[]{1, 0});
        directions.put('d', new int[]{1, 0});
    }
    public location move(char key) {
        int[] d = directions.get(key);
        if (d == null) {
            return this.curr;
        }
        location newLoc = new location(this.curr.locX + d[0], this.curr.locY + d[1]);
        String tileType = genWorld.getTileName(genWorld.getTiles(), newLoc.locX, newLoc.locY);
        if (!tileType.equals("wall")) {
            if (!tileType.equals("locked door")) {
                this.curr = new location(newLoc.locX, newLoc.locY);
            } else if (tileType.equals("locked door") && genWorld.keys >= 5){
                this.curr = new location(newLoc.locX, newLoc.locY);
            }
        }
        return this.curr;
    }
}
