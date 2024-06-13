package pointsNlines;

import framework.vec3;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import static org.lwjgl.opengl.GL33.*;

public class PointCollection {
    private final GraphicObject points = new GraphicObject();

    public void addPoint(vec3 p) {
        p.z = 1.0f;
        points.Vtx().add(p);
        System.out.printf("Point %.1f, %.1f added\n", p.x, p.y);
    }

    public void update(){
        points.updateGPU();
    }

    public vec3 getNearest(vec3 pickedPoint) {
        vec3 nearest = null;
        float minDist = Float.MAX_VALUE;
        for (vec3 p : points.Vtx()) {
            float dist = (float) sqrt(pow(p.x - pickedPoint.x, 2) + pow(p.y - pickedPoint.y, 2));
            if (dist < minDist) {
                minDist = dist;
                nearest = p;
            }
        }
        return nearest;
    }

    public void draw() {
        points.draw(GL_POINTS, new vec3(1.0f, 0.0f, 0.0f));
    }
}
