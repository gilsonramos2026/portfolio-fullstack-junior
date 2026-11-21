package com.portifolio.api.service.impl;


import com.portifolio.api.entity.enums.ImageType;
import com.portifolio.api.exception.BusinessRuleException;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;

/**
 * Reprocessa as imagens enviadas: decodifica e grava de novo, o que descarta todos os metadados (EXIF com GPS e
 * modelo da câmera, comentários, miniaturas embutidas) e qualquer dado anexado depois do fim da imagem.
 * Também limita as dimensões (evita "bombas de descompressão" e imagens enormes servidas em &lt;img&gt;).
 *
 * <p>JPEG e PNG são reprocessados. GIF e WebP não: o JDK não codifica WebP e regravar GIF perderia a animação;
 * para esses formatos o arquivo segue como enviado (validado pela assinatura).
 */
final class ImageSanitizer {

    /** Maior lado, em pixels, depois do reprocessamento. */
    static final int MAX_DIMENSION = 2000;
    /** Limite de pixels aceitos na entrada (checado antes de decodificar). */
    static final long MAX_PIXELS = 40_000_000L;

    private static final float JPEG_QUALITY = 0.85f;

    byte[] sanitize(byte[] data, ImageType type) {
        if (type != ImageType.JPEG && type != ImageType.PNG) {
            return data;
        }
        try (ImageInputStream in = ImageIO.createImageInputStream(new ByteArrayInputStream(data))) {
            Iterator<ImageReader> readers = ImageIO.getImageReaders(in);
            if (!readers.hasNext()) {
                throw unreadable();
            }
            ImageReader reader = readers.next();
            try {
                reader.setInput(in, true, true);
                long width = reader.getWidth(0);
                long height = reader.getHeight(0);
                if (width <= 0 || height <= 0) {
                    throw unreadable();
                }
                if (width * height > MAX_PIXELS) {
                    throw new BusinessRuleException("A imagem tem dimensões grandes demais (máximo de 40 megapixels)");
                }
                BufferedImage image = reader.read(0);
                if (type == ImageType.JPEG) {
                    image = applyOrientation(image, readJpegOrientation(data));
                }
                image = scaleDown(image);
                return type == ImageType.JPEG ? writeJpeg(image) : writePng(image);
            } finally {
                reader.dispose();
            }
        } catch (BusinessRuleException e) {
            throw e;
        } catch (IOException | RuntimeException e) {
            throw unreadable();
        }
    }

    private static BusinessRuleException unreadable() {
        return new BusinessRuleException("Não foi possível ler a imagem. Verifique se o arquivo não está corrompido.");
    }

    // ---------------------------------------------------------------- orientação (EXIF)

    /** Aplica a rotação/espelhamento do EXIF, já que ele será descartado e a foto ficaria deitada. */
    static BufferedImage applyOrientation(BufferedImage src, int orientation) {
        if (orientation < 2 || orientation > 8) {
            return src;
        }
        int w = src.getWidth();
        int h = src.getHeight();
        AffineTransform t = switch (orientation) {
            case 2 -> new AffineTransform(-1, 0, 0, 1, w, 0);
            case 3 -> new AffineTransform(-1, 0, 0, -1, w, h);
            case 4 -> new AffineTransform(1, 0, 0, -1, 0, h);
            case 5 -> new AffineTransform(0, 1, 1, 0, 0, 0);
            case 6 -> new AffineTransform(0, 1, -1, 0, h, 0);
            case 7 -> new AffineTransform(0, -1, -1, 0, h, w);
            default -> new AffineTransform(0, -1, 1, 0, 0, w);
        };
        boolean swap = orientation >= 5;
        BufferedImage out = new BufferedImage(swap ? h : w, swap ? w : h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = out.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, out.getWidth(), out.getHeight());
        g.drawImage(src, t, null);
        g.dispose();
        return out;
    }

    /** Lê a tag Orientation (0x0112) do segmento APP1/Exif de um JPEG; 1 (normal) se não houver. */
    static int readJpegOrientation(byte[] d) {
        if (d.length < 4 || u8(d, 0) != 0xFF || u8(d, 1) != 0xD8) {
            return 1;
        }
        int pos = 2;
        while (pos + 4 <= d.length) {
            if (u8(d, pos) != 0xFF) {
                return 1;
            }
            int marker = u8(d, pos + 1);
            if (marker == 0xFF) {
                pos++;
                continue;
            }
            if (marker == 0xDA || marker == 0xD9) {
                return 1;
            }
            int length = (u8(d, pos + 2) << 8) | u8(d, pos + 3);
            int start = pos + 4;
            int end = pos + 2 + length;
            if (length < 2 || end > d.length) {
                return 1;
            }
            if (marker == 0xE1 && end - start >= 14 && isExifHeader(d, start)) {
                return tiffOrientation(d, start + 6, end);
            }
            pos = end;
        }
        return 1;
    }

    private static boolean isExifHeader(byte[] d, int at) {
        return d[at] == 'E' && d[at + 1] == 'x' && d[at + 2] == 'i' && d[at + 3] == 'f' && d[at + 4] == 0 && d[at + 5] == 0;
    }

    private static int tiffOrientation(byte[] d, int tiff, int end) {
        if (end - tiff < 8) {
            return 1;
        }
        boolean little;
        if (d[tiff] == 'I' && d[tiff + 1] == 'I') {
            little = true;
        } else if (d[tiff] == 'M' && d[tiff + 1] == 'M') {
            little = false;
        } else {
            return 1;
        }
        long ifdOffset = u32(d, tiff + 4, little);
        long ifd = tiff + ifdOffset;
        if (ifdOffset < 8 || ifd + 2 > end) {
            return 1;
        }
        int entries = u16(d, (int) ifd, little);
        for (int i = 0; i < entries; i++) {
            long entry = ifd + 2 + 12L * i;
            if (entry + 12 > end) {
                return 1;
            }
            if (u16(d, (int) entry, little) == 0x0112) {
                int value = u16(d, (int) entry + 8, little);
                return value >= 1 && value <= 8 ? value : 1;
            }
        }
        return 1;
    }

    private static int u8(byte[] d, int i) {
        return d[i] & 0xFF;
    }

    private static int u16(byte[] d, int i, boolean little) {
        return little ? u8(d, i) | (u8(d, i + 1) << 8) : (u8(d, i) << 8) | u8(d, i + 1);
    }

    private static long u32(byte[] d, int i, boolean little) {
        long a = u16(d, i, little);
        long b = u16(d, i + 2, little);
        return little ? a | (b << 16) : (a << 16) | b;
    }

    // ---------------------------------------------------------------- redimensionamento e escrita

    /** Reduz (nunca amplia) para caber em MAX_DIMENSION, em etapas de metade para manter a nitidez. */
    static BufferedImage scaleDown(BufferedImage src) {
        int w = src.getWidth();
        int h = src.getHeight();
        int longest = Math.max(w, h);
        if (longest <= MAX_DIMENSION) {
            return src;
        }
        double ratio = (double) MAX_DIMENSION / longest;
        int targetW = Math.max(1, (int) Math.round(w * ratio));
        int targetH = Math.max(1, (int) Math.round(h * ratio));
        BufferedImage current = src;
        while (current.getWidth() / 2 >= targetW && current.getHeight() / 2 >= targetH) {
            current = resize(current, current.getWidth() / 2, current.getHeight() / 2);
        }
        return resize(current, targetW, targetH);
    }

    private static BufferedImage resize(BufferedImage src, int w, int h) {
        int type = src.getColorModel().hasAlpha() ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB;
        BufferedImage out = new BufferedImage(w, h, type);
        Graphics2D g = out.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.drawImage(src, 0, 0, w, h, null);
        g.dispose();
        return out;
    }

    private static byte[] writePng(BufferedImage image) throws IOException {
        int type = image.getColorModel().hasAlpha() ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB;
        BufferedImage normalized = image.getType() == type ? image : copyAs(image, type, null);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        if (!ImageIO.write(normalized, "png", out)) {
            throw unreadable();
        }
        return out.toByteArray();
    }

    private static byte[] writeJpeg(BufferedImage image) throws IOException {
        // JPEG não tem transparência: achata sobre branco.
        BufferedImage rgb = image.getType() == BufferedImage.TYPE_INT_RGB
                ? image : copyAs(image, BufferedImage.TYPE_INT_RGB, Color.WHITE);
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpeg");
        if (!writers.hasNext()) {
            throw unreadable();
        }
        ImageWriter writer = writers.next();
        try {
            ImageWriteParam param = writer.getDefaultWriteParam();
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(JPEG_QUALITY);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            try (MemoryCacheImageOutputStream stream = new MemoryCacheImageOutputStream(out)) {
                writer.setOutput(stream);
                // Sem metadados no IIOImage: só o cabeçalho JFIF padrão é gravado.
                writer.write(null, new IIOImage(rgb, null, null), param);
            }
            return out.toByteArray();
        } finally {
            writer.dispose();
        }
    }

    private static BufferedImage copyAs(BufferedImage src, int type, Color background) {
        BufferedImage out = new BufferedImage(src.getWidth(), src.getHeight(), type);
        Graphics2D g = out.createGraphics();
        if (background != null) {
            g.setColor(background);
            g.fillRect(0, 0, out.getWidth(), out.getHeight());
        }
        g.drawImage(src, 0, 0, null);
        g.dispose();
        return out;
    }
}
