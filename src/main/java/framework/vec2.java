package framework;

public class vec2 {
    public float x, y;

    public vec2() {
        this.x = 0;
        this.y = 0;
    }

    public vec2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public vec2 add(vec2 other) {
        return new vec2(this.x + other.x, this.y + other.y);
    }

    public vec2 sub(vec2 other) {
        return new vec2(this.x - other.x, this.y - other.y);
    }

    public vec2 mul(float scalar) {
        return new vec2(this.x * scalar, this.y * scalar);
    }

    public vec2 div(float scalar) {
        return new vec2(this.x / scalar, this.y / scalar);
    }

    public float dot(vec2 other) {
        return this.x * other.x + this.y * other.y;
    }

    public float length() {
        return (float) Math.sqrt(this.x * this.x + this.y * this.y);
    }

    public vec2 normalize() {
        return this.div(this.length());
    }

}
