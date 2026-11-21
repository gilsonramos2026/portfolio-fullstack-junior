package com.portifolio.api.service.impl;


import com.portifolio.api.config.UploadProperties;
import com.portifolio.api.entity.enums.ImageType;
import com.portifolio.api.exception.BusinessRuleException;
import com.portifolio.api.exception.ResourceNotFoundException;
import com.portifolio.api.service.FileStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LocalFileStorageServiceTest {

    private static final byte[] PNG = {(byte) 0x89, 'P', 'N', 'G', 0x0D, 0x0A, 0x1A, 0x0A, 0, 0, 0, 0, 1, 2, 3};

    @TempDir
    Path dir;

    private static byte[] validPng() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(new BufferedImage(4, 4, BufferedImage.TYPE_INT_RGB), "png", out);
        return out.toByteArray();
    }

    private LocalFileStorageService service;

    @BeforeEach
    void setUp() {
        service = new LocalFileStorageService(new UploadProperties(dir.toString(), 1024));
    }

    @Test
    void storesImageWithRandomNameAndExtensionFromContent() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "../../evil.exe", "application/octet-stream", validPng());

        String url = service.store(file);

        assertThat(url).startsWith("/api/files/").endsWith(".png").doesNotContain("evil");
        String name = url.substring("/api/files/".length());
        assertThat(Files.exists(dir.resolve(name))).isTrue();
        assertThat(service.load(name).type()).isEqualTo(ImageType.PNG);
    }

    @Test
    void storedPngIsReencodedWithoutTrailingData() throws Exception {
        byte[] png = validPng();
        byte[] withTrailer = Arrays.copyOf(png, png.length + 10);
        System.arraycopy("SECRET-TAIL".getBytes(), 0, withTrailer, png.length, 10);

        String url = service.store(new MockMultipartFile("file", "a.png", "image/png", withTrailer));

        byte[] stored = Files.readAllBytes(dir.resolve(url.substring("/api/files/".length())));
        assertThat(new String(stored, java.nio.charset.StandardCharsets.ISO_8859_1)).doesNotContain("SECRET-TAI");
    }

    @Test
    void rejectsCorruptImageWithValidSignature() {
        MockMultipartFile file = new MockMultipartFile("file", "a.png", "image/png", PNG);

        assertThatThrownBy(() -> service.store(file)).isInstanceOf(BusinessRuleException.class);
        assertThat(dir.toFile().list()).isEmpty(); // nada é gravado quando a imagem é recusada
    }

    @Test
    void rejectsNonImageEvenWithImageContentType() {
        MockMultipartFile file = new MockMultipartFile("file", "a.png", "image/png", "<svg onload=alert(1)>".getBytes());

        assertThatThrownBy(() -> service.store(file)).isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void rejectsEmptyAndOversizedFiles() {
        assertThatThrownBy(() -> service.store(new MockMultipartFile("file", new byte[0])))
                .isInstanceOf(BusinessRuleException.class);
        byte[] big = new byte[2048];
        System.arraycopy(PNG, 0, big, 0, PNG.length);
        assertThatThrownBy(() -> service.store(new MockMultipartFile("file", "big.png", "image/png", big)))
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void listsAndDeletesOnlyStoredImages() throws Exception {
        String url = service.store(new MockMultipartFile("file", "a.png", "image/png", validPng()));
        String name = url.substring("/api/files/".length());
        Files.writeString(dir.resolve("notas.txt"), "não é um upload gerenciado");

        assertThat(service.listAll()).extracting(FileStorageService.StoredFileInfo::filename).containsExactly(name);
        assertThat(service.delete("notas.txt")).isFalse();
        assertThat(service.delete("../" + name)).isFalse();
        assertThat(Files.exists(dir.resolve("notas.txt"))).isTrue();
        assertThat(service.delete(name)).isTrue();
        assertThat(service.delete(name)).isFalse();
        assertThat(service.listAll()).isEmpty();
    }

    @Test
    void loadRejectsTraversalAndUnknownNames() {
        assertThatThrownBy(() -> service.load("../secret.png")).isInstanceOf(ResourceNotFoundException.class);
        assertThatThrownBy(() -> service.load("..%2Fsecret.png")).isInstanceOf(ResourceNotFoundException.class);
        assertThatThrownBy(() -> service.load("00000000-0000-0000-0000-000000000000.png"))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
