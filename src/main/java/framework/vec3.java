package framework;

public class vec3 {
    public float x, y, z;

    public vec3() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }

    public vec3(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public vec3 add(vec3 other) {
        return new vec3(this.x + other.x, this.y + other.y, this.z + other.z);
    }

    public vec3 sub(vec3 other) {
        return new vec3(this.x - other.x, this.y - other.y, this.z - other.z);
    }

    public vec3 mul(float scalar) {
        return new vec3(this.x * scalar, this.y * scalar, this.z * scalar);
    }

    public vec3 div(float scalar) {
        return new vec3(this.x / scalar, this.y / scalar, this.z / scalar);
    }

    public float dot(vec3 other) {
        return this.x * other.x + this.y * other.y + this.z * other.z;
    }

    public float length() {
        return (float) Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }

    public vec3 normalize() {
        return this.div(this.length());
    }

    public vec3 cross(vec3 other) {
        return new vec3(
            this.y * other.z - this.z * other.y,
            this.z * other.x - this.x * other.z,
            this.x * other.y - this.y * other.x
        );
    }
}
