package pointsNlines;

import framework.vec3;
import framework.vec4;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL33.*;

public class LineCollection {
    private GraphicObject lines = new GraphicObject();
    private ArrayList<Line> equations = new ArrayList<>();

    public void addLine(Line line) {
        equations.add(line);
        vec4 points = line.getBounds();
        lines.Vtx().add(new vec3(points.x, points.y, 0));
        lines.Vtx().add(new vec3(points.z, points.w, 0));
    }

    public Line getNearest(vec3 pp) {
        for(Line line : equations) {
            if(line.isOnLine(pp)) {
                return line;
            }
        }
        return null;
    }

    public void update() {
        for(int i = 0; i < lines.Vtx().size(); i += 2) {
            vec4 points = equations.get((i + 1) / 2).getBounds();
            lines.Vtx().set(i, new vec3(points.x, points.y, 0));
            lines.Vtx().set(i + 1, new vec3(points.z, points.w, 0));
        }
        lines.updateGPU();
    }

    public void Draw() {
        lines.draw(GL_LINES, new vec3(0, 1, 1));
    }
}
