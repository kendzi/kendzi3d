/*
 * Copyright (c) 2005 Sun Microsystems, Inc. All Rights Reserved.
 * Copyright (c) 2011 JogAmp Community. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * - Redistribution of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * - Redistribution in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * Neither the name of Sun Microsystems, Inc. or the names of
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES,
 * INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN
 * MICROSYSTEMS, INC. ("SUN") AND ITS LICENSORS SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR
 * ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR
 * DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE
 * DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY,
 * ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF
 * SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 *
 * You acknowledge that this software is not designed or intended for use
 * in the design, construction, operation or maintenance of any nuclear
 * facility.
 *
 * Sun gratefully acknowledges that this software was originally authored
 * and developed by Kenneth Bradley Russell and Christopher John Kline.
 */

package kendzi.jogl.util.texture;

import com.jogamp.common.util.IOUtil;
import com.jogamp.opengl.util.PNGPixelRect;
import com.jogamp.opengl.util.texture.spi.JPEGImage;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.imageio.ImageIO;

import kendzi.jogl.glu.GLException;
import kendzi.jogl.util.GLPixelBuffer;
import kendzi.jogl.util.texture.awt.AWTTextureData;
import kendzi.jogl.util.texture.spi.TextureProvider;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLCapabilities;

/**
 * <P>
 * Provides input and output facilities for both loading OpenGL textures from
 * disk and streams as well as writing textures already in memory back to disk.
 * </P>
 * 
 * <P>
 * The TextureIO class supports an arbitrary number of plug-in readers and
 * writers via TextureProviders and TextureWriters. TextureProviders know how to
 * produce TextureData objects from files, InputStreams and URLs. TextureWriters
 * know how to write TextureData objects to disk in various file formats. The
 * TextureData class represents the raw data of the texture before it has been
 * converted to an OpenGL texture object. The Texture class represents the
 * OpenGL texture object and provides easy facilities for using the texture.
 * </P>
 * 
 * <P>
 * There are several built-in TextureProviders and TextureWriters supplied with
 * the TextureIO implementation. The most basic provider uses the platform's
 * Image I/O facilities to read in a BufferedImage and convert it to a texture.
 * This is the baseline provider and is registered so that it is the last one
 * consulted. All others are asked first to open a given file.
 * </P>
 * 
 * <P>
 * There are three other providers registered by default as of the time of this
 * writing. One handles SGI RGB (".sgi", ".rgb") images from both files and
 * streams. One handles DirectDraw Surface (".dds") images read from files,
 * though can not read these images from streams. One handles Targa (".tga")
 * images read from both files and streams. These providers are executed in an
 * arbitrary order. Some of these providers require the file's suffix to either
 * be specified via the newTextureData methods or for the file to be named with
 * the appropriate suffix. In general a file suffix should be provided to the
 * newTexture and newTextureData methods if at all possible.
 * </P>
 * 
 * <P>
 * Note that additional TextureProviders, if reading images from InputStreams,
 * must use the mark()/reset() methods on InputStream when probing for e.g.
 * magic numbers at the head of the file to make sure not to disturb the state
 * of the InputStream for downstream TextureProviders.
 * </P>
 * 
 * <P>
 * There are analogous TextureWriters provided for writing textures back to disk
 * if desired. As of this writing, there are four TextureWriters registered by
 * default: one for Targa files, one for SGI RGB files, one for DirectDraw
 * surface (.dds) files, and one for ImageIO-supplied formats such as .jpg and
 * .png. Some of these writers have certain limitations such as only being able
 * to write out textures stored in GL_RGB or GL_RGBA format. The DDS writer
 * supports fetching and writing to disk of texture data in DXTn compressed
 * format. Whether this will occur is dependent on whether the texture's
 * internal format is one of the DXTn compressed formats and whether the target
 * file is .dds format.
 */

@Deprecated
public class TextureIO {

    // For manually disabling the use of the texture rectangle
    // extensions so you know the texture target is GL_TEXTURE_2D; this
    // is useful for shader writers (thanks to Chris Campbell for this
    // observation)
    private static boolean texRectEnabled = true;

    // ----------------------------------------------------------------------
    // methods that *do not* require a current context
    // These methods assume RGB or RGBA textures.
    // Some texture providers may not recognize the file format unless
    // the fileSuffix is specified, so it is strongly recommended to
    // specify it wherever it is known.
    // Some texture providers may also only support one kind of input,
    // i.e., reading from a file as opposed to a stream.

    /**
     * Creates a TextureData from the given URL. Does no OpenGL work.
     *
     * @param url
     *            the URL from which to read the texture data
     * @param mipmap
     *            whether mipmaps should be produced for this texture either by
     *            autogenerating them or reading them from the file. Some file
     *            formats support multiple mipmaps in a single file in which case
     *            those mipmaps will be used rather than generating them.
     * @param fileSuffix
     *            the suffix of the file name to be used as a hint of the file
     *            format to the underlying texture provider, or null if none and
     *            should be auto-detected (some texture providers do not support
     *            this)
     * @return the texture data from the URL, or null if none of the registered
     *         texture providers could read the URL
     * @throws IOException
     *             if an error occurred while reading the URL
     */
    public static TextureData newTextureData(final URL url, final boolean mipmap, String fileSuffix) throws IOException {
        if (fileSuffix == null) {
            fileSuffix = IOUtil.getFileSuffix(url.getPath());
        }
        return newTextureDataImpl(url, 0, 0, mipmap, fileSuffix);
    }

    // ----------------------------------------------------------------------
    // These methods make no assumption about the OpenGL internal format
    // or pixel format of the texture; they must be specified by the
    // user. It is not allowed to supply 0 (indicating no preference)
    // for either the internalFormat or the pixelFormat;
    // IllegalArgumentException will be thrown in this case.

    // ----------------------------------------------------------------------
    // methods that *do* require a current context
    //

    /**
     * Creates an OpenGL texture object from the specified TextureData using the
     * given OpenGL context.
     *
     * @param data
     *            the texture data to turn into an OpenGL texture
     * @throws GLException
     *             if no OpenGL context is current or if an OpenGL error occurred
     * @throws IllegalArgumentException
     *             if the passed TextureData was null
     */
    public static Texture newTexture(final TextureData data) throws GLException, IllegalArgumentException {
        if (data == null) {
            throw new IllegalArgumentException("Null TextureData");
        }
        return new Texture(data);
    }

    /**
     * Creates an OpenGL texture object from the specified URL using the current
     * OpenGL context.
     *
     * @param url
     *            the URL from which to read the texture data
     * @param mipmap
     *            whether mipmaps should be produced for this texture either by
     *            autogenerating them or reading them from the file. Some file
     *            formats support multiple mipmaps in a single file in which case
     *            those mipmaps will be used rather than generating them.
     * @param fileSuffix
     *            the suffix of the file name to be used as a hint of the file
     *            format to the underlying texture provider, or null if none and
     *            should be auto-detected (some texture providers do not support
     *            this)
     * @throws IOException
     *             if an error occurred while reading the URL
     * @throws GLException
     *             if no OpenGL context is current or if an OpenGL error occurred
     */
    public static Texture newTexture(final URL url, final boolean mipmap, String fileSuffix) throws IOException, GLException {
        if (fileSuffix == null) {
            fileSuffix = IOUtil.getFileSuffix(url.getPath());
        }
        final TextureData data = newTextureData(url, mipmap, fileSuffix);
        final Texture texture = newTexture(data);
        data.flush();
        return texture;
    }

    // ----------------------------------------------------------------------
    // SPI support
    //

    /**
     * Adds a {@link TextureProvider} to support reading of a new file format.
     * <p>
     * The last provider added, will be the first provider to be tested.
     * </p>
     * <p>
     * In case the {@link TextureProvider} also implements
     * {@link TextureProvider.SupportsImageTypes}, the {@link TextureProvider} is
     * being mapped to its supporting {@link ImageType}s allowing an O(1)
     * association.
     * </p>
     */
    public static void addTextureProvider(final TextureProvider provider) {
        // Must always add at the front so the ImageIO provider is last,
        // so we don't accidentally use it instead of a user's possibly
        // more optimal provider
        textureProviders.add(0, provider);

        if (provider instanceof TextureProvider.SupportsImageTypes) {
            final ImageType[] imageTypes = ((TextureProvider.SupportsImageTypes) provider).getImageTypes();
            if (null != imageTypes) {
                for (int i = 0; i < imageTypes.length; i++) {
                    imageType2TextureProvider.put(imageTypes[i], provider);
                }
            }
        }
    }

    // ---------------------------------------------------------------------------
    // Global disabling of texture rectangle extension
    //

    /**
     * Indicates whether the GL_ARB_texture_rectangle extension is allowed to be
     * used for non-power-of-two textures; see {@link #setTexRectEnabled
     * setTexRectEnabled}.
     */
    public static boolean isTexRectEnabled() {
        return texRectEnabled;
    }

    // ----------------------------------------------------------------------
    // Internals only below this point
    //

    private static List<TextureProvider> textureProviders = new ArrayList<>();
    private static Map<ImageType, TextureProvider> imageType2TextureProvider = new HashMap<>();

    static {
        addTextureProvider(new IIOTextureProvider());
        // Other special-case providers
        addTextureProvider(new JPGTextureProvider());
        addTextureProvider(new PNGTextureProvider());
    }

    // Implementation methods
    private static TextureData newTextureDataImpl(InputStream stream, final int internalFormat, final int pixelFormat,
            final boolean mipmap, String fileSuffix) throws IOException {
        if (stream == null) {
            throw new IOException("Stream was null");
        }

        // Note: use of BufferedInputStream works around 4764639/4892246
        if (!(stream instanceof BufferedInputStream)) {
            stream = new BufferedInputStream(stream);
        }

        // First attempt to use an ImageType mapped TextureProvider for O(1)
        // using stream parsed data, ignoring the given fileSuffix!
        try {
            final ImageType imageType = new ImageType(stream);
            if (imageType.isDefined()) {
                final TextureProvider mappedProvider = imageType2TextureProvider.get(imageType);
                if (null != mappedProvider) {
                    final TextureData data = mappedProvider.newTextureData(stream, internalFormat, pixelFormat, mipmap,
                            imageType.type);
                    if (data != null) {
                        data.srcImageType = imageType;
                        return data;
                    }
                }
            }
        } catch (final IOException ioe) {
        }

        fileSuffix = toLowerCase(fileSuffix);

        for (final Iterator<TextureProvider> iter = textureProviders.iterator(); iter.hasNext();) {
            final TextureProvider provider = iter.next();
            final TextureData data = provider.newTextureData(stream, internalFormat, pixelFormat, mipmap, fileSuffix);
            if (data != null) {
                if (provider instanceof TextureProvider.SupportsImageTypes) {
                    data.srcImageType = ((TextureProvider.SupportsImageTypes) provider).getImageTypes()[0];
                }
                return data;
            }
        }

        throw new IOException("No suitable reader for given stream");
    }

    private static TextureData newTextureDataImpl(final URL url, final int internalFormat, final int pixelFormat,
            final boolean mipmap, final String fileSuffix) throws IOException {
        if (url == null) {
            throw new IOException("URL was null");
        }
        final InputStream stream = new BufferedInputStream(url.openStream());
        try {
            return newTextureDataImpl(stream, internalFormat, pixelFormat, mipmap, fileSuffix);
        } catch (final IOException ioe) {
            throw new IOException(ioe.getMessage() + ", given URL " + url, ioe);
        } finally {
            stream.close();
        }
    }

    // ----------------------------------------------------------------------
    // Base class for internal image providers, only providing stream based data!
    abstract static class StreamBasedTextureProvider implements TextureProvider, TextureProvider.SupportsImageTypes {
        @Override
        public final TextureData newTextureData(final File file, final int internalFormat, final int pixelFormat,
                final boolean mipmap, final String fileSuffix) throws IOException {
            throw new UnsupportedOperationException("Only stream is supported");
        }

        @Override
        public final TextureData newTextureData(final URL url, final int internalFormat, final int pixelFormat,
                final boolean mipmap, final String fileSuffix) throws IOException {
            throw new UnsupportedOperationException("Only stream is supported");
        }
    }

    static class IIOTextureProvider implements TextureProvider {

        @Override
        public TextureData newTextureData(File file, int internalFormat, int pixelFormat, boolean mipmap, String fileSuffix)
                throws IOException {
            return Optional.ofNullable(ImageIO.read(file))
                    .map(img -> new AWTTextureData(internalFormat, pixelFormat, mipmap, img)).orElse(null);
        }

        @Override
        public TextureData newTextureData(InputStream stream, int internalFormat, int pixelFormat, boolean mipmap,
                String fileSuffix) throws IOException {
            return Optional.ofNullable(ImageIO.read(stream))
                    .map(img -> new AWTTextureData(internalFormat, pixelFormat, mipmap, img)).orElse(null);
        }

        @Override
        public TextureData newTextureData(URL url, int internalFormat, int pixelFormat, boolean mipmap, String fileSuffix)
                throws IOException {
            return Optional.ofNullable(ImageIO.read(url)).map(img -> new AWTTextureData(internalFormat, pixelFormat, mipmap, img))
                    .orElse(null);
        }
    }

    // ----------------------------------------------------------------------
    // PNG image provider
    static class PNGTextureProvider extends StreamBasedTextureProvider {
        private static final ImageType[] imageTypes = new ImageType[] { new ImageType(ImageType.T_PNG) };

        @Override
        public final ImageType[] getImageTypes() {
            return imageTypes;
        }

        @Override
        public TextureData newTextureData(final InputStream stream, int internalFormat, int pixelFormat, final boolean mipmap,
                final String fileSuffix) throws IOException {
            final GLCapabilities cap = GL.getCapabilities();
            if (ImageType.T_PNG.equals(fileSuffix) || ImageType.T_PNG.equals(ImageType.Util.getFileSuffix(stream))) {
                final PNGPixelRect image = PNGPixelRect.read(stream, null, true /* directBuffer */, 0 /* destMinStrideInBytes */,
                        true /* destIsGLOriented */);
                final GLPixelBuffer.GLPixelAttributes glpa = new GLPixelBuffer.GLPixelAttributes(image.getPixelformat(),
                        false /* pack */);
                if (0 == pixelFormat) {
                    pixelFormat = glpa.format;
                } // else FIXME: Actually not supported w/ preset pixelFormat!
                if (0 == internalFormat) {
                    final boolean hasAlpha = 4 == glpa.pfmt.comp.bytesPerPixel();
                    if (cap.OpenGL20) {
                        internalFormat = hasAlpha ? GL11.GL_RGBA8 : GL11.GL_RGB8;
                    } else {
                        internalFormat = hasAlpha ? GL11.GL_RGBA : GL11.GL_RGB;
                    }
                }
                return new TextureData(internalFormat, image.getSize().getWidth(), image.getSize().getHeight(), 0, pixelFormat,
                        glpa.type, mipmap, false, false, image.getPixels(), null);
            }

            return null;
        }
    }

    // ----------------------------------------------------------------------
    // JPEG image provider
    static class JPGTextureProvider extends StreamBasedTextureProvider {
        private static final ImageType[] imageTypes = new ImageType[] { new ImageType(ImageType.T_JPG) };

        @Override
        public final ImageType[] getImageTypes() {
            return imageTypes;
        }

        @Override
        public TextureData newTextureData(final InputStream stream, int internalFormat, int pixelFormat, final boolean mipmap,
                final String fileSuffix) throws IOException {
            if (ImageType.T_JPG.equals(fileSuffix) || ImageType.T_JPG.equals(ImageType.Util.getFileSuffix(stream))) {
                final JPEGImage image = JPEGImage.read(/* glp, */ stream);
                if (pixelFormat == 0) {
                    pixelFormat = image.getGLFormat();
                }
                if (internalFormat == 0) {
                    if (GL.getCapabilities().forwardCompatible) {
                        internalFormat = (image.getBytesPerPixel() == 4) ? GL11.GL_RGBA8 : GL11.GL_RGB8;
                    } else {
                        internalFormat = (image.getBytesPerPixel() == 4) ? GL11.GL_RGBA : GL11.GL_RGB;
                    }
                }
                return new TextureData(internalFormat, image.getWidth(), image.getHeight(), 0, pixelFormat, image.getGLType(),
                        mipmap, false, false, image.getData(), null);
            }

            return null;
        }
    }

    // ----------------------------------------------------------------------
    // Helper routines
    //

    private static String toLowerCase(final String arg) {
        if (arg == null) {
            return null;
        }

        return arg.toLowerCase();
    }
}
