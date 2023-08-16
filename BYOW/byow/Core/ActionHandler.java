package byow.Core;

import edu.princeton.cs.algs4.StdDraw;


public class ActionHandler {
    GenWorld genWorld;
    Engine e;
    public ActionHandler(GenWorld genWorld, Engine e) {
        this.genWorld = genWorld;
        this.e = e;
    }

    public void actionDispatcher() {
        boolean colonPressed = false;
        while (true) {
            int mouseX = (int) Math.floor(StdDraw.mouseX());
            int mouseY = (int) Math.floor(StdDraw.mouseY());
            genWorld.mouseX = mouseX;
            genWorld.mouseY = mouseY;
            genWorld.display(e.mainGuy);
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                if (colonPressed) {
                    if (key == 'Q' || key == 'q') {
                        e.saveGame();
                        return;
                    } else {
                        colonPressed = false;
                    }
                }
                if (key == ':') {
                    colonPressed = true;
                }
                if (key == 'P' || key == 'p') {
                    genWorld.flipDarkMode();
                }
                e.mainGuy.move(key);
                genWorld.display(e.mainGuy);
            }
        }
    }

}
