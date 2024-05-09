package dev.daniesteb.ded.data.imports.controller;

import dev.daniesteb.ded.data.imports.infrastructure.input.rest.DataImportsApi;
import dev.daniesteb.ded.data.imports.infrastructure.input.rest.bean.PostDataImport200Response;
import dev.daniesteb.ded.data.imports.infrastructure.input.rest.bean.PostDataImportUpload200Response;
import dev.daniesteb.ded.data.imports.infrastructure.input.rest.bean.PostDataImportValidateRequest;
import dev.daniesteb.ded.data.imports.service.DataImportService;
import dev.daniesteb.ded.data.imports.service.mapper.DataImportMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.Part;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
@RequiredArgsConstructor
public class DataImportController implements DataImportsApi {
    private final DataImportService dataImportService;
    private final DataImportMapper dataImportMapper;

    @Override
    public Mono<ResponseEntity<PostDataImport200Response>> postDataImport(Flux<Part> file,
                                                                          String fileTemplateType,
                                                                          ServerWebExchange exchange) {
        log.info("|-> postDataImport started in controller");
        return dataImportService.importFileData(file, fileTemplateType)
                                .map(dataImportMapper::toPostDataImport200Response)
                                .map(ResponseEntity::ok)
                                .doOnError(error -> log.error(
                                        "|-> postDataImport finished with error. ErrorDetail: {}",
                                        error.getMessage()))
                                .doOnSuccess(response -> log.info("|-> postDataImport finished successfully."));
    }

    @Override
    public Mono<ResponseEntity<PostDataImportUpload200Response>> postDataImportUpload(Flux<Part> file,
                                                                                      ServerWebExchange exchange) {
        log.info("|-> postDataImportUpload started in controller");
        return dataImportService.uploadFile(file)
                                .map(dataImportMapper::toPostDataImportUpload200Response)
                                .map(ResponseEntity::ok)
                                .doOnError(error -> log.error(
                                        "|-> postDataImportUpload finished with error. ErrorDetail: {}",
                                        error.getMessage()))
                                .doOnSuccess(response -> log.info("|-> postDataImportUpload finished successfully."));
    }

    @Override
    public Mono<ResponseEntity<PostDataImport200Response>> postDataImportValidate(PostDataImportValidateRequest postDataImportValidateRequest,
                                                                                  ServerWebExchange exchange) {
        log.info("|-> postDataImportValidate started in controller");
        return dataImportService.validateFileData(dataImportMapper.toFileInfo(postDataImportValidateRequest))
                                .map(dataImportMapper::toPostDataImport200Response)
                                .map(ResponseEntity::ok)
                                .doOnError(error -> log.error(
                                        "|-> postDataImportValidate finished with error. ErrorDetail: {}",
                                        error.getMessage()))
                                .doOnSuccess(response -> log.info("|-> postDataImportValidate finished successfully."));
    }
}
