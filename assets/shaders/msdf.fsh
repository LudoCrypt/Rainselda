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

uniform int u_offset;
uniform int u_colorize;

float median(float r, float g, float b) {
    return max(min(r, g), min(max(r, g), b));
}

void main() {
    vec3 msd = texture(u_texture, v_texCoords).rgb;
    float sd = median(msd.r, msd.g, msd.b);

    // precomputed but probably should change at some point
    float pxDist = 1.0;

    float screenPxDistance = pxDist * (sd - 0.5);
    float opacity = clamp(screenPxDistance + 0.5, 0.0, 1.0);

    vec3 color = vec3(0.0);

    if (u_colorize == 1) {
        color[u_offset] = 1.0;
    } else {
        color = vec3(0.0);
    }

	gl_FragColor = vec4(v_color.xyz * color, v_color.w * opacity);
}