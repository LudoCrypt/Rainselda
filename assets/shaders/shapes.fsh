#version 330

#ifdef GL_ES
	#define LOWP lowp
	precision mediump float;
#else
	#define LOWP 
#endif

varying LOWP vec4 v_color;
varying vec2 v_texCoords;

uniform sampler2D u_texture;

uniform ivec2 u_res;
uniform int u_fill;
uniform int u_shape;

uniform int u_thickness;

uniform float u_radius;

float roundRect(vec2 point, vec2 size, float radius) {
    point = abs(point) - size * (1.0 - radius / size);
    point = max(point, point.yx - radius);
    
    float a = 0.5 * (point.x + point.y);
    float b = 0.5 * (point.x - point.y);
    
    return a - sqrt(radius * radius * 0.5 - b * b);
}

float rect(vec2 point, vec2 size) {
    point = abs(point) - size;
    point = max(point, point.yx);
    
    return point.y;
}

void main() {
    float d = 0.0;

    vec2 qcoord = v_texCoords;

    qcoord *= vec2(u_res);
    qcoord = round(qcoord);

    if (u_shape == 0) {
        d = rect(qcoord, vec2(u_res));
    } else if (u_shape == 1) {
        d = roundRect(qcoord, vec2(u_res), u_radius * min(u_res.x, u_res.y));
    }

    int fillCheck = (-d <= float(u_thickness)) ? 1 : 0;

    if (u_fill == 1) {
        fillCheck = 1;
    }

    if (d < 0.0 && fillCheck == 1) {
        gl_FragColor = v_color;
        return;
    }

	gl_FragColor = vec4(0.5);
}