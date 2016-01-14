package org.lwjglb.engine.graph;

import de.matthiasmann.twl.utils.PNGDecoder;
import de.matthiasmann.twl.utils.PNGDecoder.Format;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL30.*;

public class Texture {

    private final int id;

    private final int width;

    private final int height;

    /**
     * Creates an empty texture.
     *
     * @param width Width of the texture
     * @param height Height of the texture
     * @throws Exception
     */
    public Texture(int width, int height) throws Exception {
        this.id = glGenTextures();
        this.width = width;
        this.height = height;
        glBindTexture(GL_TEXTURE_2D, this.id);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT, this.width, this.height, 0, GL_DEPTH_COMPONENT, GL_FLOAT, (ByteBuffer) null);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);
        float borderColor[] = new float[]{1.0f, 1.0f, 1.0f, 1.0f};
        FloatBuffer floatBuff = BufferUtils.createFloatBuffer(borderColor.length);
        floatBuff.put(borderColor).flip();
        glTexParameterfv(GL_TEXTURE_2D, GL_TEXTURE_BORDER_COLOR, floatBuff);
    }

    public Texture(String fileName) throws Exception {
        this(Texture.class.getResourceAsStream(fileName));
    }

    public Texture(InputStream is) throws Exception {
        // Load Texture file
        PNGDecoder decoder = new PNGDecoder(is);

        this.width = decoder.getWidth();
        this.height = decoder.getHeight();

        // Load texture contents into a byte buffer
        ByteBuffer buf = ByteBuffer.allocateDirect(
                4 * decoder.getWidth() * decoder.getHeight());
        decoder.decode(buf, decoder.getWidth() * 4, Format.RGBA);
        buf.flip();

        // Create a new OpenGL texture 
        this.id = glGenTextures();
        // Bind the texture
        glBindTexture(GL_TEXTURE_2D, this.id);

        // Tell OpenGL how to unpack the RGBA bytes. Each component is 1 byte size
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        // Upload the texture data
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, this.width, this.height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buf);
        // Generate Mip Map
        glGenerateMipmap(GL_TEXTURE_2D);
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public void bind() {
        glBindTexture(GL_TEXTURE_2D, id);
    }

    public int getId() {
        return id;
    }

    public void cleanup() {
        glDeleteTextures(id);
    }
}