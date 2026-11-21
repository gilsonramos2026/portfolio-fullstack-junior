package com.portifolio.api.entity.enums;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ImageTypeTest {

    private static byte[] bytes(int... values) {
        byte[] result = new byte[12];
        for (int i = 0; i < values.length; i++) {
            result[i] = (byte) values[i];
        }
        return result;
    }

    @Test
    void detectsPng() {
        assertThat(ImageType.detect(bytes(0x89, 'P', 'N', 'G', 0x0D, 0x0A, 0x1A, 0x0A))).contains(ImageType.PNG);
    }

    @Test
    void detectsJpeg() {
        assertThat(ImageType.detect(bytes(0xFF, 0xD8, 0xFF, 0xE0))).contains(ImageType.JPEG);
    }

    @Test
    void detectsGif() {
        assertThat(ImageType.detect(bytes('G', 'I', 'F', '8', '9', 'a'))).contains(ImageType.GIF);
        assertThat(ImageType.detect(bytes('G', 'I', 'F', '8', '7', 'a'))).contains(ImageType.GIF);
    }

    @Test
    void detectsWebp() {
        assertThat(ImageType.detect(bytes('R', 'I', 'F', 'F', 0, 0, 0, 0, 'W', 'E', 'B', 'P'))).contains(ImageType.WEBP);
    }

    @Test
    void rejectsSvgHtmlAndShortInput() {
        assertThat(ImageType.detect("<svg xmlns=".getBytes())).isEmpty();
        assertThat(ImageType.detect("<html><scr".getBytes())).isEmpty();
        assertThat(ImageType.detect(new byte[]{(byte) 0x89, 'P'})).isEmpty();
        assertThat(ImageType.detect(null)).isEmpty();
    }

    @Test
    void rejectsRiffThatIsNotWebp() {
        assertThat(ImageType.detect(bytes('R', 'I', 'F', 'F', 0, 0, 0, 0, 'W', 'A', 'V', 'E'))).isEmpty();
    }

}