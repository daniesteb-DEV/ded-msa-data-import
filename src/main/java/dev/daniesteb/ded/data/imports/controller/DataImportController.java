package dev.daniesteb.ded.data.imports.controller;

import dev.daniesteb.ded.data.imports.infrastructure.input.rest.DataImportsApi;
import dev.daniesteb.ded.data.imports.infrastructure.input.rest.bean.PostDataImport200Response;
import dev.daniesteb.ded.data.imports.infrastructure.input.rest.bean.PostDataImportRequest;
import dev.daniesteb.ded.data.imports.service.DataImportService;
import dev.daniesteb.ded.data.imports.service.mapper.DataImportMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
@RequiredArgsConstructor
public class DataImportController implements DataImportsApi {
    private final DataImportService dataImportService;
    private final DataImportMapper dataImportMapper;

    @Override
    public Mono<ResponseEntity<PostDataImport200Response>> postDataImport(Mono<PostDataImportRequest> postDataImportRequest,
                                                                          ServerWebExchange exchange) {
        log.info("|-> postDataImport started in controller");
        return postDataImportRequest.map(dataImportMapper::toDataSourceInfo)
                                    .flatMap(dataImportService::importFileData)
                                    .map(dataImportMapper::toPostDataImport200Response)
                                    .map(ResponseEntity::ok)
                                    .doOnError(error -> log.error(
                                            "|-> postDataImport finished with error. ErrorDetail: {}",
                                            error.getMessage()))
                                    .doOnSuccess(response -> log.info("|-> postDataImport finished successfully."));
    }
}
