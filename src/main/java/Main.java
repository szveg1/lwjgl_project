import framework.GPUProgram;
import framework.vec2;
import framework.vec3;
import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import pointsNlines.Line;
import pointsNlines.LineCollection;
import pointsNlines.PointCollection;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.URISyntaxException;
import java.nio.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Main {

    private long window;

    private int vaoId, vboId;
    private enum Mode {
        ADDPOINT, ADDLINE, INTERSECT, MOVELINE
    }
    Mode mode;

    public void run() {
        initGLFW();
        loop();

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void initGLFW() {
        // Initialize GLFW.
        if ( !glfwInit() ) System.exit(-1);

        glfwSetErrorCallback((error, description) -> {
            throw new IllegalStateException(GLFWErrorCallback.getDescription(description));
        });

        // Create the window and set the OpenGL context
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
        window = glfwCreateWindow(600, 600, "Hello World!", NULL, NULL);
        if ( window == NULL )
            throw new RuntimeException("Failed to create the GLFW window");

        // Set up a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            switch(key) {
                case GLFW_KEY_P -> {
                    mode = Mode.ADDPOINT;
                    pickedPoint = null;
                    pickedLine = null;
                    points.clear();
                    lines.clear();
                }
                case GLFW_KEY_L -> {
                    mode = Mode.ADDLINE;
                    pickedPoint = null;
                    pickedLine = null;
                    points.clear();
                    lines.clear();
                }
                case GLFW_KEY_I -> {
                    mode = Mode.INTERSECT;
                    pickedPoint = null;
                    pickedLine = null;
                    points.clear();
                    lines.clear();
                }
                case GLFW_KEY_M -> {
                    mode = Mode.MOVELINE;
                    pickedPoint = null;
                    pickedLine = null;
                    points.clear();
                    lines.clear();
                }
            }
        });

        // Set up a mouse button callback. It will be called every time a mouse button is pressed or released.
        glfwSetMouseButtonCallback(window, (window, button, action, mods) -> {
            if (button == GLFW_MOUSE_BUTTON_LEFT && action == GLFW_PRESS) {
                double[] x = new double[1];
                double[] y = new double[1];

                glfwGetCursorPos(window, x, y);
                vec2 ndcPos = pixelToNDC(x[0], y[0]);

                vec3 p = new vec3(ndcPos.x, ndcPos.y, 0.0f);

                switch (mode) {
                    case ADDPOINT:
                        pc.addPoint(p);
                        pc.update();
                        break;
                    case ADDLINE:
                        pickedPoint = pc.getNearest(p);
                        points.add(pickedPoint);
                        if (points.size() > 1 && !points.get(0).equals(points.get(1))) {
                            lc.addLine(new Line(points.get(0), points.get(1)));
                            lc.update();
                            points.clear();
                        }
                        break;
                    case INTERSECT:
                        pickedLine = lc.getNearest(p);
                        if (pickedLine != null) {
                            lines.add(pickedLine);
                        }
                        if (lines.size() > 1) {
                            System.out.println("Intersect");
                            pc.addPoint(lines.get(0).getIntersect(lines.get(1)));
                            pc.update();
                            lines.clear();
                        }
                        break;
                    case MOVELINE:
                        System.out.println("Move");
                        pickedLine = lc.getNearest(p);
                        if(action != GLFW_PRESS) {
                            pickedLine = null;
                        }
                        System.out.printf("Line picked: %.1f x + %.1f y + %.1f = 0\n", pickedLine.eq.x, pickedLine.eq.y, pickedLine.eq.z);
                        break;
                }
            }
            glfwSwapBuffers(window);
        });

        // Set up a cursor position callback. It will be called every time the cursor is moved.
        glfwSetCursorPosCallback(window, (window, xpos, ypos) -> {
            if(mode == Mode.MOVELINE && pickedLine != null) {
                double[] x = new double[1];
                double[] y = new double[1];

                glfwGetCursorPos(window, x, y);
                vec2 ndcPos = pixelToNDC(x[0], y[0]);

                vec3 p = new vec3(ndcPos.x, ndcPos.y, 0.0f);
                pickedLine.move(p);
                lc.update();
                glfwSwapBuffers(window);
            }
        });

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);
    }

    PointCollection pc;
    LineCollection lc;
    vec3 pickedPoint = null;
    Line pickedLine = null;
    ArrayList<vec3> points = new ArrayList<>();
    ArrayList<Line> lines = new ArrayList<>();

    private void loop() {
        GL.createCapabilities();
        System.out.println("GL Vendor: " + glGetString(GL_VENDOR));
        System.out.println("GL Renderer: " + glGetString(GL_RENDERER));
        System.out.println("GL Version: " + glGetString(GL_VERSION));

        // Set the framebuffer size
        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        glfwGetFramebufferSize(window, width, height);
        glViewport(0, 0, width.get(0), height.get(0));
        glLineWidth(3.0f);
        glPointSize(10.0f);
        pc = new PointCollection();
        lc = new LineCollection();

        // Create VAO
        vaoId = GL33.glGenVertexArrays();
        GL33.glBindVertexArray(vaoId);

        // Define vertices data
        float[] vertices = {
                -0.8f,  -0.8f,
                -0.6f, 1.0f,
                0.8f, -0.2f,
        };

        // Create VBO and put data into it
        vboId = GL33.glGenBuffers();
        GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, vboId);
        GL33.glBufferData(GL33.GL_ARRAY_BUFFER, vertices, GL33.GL_STATIC_DRAW);

        // Define vertex data layout
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0);
        glEnableVertexAttribArray(0);

        String vertexShaderSource;
        String fragmentShaderSource;

        InputStream vertexShaderStream = getClass().getResourceAsStream("shader.vert");
        InputStream fragmentShaderStream = getClass().getResourceAsStream("shader.frag");

        if (vertexShaderStream == null || fragmentShaderStream == null) {
            try {
                throw new FileNotFoundException("Shader files not found");
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            vertexShaderSource = new String(vertexShaderStream.readAllBytes());
            fragmentShaderSource = new String(fragmentShaderStream.readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        GPUProgram program = GPUProgram.getInstance();

        program.create(vertexShaderSource,fragmentShaderSource, "fragmentColor", null);

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while ( !glfwWindowShouldClose(window) ) {
            // Set the clear color
            glClearColor((float)76 / 255, (float)76 / 255, (float)76 / 255, 0);
            glClear(GL_COLOR_BUFFER_BIT); // clear the framebuffer

            lc.Draw();
            pc.draw();

            glfwSwapBuffers(window); // swap the color buffers

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();
        }

        // Unbind VBO and VAO
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    private vec2 pixelToNDC(double pX, double pY) {
        int[] width = new int[1];
        int[] height = new int[1];
        glfwGetWindowSize(window, width, height);
        return new vec2((float) (2.0f * pX / width[0] - 1.0f), (float) (1.0f - 2.0f * pY / height[0]));
    }

    public static void main(String[] args) {
        System.out.printf(String.valueOf(Main.class));
        new Main().run();
    }

}
