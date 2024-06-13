#version 330			// Shader 3.3

uniform vec3 color;
out vec4 fragmentColor;

void main() {
    fragmentColor = vec4(color, 1);
}