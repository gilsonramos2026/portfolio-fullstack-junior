package com.portifolio.api.service.impl;

import com.portifolio.api.entity.enums.ImageType;
import com.portifolio.api.exception.BusinessRuleException;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ImageSanitizerTest {

    private final ImageSanitizer sanitizer = new ImageSanitizer();

    /** Imagem w x h: metade esquerda vermelha, metade direita azul. */
    private static BufferedImage twoColors(int w, int h, int type) {
        BufferedImage img = new BufferedImage(w, h, type);
        Graphics2D g = img.createGraphics();
        g.setColor(Color.RED);
        g.fillRect(0, 0, w / 2, h);
        g.setColor(Color.BLUE);
        g.fillRect(w / 2, 0, w - w / 2, h);
        g.dispose();
        return img;
    }

    private static byte[] encode(BufferedImage img, String format) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(img, format, out);
        return out.toByteArray();
    }

    private static BufferedImage decode(byte[] data) throws Exception {
        return ImageIO.read(new ByteArrayInputStream(data));
    }

    private static boolean contains(byte[] data, String text) {
        return new String(data, StandardCharsets.ISO_8859_1).contains(text);
    }

    /** Insere, logo após o SOI, um APP1/Exif (com a orientação dada) e um comentário com "segredos". */
    private static byte[] withExifAndComment(byte[] jpeg, int orientation, String secret) {
        byte[] tiff = {
                'I', 'I', 42, 0, 8, 0, 0, 0,          // cabeçalho TIFF little-endian, IFD em 8
                1, 0,                                  // 1 entrada
                0x12, 0x01, 3, 0, 1, 0, 0, 0, (byte) orientation, 0, 0, 0, // tag 0x0112 SHORT x1
                0, 0, 0, 0                             // fim da IFD
        };
        byte[] exifHeader = {'E', 'x', 'i', 'f', 0, 0};
        int app1Len = 2 + exifHeader.length + tiff.length;
        byte[] comment = secret.getBytes(StandardCharsets.ISO_8859_1);
        int comLen = 2 + comment.length;

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write(0xFF);
        out.write(0xD8);
        out.write(0xFF);
        out.write(0xE1);
        out.write(app1Len >> 8);
        out.write(app1Len & 0xFF);
        out.writeBytes(exifHeader);
        out.writeBytes(tiff);
        out.write(0xFF);
        out.write(0xFE);
        out.write(comLen >> 8);
        out.write(comLen & 0xFF);
        out.writeBytes(comment);
        out.write(jpeg, 2, jpeg.length - 2);
        return out.toByteArray();
    }

    private static boolean isRed(int rgb) {
        return ((rgb >> 16) & 0xFF) > 150 && (rgb & 0xFF) < 100;
    }

    private static boolean isBlue(int rgb) {
        return (rgb & 0xFF) > 150 && ((rgb >> 16) & 0xFF) < 100;
    }

    @Test
    void readsExifOrientation() throws Exception {
        byte[] jpeg = encode(twoColors(40, 20, BufferedImage.TYPE_INT_RGB), "jpg");
        assertThat(ImageSanitizer.readJpegOrientation(jpeg)).isEqualTo(1);
        for (int o = 1; o <= 8; o++) {
            assertThat(ImageSanitizer.readJpegOrientation(withExifAndComment(jpeg, o, "x"))).isEqualTo(o);
        }
        assertThat(ImageSanitizer.readJpegOrientation(new byte[]{1, 2, 3})).isEqualTo(1);
    }

    @Test
    void jpegLosesExifAndCommentsAndKeepsPicture() throws Exception {
        byte[] original = withExifAndComment(encode(twoColors(40, 20, BufferedImage.TYPE_INT_RGB), "jpg"), 1, "GPS-SECRET-LOCATION");
        assertThat(contains(original, "GPS-SECRET-LOCATION")).isTrue();

        byte[] clean = sanitizer.sanitize(original, ImageType.JPEG);

        assertThat(contains(clean, "GPS-SECRET-LOCATION")).isFalse();
        assertThat(contains(clean, "Exif")).isFalse();
        BufferedImage img = decode(clean);
        assertThat(img.getWidth()).isEqualTo(40);
        assertThat(img.getHeight()).isEqualTo(20);
        assertThat(isRed(img.getRGB(5, 10))).isTrue();
        assertThat(isBlue(img.getRGB(35, 10))).isTrue();
    }

    @Test
    void jpegOrientationIsAppliedBeforeMetadataIsDropped() throws Exception {
        // Orientação 6 = girar 90° no sentido horário: 40x20 vira 20x40 e a metade vermelha (esquerda) vai para o topo.
        byte[] original = withExifAndComment(encode(twoColors(40, 20, BufferedImage.TYPE_INT_RGB), "jpg"), 6, "s");

        BufferedImage img = decode(sanitizer.sanitize(original, ImageType.JPEG));

        assertThat(img.getWidth()).isEqualTo(20);
        assertThat(img.getHeight()).isEqualTo(40);
        assertThat(isRed(img.getRGB(10, 5))).isTrue();
        assertThat(isBlue(img.getRGB(10, 35))).isTrue();
    }

    @Test
    void orientation3RotatesHalfTurn() throws Exception {
        byte[] original = withExifAndComment(encode(twoColors(40, 20, BufferedImage.TYPE_INT_RGB), "jpg"), 3, "s");
        BufferedImage img = decode(sanitizer.sanitize(original, ImageType.JPEG));
        assertThat(img.getWidth()).isEqualTo(40);
        assertThat(isBlue(img.getRGB(5, 10))).isTrue();
        assertThat(isRed(img.getRGB(35, 10))).isTrue();
    }

    @Test
    void pngTrailingDataIsDroppedAndTransparencyKept() throws Exception {
        BufferedImage argb = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
        argb.setRGB(0, 0, 0x80FF0000); // vermelho semitransparente
        byte[] png = encode(argb, "png");
        byte[] withTrailer = new byte[png.length + 20];
        System.arraycopy(png, 0, withTrailer, 0, png.length);
        System.arraycopy("TRAILING-SECRET-XX".getBytes(StandardCharsets.ISO_8859_1), 0, withTrailer, png.length, 18);

        byte[] clean = sanitizer.sanitize(withTrailer, ImageType.PNG);

        assertThat(contains(clean, "TRAILING-SECRET")).isFalse();
        BufferedImage img = decode(clean);
        assertThat(img.getColorModel().hasAlpha()).isTrue();
        assertThat((img.getRGB(0, 0) >>> 24)).isBetween(0x70, 0x90);
        assertThat((img.getRGB(5, 5) >>> 24)).isZero();
    }

    @Test
    void largeImagesAreScaledDownKeepingProportions() throws Exception {
        byte[] png = encode(twoColors(5000, 1000, BufferedImage.TYPE_INT_RGB), "png");

        BufferedImage img = decode(sanitizer.sanitize(png, ImageType.PNG));

        assertThat(img.getWidth()).isEqualTo(ImageSanitizer.MAX_DIMENSION);
        assertThat(img.getHeight()).isEqualTo(400);
        assertThat(isRed(img.getRGB(100, 200))).isTrue();
        assertThat(isBlue(img.getRGB(1900, 200))).isTrue();
    }

    @Test
    void smallImagesAreNotEnlarged() throws Exception {
        BufferedImage img = decode(sanitizer.sanitize(encode(twoColors(300, 200, BufferedImage.TYPE_INT_RGB), "png"), ImageType.PNG));
        assertThat(img.getWidth()).isEqualTo(300);
        assertThat(img.getHeight()).isEqualTo(200);
    }

    @Test
    void rejectsImagesWithTooManyPixelsBeforeDecoding() throws Exception {
        byte[] huge = encode(new BufferedImage(8001, 5000, BufferedImage.TYPE_BYTE_GRAY), "png"); // 40,005 MP
        assertThatThrownBy(() -> sanitizer.sanitize(huge, ImageType.PNG))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("dimensões");
    }

    @Test
    void rejectsCorruptImageThatOnlyHasTheRightSignature() {
        byte[] fakePng = {(byte) 0x89, 'P', 'N', 'G', 0x0D, 0x0A, 0x1A, 0x0A, 0, 0, 0, 0, 1, 2, 3};
        assertThatThrownBy(() -> sanitizer.sanitize(fakePng, ImageType.PNG)).isInstanceOf(BusinessRuleException.class);
        byte[] fakeJpeg = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, 1, 2, 3, 4};
        assertThatThrownBy(() -> sanitizer.sanitize(fakeJpeg, ImageType.JPEG)).isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void gifAndWebpPassThroughUnchanged() {
        byte[] gif = "GIF89a-bytes".getBytes(StandardCharsets.ISO_8859_1);
        assertThat(sanitizer.sanitize(gif, ImageType.GIF)).isSameAs(gif);
        byte[] webp = "RIFF....WEBPdata".getBytes(StandardCharsets.ISO_8859_1);
        assertThat(sanitizer.sanitize(webp, ImageType.WEBP)).isSameAs(webp);
    }
}
