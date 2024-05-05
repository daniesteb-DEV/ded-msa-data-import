package dev.daniesteb.ded.data.imports.service.mapper;

import dev.daniesteb.ded.data.imports.domain.DataImport;
import dev.daniesteb.ded.data.imports.domain.DetailValidatedFile;
import dev.daniesteb.ded.data.imports.domain.FileInfo;
import dev.daniesteb.ded.data.imports.infrastructure.input.rest.bean.PostDataImport200Response;
import dev.daniesteb.ded.data.imports.infrastructure.input.rest.bean.PostDataImportRequest;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        builder = @Builder(disableBuilder = true)
)
public interface DataImportMapper {
    DataImport toDataSourceInfo(PostDataImportRequest postDataImportRequest);

    FileInfo toFileInfo(dev.daniesteb.ded.data.imports.infrastructure.input.rest.bean.FileInfo fileInfo);

    PostDataImport200Response toPostDataImport200Response(DetailValidatedFile detailValidatedFile);
}
