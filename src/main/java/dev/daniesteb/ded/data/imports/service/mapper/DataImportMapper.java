package dev.daniesteb.ded.data.imports.service.mapper;

import dev.daniesteb.ded.data.imports.domain.DetailValidatedFile;
import dev.daniesteb.ded.data.imports.domain.FileInfo;
import dev.daniesteb.ded.data.imports.infrastructure.input.rest.bean.PostDataImport200Response;
import dev.daniesteb.ded.data.imports.infrastructure.input.rest.bean.PostDataImportRequest;
import dev.daniesteb.ded.data.imports.infrastructure.input.rest.bean.PostDataImportUpload200Response;
import dev.daniesteb.ded.data.imports.infrastructure.input.rest.bean.PostDataImportValidateRequest;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        builder = @Builder(disableBuilder = true)
)
public interface DataImportMapper {
    @Mapping(target = "fileId", ignore = true)
    FileInfo toFileInfo(PostDataImportRequest postDataImportRequest);

    PostDataImport200Response toPostDataImport200Response(DetailValidatedFile detailValidatedFile);

    PostDataImportUpload200Response toPostDataImportUpload200Response(FileInfo fileInfo);

    FileInfo toFileInfo(PostDataImportValidateRequest postDataImportValidateRequest);
}
