package framework;

import static org.lwjgl.opengl.GL33.*;
public class GPUProgram {
    private int vertexShaderId, fragmentShaderId, geometryShaderId, shaderProgramId;

    private static GPUProgram instance = null;

    private void getErrorInfo(int handle) {
        int logLength = glGetShaderi(handle, GL_INFO_LOG_LENGTH);
        if (logLength > 0) {
            System.err.println(glGetShaderInfoLog(handle));
        }
    }

    private boolean checkShader(int shaderId) {
        return glGetShaderi(shaderId, GL_COMPILE_STATUS) == GL_TRUE;
    }

    private boolean checkLinking() {
        System.out.println(glGetProgramInfoLog(shaderProgramId));
        return glGetProgrami(shaderProgramId, GL_LINK_STATUS) == GL_TRUE;
    }

    private int getLocation(String name) {
        int location = glGetUniformLocation(shaderProgramId, name);
        if(location < 0) System.err.println("Could not locate uniform " + name + " in shader program");
        return location;
    }

    private GPUProgram(){}

    public static GPUProgram getInstance(){
        if(instance == null)
            instance = new GPUProgram();
        return instance;
    }
    
    public int getId(){
        return shaderProgramId;
    }

    public boolean create(String vertexShaderSource, String fragmentShaderSource, String fragmentShaderOutputName,
                          String geometryShaderSource)
    {
        // Create the vertex shader
        if(vertexShaderId == 0)
            vertexShaderId = glCreateShader(GL_VERTEX_SHADER);
        if(vertexShaderId == 0){
            System.err.println("Could not create vertex shader");
            System.exit(-1);
        }
        glShaderSource(vertexShaderId, vertexShaderSource);
        glCompileShader(vertexShaderId);
        if(!checkShader(vertexShaderId)){
            System.err.println("Vertex shader compilation failed");
            getErrorInfo(vertexShaderId);
            return false;
        }

        // Create the fragment shader
        if(fragmentShaderId == 0)
            fragmentShaderId = glCreateShader(GL_FRAGMENT_SHADER);
        if(fragmentShaderId == 0){
            System.err.println("Could not create fragment shader");
            System.exit(-1);
        }
        glShaderSource(fragmentShaderId, fragmentShaderSource);
        glCompileShader(fragmentShaderId);
        if(!checkShader(fragmentShaderId)){
            System.err.println("Fragment shader compilation failed");
            getErrorInfo(fragmentShaderId);
            return false;
        }

        // Create the geometry shader if it exists
        if(geometryShaderSource != null){
            if(geometryShaderId == 0)
                geometryShaderId = glCreateShader(GL_GEOMETRY_SHADER);
            if(geometryShaderId == 0){
                System.err.println("Could not create geometry shader");
                System.exit(-1);
            }
            glShaderSource(geometryShaderId, geometryShaderSource);
            glCompileShader(geometryShaderId);
            if(!checkShader(geometryShaderId)){
                System.err.println("Geometry shader compilation failed");
                getErrorInfo(geometryShaderId);
                return false;
            }
        }

        // Create the shader program
        if(shaderProgramId == 0)
            shaderProgramId = glCreateProgram();
        if(shaderProgramId == 0){
            System.err.println("Could not create shader program");
            System.exit(-1);
        }

        // Attach the shaders to the program
        glAttachShader(shaderProgramId, vertexShaderId);
        glAttachShader(shaderProgramId, fragmentShaderId);
        if(geometryShaderSource != null)
            glAttachShader(shaderProgramId, geometryShaderId);

        glBindFragDataLocation(shaderProgramId, 0, fragmentShaderOutputName);

        glLinkProgram(shaderProgramId);
        if(!checkLinking()){
            System.err.println("Shader program linking failed");
            return false;
        }

        glUseProgram(shaderProgramId);
        return true;
    }

    public void setUniform(int i, String name){
        int location = getLocation(name);
        if(location >= 0) glUniform1i(location, i);
    }

    public void setUniform(float f, String name){
        int location = getLocation(name);
        if(location >= 0) glUniform1f(location, f);
    }

    public void setUniform(vec2 v, String name){
        int location = getLocation(name);
        if(location >= 0) glUniform2f(location, v.x, v.y);
    }

    public void setUniform(vec3 v, String name){
        int location = getLocation(name);
        if(location >= 0) glUniform3f(location, v.x, v.y, v.z);
    }

    public void setUniform(vec4 v, String name){
        int location = getLocation(name);
        if(location >= 0) glUniform4f(location, v.x, v.y, v.z, v.w);
    }
}
