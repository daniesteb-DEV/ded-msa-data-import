package dev.daniesteb.ded.data.imports.service.mapper;

import dev.daniesteb.ded.data.imports.domain.DataImport;
import dev.daniesteb.ded.data.imports.domain.DetailValidatedFile;
import dev.daniesteb.ded.data.imports.domain.FileInfo;
import dev.daniesteb.ded.data.imports.infrastructure.input.rest.bean.PostDataImport200Response;
import dev.daniesteb.ded.data.imports.infrastructure.input.rest.bean.PostDataImportRequest;
import dev.daniesteb.ded.data.imports.infrastructure.input.rest.bean.PostDataImportUpload200Response;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        builder = @Builder(disableBuilder = true)
)
public interface DataImportMapper {
    DataImport toDataSourceInfo(PostDataImportRequest postDataImportRequest);

    PostDataImport200Response toPostDataImport200Response(DetailValidatedFile detailValidatedFile);

    PostDataImportUpload200Response toPostDataImportUpload200Response(FileInfo fileInfo);
}
