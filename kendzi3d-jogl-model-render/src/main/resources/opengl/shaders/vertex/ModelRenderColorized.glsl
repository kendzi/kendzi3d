#version 120

uniform vec3 position;
uniform vec2 inputCoord;

varying vec2 outputCoord;

void main() {
    gl_Position = vec4(position, 1.0f);
    outputCoord = inputCoord;
}
