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

uniform int u_falloff;
uniform vec2 u_falloffCenter;
uniform vec2 u_falloffMag;

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

#define THE_NUMBER_E 2.71828182846
const float LN_NOT_POINT_NOT_ONE = log(0.001);

void main() {
    float d = 0.0;

    vec2 qcoord = v_texCoords;

    qcoord *= vec2(u_res);
    qcoord = round(qcoord);

    if (u_shape == 0) {
        d = rect(qcoord, vec2(u_res));
    } else if (u_shape == 1) {
        d = roundRect(qcoord, vec2(u_res), u_radius * min(u_res.x, u_res.y));
    } else if (u_shape == 2) {
        d = roundRect(qcoord, vec2(u_res), u_radius);
    }

    int fillCheck = (-d <= float(u_thickness)) ? 1 : 0;

    if (u_fill == 1) {
        fillCheck = 1;
    }

    if (d < 0.0 && fillCheck == 1) {
        gl_FragColor = v_color;

        if (u_falloff == 1) {
            float d = distance(gl_FragCoord.xy, u_falloffCenter);
            gl_FragColor.a *= pow(THE_NUMBER_E, (pow(d, u_falloffMag.y) * LN_NOT_POINT_NOT_ONE) / (pow(u_falloffMag.x, u_falloffMag.y)));
        }

        return;
    }

	gl_FragColor = vec4(0.0);
}