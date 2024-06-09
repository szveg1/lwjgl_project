package framework;

public class vec4 {
    public float x, y, z, w;
    public vec4() {
        x = 0;
        y = 0;
        z = 0;
        w = 0;
    }

    public vec4(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public vec4 add(vec4 other) {
        return new vec4(x + other.x, y + other.y, z + other.z, w + other.w);
    }

    public vec4 sub(vec4 other) {
        return new vec4(x - other.x, y - other.y, z - other.z, w - other.w);
    }

    public vec4 mul(float scalar) {
        return new vec4(x * scalar, y * scalar, z * scalar, w * scalar);
    }

    public vec4 div(float scalar) {
        return new vec4(x / scalar, y / scalar, z / scalar, w / scalar);
    }

    public float dot(vec4 other) {
        return x * other.x + y * other.y + z * other.z + w * other.w;
    }


}
