package pointsNlines;

import framework.GPUProgram;
import framework.vec3;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import static org.lwjgl.opengl.GL33.*;

public class GraphicObject {
    private int vao, vbo;
    private final ArrayList<vec3> vtx = new ArrayList<>();

    public GraphicObject() {
        vao = glGenVertexArrays(); glBindVertexArray(vao);
        vbo = glGenBuffers(); glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
    }

    public ArrayList<vec3> Vtx() {
        return vtx;
    }

    public void updateGPU() {
        glBindVertexArray(vao);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);

        // Create a FloatBuffer from the vertex data
        FloatBuffer vertexData = BufferUtils.createFloatBuffer(vtx.size() * 3);
        for (vec3 v : vtx) {
            vertexData.put(new float[]{v.x, v.y, v.z});
        }
        vertexData.flip();

        // Pass the vertex data to the buffer
        glBufferData(GL_ARRAY_BUFFER, vertexData, GL_STATIC_DRAW);
    }

    public void draw(int type, vec3 color) {
        if(!vtx.isEmpty()) {
            glBindVertexArray(vao);
            GPUProgram.getInstance().setUniform(color, "color");
            glDrawArrays(type, 0, vtx.size());
        }
    }
}
