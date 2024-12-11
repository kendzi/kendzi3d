#version 120

uniform vec4 inColour;
uniform vec2 outputCoord;

//varying vec4 FragColor;
uniform sampler2D image;

void main() {
    //gl_FragColor = texture2D(image, outputCoord);
    gl_FragColor = inColour;
}