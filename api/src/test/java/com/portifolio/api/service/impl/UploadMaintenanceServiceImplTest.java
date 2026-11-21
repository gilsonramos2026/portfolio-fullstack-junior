package com.portifolio.api.service.impl;

import com.portifolio.api.dto.response.OrphanFilesResponse;
import com.portifolio.api.repository.ProfileRepository;
import com.portifolio.api.repository.ProjectRepository;
import com.portifolio.api.repository.SkillRepository;
import com.portifolio.api.service.FileStorageService;
import org.junit.jupiter.api.Test;
import com.portifolio.api.service.FileStorageService.StoredFileInfo;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UploadMaintenanceServiceImplTest {

    private static final Instant NOW = Instant.parse("2026-10-30T12:00:00Z");
    private static final String USED_COVER = "11111111-1111-1111-1111-111111111111.png";
    private static final String USED_SHOT = "22222222-2222-2222-2222-222222222222.jpg";
    private static final String USED_ICON = "33333333-3333-3333-3333-333333333333.webp";
    private static final String USED_AVATAR = "44444444-4444-4444-4444-444444444444.png";
    private static final String OLD_ORPHAN = "55555555-5555-5555-5555-555555555555.png";
    private static final String NEW_UPLOAD = "66666666-6666-6666-6666-666666666666.png";

    private final FileStorageService storage = mock(FileStorageService.class);
    private final ProjectRepository projects = mock(ProjectRepository.class);
    private final SkillRepository skills = mock(SkillRepository.class);
    private final ProfileRepository profiles = mock(ProfileRepository.class);
    private final UploadMaintenanceServiceImpl service = new UploadMaintenanceServiceImpl(
            storage, projects, skills, profiles, Clock.fixed(NOW, ZoneOffset.UTC));

    private static StoredFileInfo file(String name, long bytes, Instant modified) {
        return new StoredFileInfo(name, bytes, modified);
    }

    private void givenFilesAndReferences() {
        Instant old = NOW.minusSeconds(3 * 24 * 3600);
        when(storage.listAll()).thenReturn(List.of(
                file(USED_COVER, 10, old), file(USED_SHOT, 10, old), file(USED_ICON, 10, old), file(USED_AVATAR, 10, old),
                file(OLD_ORPHAN, 300, old),
                file(NEW_UPLOAD, 50, NOW.minusSeconds(3600))));
        when(projects.findAllImageUrls()).thenReturn(List.of("/api/files/" + USED_COVER, "https://externa.com/a.png"));
        when(projects.findAllScreenshotUrls()).thenReturn(List.of("/api/files/" + USED_SHOT));
        when(skills.findAllIconUrls()).thenReturn(List.of("/api/files/" + USED_ICON));
        when(profiles.findAllAvatarUrls()).thenReturn(List.of("/api/files/" + USED_AVATAR));
    }

    @Test
    void onlyOldUnreferencedFilesAreOrphans() {
        givenFilesAndReferences();

        OrphanFilesResponse result = service.findOrphans();

        assertThat(result.files()).containsExactly(OLD_ORPHAN);
        assertThat(result.count()).isEqualTo(1);
        assertThat(result.totalBytes()).isEqualTo(300L);
        verify(storage, never()).delete(org.mockito.ArgumentMatchers.anyString());
    }

    @Test
    void deleteRemovesOnlyOrphans() {
        givenFilesAndReferences();
        when(storage.delete(OLD_ORPHAN)).thenReturn(true);

        OrphanFilesResponse result = service.deleteOrphans();

        assertThat(result.files()).containsExactly(OLD_ORPHAN);
        verify(storage).delete(OLD_ORPHAN);
        verify(storage, never()).delete(USED_COVER);
        verify(storage, never()).delete(NEW_UPLOAD);
    }

    @Test
    void doesNotCountFilesThatCouldNotBeDeleted() {
        givenFilesAndReferences();
        when(storage.delete(OLD_ORPHAN)).thenReturn(false);

        assertThat(service.deleteOrphans().count()).isZero();
    }

    @Test
    void nothingToCleanWhenFolderIsEmpty() {
        when(storage.listAll()).thenReturn(List.of());
        assertThat(service.findOrphans().count()).isZero();
    }
}
