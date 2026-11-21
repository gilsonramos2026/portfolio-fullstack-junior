package com.portifolio.api.service.impl;

import com.portifolio.api.dto.response.OrphanFilesResponse;
import com.portifolio.api.repository.ProfileRepository;
import com.portifolio.api.repository.ProjectRepository;
import com.portifolio.api.repository.SkillRepository;
import com.portifolio.api.service.FileStorageService;
import com.portifolio.api.service.FileStorageService.StoredFileInfo;
import com.portifolio.api.service.UploadMaintenanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Encontra imagens enviadas que deixaram de ser usadas (projeto/skill/perfil trocou ou removeu a imagem).
 *
 * <p>Só considera arquivos com mais de {@link #MIN_AGE}: uma imagem recém-enviada ainda pode estar em um formulário
 * aberto, ainda não salvo. A remoção nunca é automática — só acontece quando o admin pede.
 */
@Service
public class UploadMaintenanceServiceImpl implements UploadMaintenanceService {

    static final Duration MIN_AGE = Duration.ofHours(24);
    private static final Logger log = LoggerFactory.getLogger(UploadMaintenanceServiceImpl.class);

    private final FileStorageService storage;
    private final ProjectRepository projects;
    private final SkillRepository skills;
    private final ProfileRepository profiles;
    private final Clock clock;

    @Autowired
    public UploadMaintenanceServiceImpl(FileStorageService storage, ProjectRepository projects,
                                        SkillRepository skills, ProfileRepository profiles) {
        this(storage, projects, skills, profiles, Clock.systemUTC());
    }

    UploadMaintenanceServiceImpl(FileStorageService storage, ProjectRepository projects, SkillRepository skills,
                                 ProfileRepository profiles, Clock clock) {
        this.storage = storage;
        this.projects = projects;
        this.skills = skills;
        this.profiles = profiles;
        this.clock = clock;
    }

    @Override
    @Transactional(readOnly = true)
    public OrphanFilesResponse findOrphans() {
        return toResponse(orphans());
    }

    @Override
    @Transactional
    public OrphanFilesResponse deleteOrphans() {
        List<StoredFileInfo> orphans = orphans();
        List<StoredFileInfo> removed = new ArrayList<>();
        for (StoredFileInfo orphan : orphans) {
            if (storage.delete(orphan.filename())) {
                removed.add(orphan);
            }
        }
        log.info("Limpeza de uploads: {} imagem(ns) órfã(s) removida(s)", removed.size());
        return toResponse(removed);
    }

    private List<StoredFileInfo> orphans() {
        Set<String> referenced = referencedFilenames();
        Instant limit = clock.instant().minus(MIN_AGE);
        return storage.listAll().stream()
                .filter(file -> !referenced.contains(file.filename()))
                .filter(file -> file.lastModified().isBefore(limit))
                .toList();
    }

    private Set<String> referencedFilenames() {
        Set<String> names = new HashSet<>();
        Stream.<List<String>>of(projects.findAllImageUrls(), projects.findAllScreenshotUrls(),
                        skills.findAllIconUrls(), profiles.findAllAvatarUrls())
                .flatMap(List::stream)
                .filter(url -> url.startsWith(LocalFileStorageService.URL_PREFIX))
                .map(url -> url.substring(LocalFileStorageService.URL_PREFIX.length()))
                .forEach(names::add);
        return names;
    }

    private static OrphanFilesResponse toResponse(List<StoredFileInfo> files) {
        return new OrphanFilesResponse(
                files.size(),
                files.stream().mapToLong(StoredFileInfo::sizeBytes).sum(),
                files.stream().map(StoredFileInfo::filename).sorted().toList());
    }
}