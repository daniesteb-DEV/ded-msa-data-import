package dev.daniesteb.ded.data.imports.service;

import dev.daniesteb.ded.data.imports.domain.DataImport;
import dev.daniesteb.ded.data.imports.domain.DetailValidatedFile;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Mono;

@Validated
public interface DataImportService {
    Mono<DetailValidatedFile> importFileData(DataImport dataImport);
}
