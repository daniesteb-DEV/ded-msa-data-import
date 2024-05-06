package dev.daniesteb.ded.data.imports.service;

import dev.daniesteb.ded.data.imports.domain.DataImport;
import dev.daniesteb.ded.data.imports.domain.DetailValidatedFile;
import dev.daniesteb.ded.data.imports.domain.FileInfo;
import org.springframework.http.codec.multipart.Part;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Validated
public interface DataImportService {
    Mono<DetailValidatedFile> importFileData(DataImport dataImport);

    Mono<DetailValidatedFile> validateFileData(DataImport dataImport);

    Mono<FileInfo> uploadFile(Flux<Part> file, String fileName, String fileType);
}
