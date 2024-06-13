package pointsNlines;

import framework.vec3;
import framework.vec4;

public class Line {
    public vec3 eq;
    private vec3 p,q;

    public Line(vec3 p, vec3 q) {
        vec3 v = q.sub(p);
        eq = p.cross(q);
        System.out.println("Line added\n");
        System.out.printf("Implicit: %.1f x + %.1f y + %.1f = 0\n", eq.x, eq.y, eq.z);
        System.out.printf("Parametric: r(t) = (%.1f, %f) + (%.1f, %.1f)t\n\n", p.x, v.x, p.y, v.y);
    }

    public vec3 getIntersect(Line other) {
        return eq.cross(other.eq);
    }

    public boolean isOnLine(vec3 p) {
        float distance = (eq.x * p.x + eq.y * p.y + eq.z) / (float) Math.sqrt(eq.x * eq.x + eq.y * eq.y);
        return Math.abs(distance) < 0.01;
    }

    vec4 getBounds() {
        float Y1 = -(eq.x + eq.z) / eq.y;
        float Y2 = (eq.x - eq.z) / eq.y;
        float X1 = 1;
        float X2 = -1;

        if(eq.y == 0) {
            Y1 = 1;
            Y2 = -1;
            X1 = -eq.z;
            X2 = -eq.z;
        }

        p = new vec3(X1, Y1, 1);
        q = new vec3(X2, Y2, 1);

        return new vec4(p.x, p.y, q.x, q.y);
    }

    public void move(vec3 point) {
        eq.z = -eq.x * point.x - eq.y * point.y;
    }
}
