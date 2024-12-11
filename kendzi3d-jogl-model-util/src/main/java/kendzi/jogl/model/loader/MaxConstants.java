/*
 * MaxConstants.java
 *
 * Created on February 12, 2008, 10:20 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package kendzi.jogl.model.loader;

/**
 * Constants representing block types in the 3DS file. This is not a complete
 * list; it only includes the types that are currently recognized. They are all
 * of type <code>int</code> since 3DS files allocate 16 bits to the block type
 * field.
 *
 * @author RodgersGB
 * @version $Revision: 1.1 $
 */
interface MaxConstants {
    // 0xMISC Administrative codes
    int TYPE_3DS_FILE = 0x4D4D;
    int TYPE_3DS_VERSION = 0x0002;
    int TYPE_MESH_DATA = 0x3D3D;
    int TYPE_MESH_VERSION = 0x3D3E;

    // 0x0--- Data type codes
    int TYPE_COLOR_I = 0x0011;
    int TYPE_COLOR_F = 0x0010;
    int TYPE_COLOR_LIN_I = 0x0012;
    int TYPE_COLOR_LIN_F = 0x0013;
    int TYPE_PERCENT_I = 0x0030;
    int TYPE_PERCENT_F = 0x0031;

    // 0x1--- Background codes
    int TYPE_BG_BITMAP = 0x1100;
    int TYPE_BG_USE_BITMAP = 0x1101;
    int TYPE_BACKGROUND_COLOR = 0x1200;
    int TYPE_BG_USE_SOLID = 0x1201;
    int TYPE_BG_GRADIENT = 0x1300;
    int TYPE_BG_USE_GRADIENT = 0x1301;

    // 0x2--- Ambient light/fog code
    int TYPE_AMBIENT_COLOR = 0x2100;
    int TYPE_FOG = 0x2200;
    int TYPE_USE_FOG = 0x2201;
    int TYPE_FOG_BGND = 0x2210;
    int TYPE_LAYER_FOG = 0x2302;
    int TYPE_USE_LAYER_FOG = 0x2302;

    // 0x3--- View codes

    // 0x4--- Object data codes
    int TYPE_NAMED_OBJECT = 0x4000;
    int TYPE_TRIANGLE_OBJECT = 0x4100;
    int TYPE_POINT_LIST = 0x4110;
    int TYPE_VERTEX_OPTIONS = 0x4111;
    int TYPE_FACE_LIST = 0x4120;
    int TYPE_MAT_FACE_LIST = 0x4130;
    int TYPE_MAT_UV = 0x4140;
    int TYPE_SMOOTH_GROUP = 0x4150;
    int TYPE_MESH_MATRIX = 0x4160;
    int TYPE_MESH_COLOR = 0x4165;
    // 0x46-- Light data codes
    int TYPE_DIRECT_LIGHT = 0x4600;
    int TYPE_SPOTLIGHT = 0x4610;
    int TYPE_ATTENUATION = 0x4625;
    int TYPE_AMBIENT_LIGHT = 0x4680;
    // 0x47-- Camera data codes
    int TYPE_CAMERA = 0x4700;

    // 0x5--- Unknown shape stuff

    // 0x6--- Path/curve codes

    // 0x7--- Viewport codes

    // 0x8--- Unknown XDATA codes

    // 0xA--- Material codes
    int TYPE_MATERIAL = 0xAFFF;
    int TYPE_MATERIAL_NAME = 0xA000;
    int TYPE_MAT_AMBIENT = 0xA010;
    int TYPE_MAT_DIFFUSE = 0xA020;
    int TYPE_MAT_SPECULAR = 0xA030;
    int TYPE_MAT_SHININESS = 0xA040;
    int TYPE_MAT_SHININESS2 = 0xA041;
    int TYPE_MAT_TRANSPARENCY = 0xA050;
    int TYPE_MAT_XPFALL = 0xA052;
    int TYPE_MAT_REFBLUR = 0xA053;
    int TYPE_MAT_2_SIDED = 0xA081;
    int TYPE_MAT_SELF_ILPCT = 0xA084;
    int TYPE_MAT_SHADING = 0xA100;
    // 0xA2+- Texture codes
    int TYPE_MAT_TEXMAP = 0xA200;
    int TYPE_MAT_MAPNAME = 0xA300;

    // 0xB--- Node codes
    int TYPE_KEY_FRAME = 0xB000;
    int TYPE_FRAME_INFO = 0xB002;
    int TYPE_FRAMES = 0xB008;
    int TYPE_PIVOT_POINT = 0xB020;

    // 0xC--- Unknown codes
    // 0xD--- Unknown codes
    // 0xF--- Unknown codes
}