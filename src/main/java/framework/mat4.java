package framework;

public class mat4 {
    private final vec4[] rows = new vec4[4];

    mat4(float m00, float m01, float m02, float m03,
         float m10, float m11, float m12, float m13,
         float m20, float m21, float m22, float m23,
         float m30, float m31, float m32, float m33) {
        rows[0].x = m00; rows[0].y = m01; rows[0].z = m02; rows[0].w = m03;
        rows[1].x = m10; rows[1].y = m11; rows[1].z = m12; rows[1].w = m13;
        rows[2].x = m20; rows[2].y = m21; rows[2].z = m22; rows[2].w = m23;
        rows[3].x = m30; rows[3].y = m31; rows[3].z = m32; rows[3].w = m33;
    }

    public vec4 mul(vec4 v) {
        return new vec4(
            rows[0].x * v.x + rows[0].y * v.y + rows[0].z * v.z + rows[0].w * v.w,
            rows[1].x * v.x + rows[1].y * v.y + rows[1].z * v.z + rows[1].w * v.w,
            rows[2].x * v.x + rows[2].y * v.y + rows[2].z * v.z + rows[2].w * v.w,
            rows[3].x * v.x + rows[3].y * v.y + rows[3].z * v.z + rows[3].w * v.w
        );
    }
}
