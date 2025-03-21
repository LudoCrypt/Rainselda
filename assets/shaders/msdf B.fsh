#version 330

#ifdef GL_ES
	#define LOWP lowp
	precision mediump float;
#else
	#define LOWP 
#endif

in LOWP vec4 v_color;
in vec2 v_texCoords;

uniform sampler2D u_texture;

// precomputed but probably should change at some point
#define pxDist 1.0

float median(float r, float g, float b) {
    return max(min(r, g), min(max(r, g), b));
}

float getOpacityAt(vec2 texCoord) {
    vec3 msd = texture(u_texture, texCoord).rgb;
    float sd = median(msd.r, msd.g, msd.b);
    float screenPxDistance = pxDist * (sd - 0.5);
    return clamp(screenPxDistance + 0.5, 0.0, 1.0);
}

void main() {
    float offsetX = 1.0 / textureSize(u_texture, 0).x;

    vec3 blue = vec3(0.0, 0.0, getOpacityAt(v_texCoords + vec2(offsetX, 0.0)));
    vec3 green = vec3(0.0, getOpacityAt(v_texCoords), 0.0);
    vec3 red = vec3(getOpacityAt(v_texCoords - vec2(offsetX, 0.0)), 0.0, 0.0);

    vec3 color = blue + green + red;

	gl_FragColor = vec4(v_color.xyz * color, v_color.w * length(color) / 1.41);
}