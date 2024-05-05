package dev.daniesteb.ded.data.imports.domain;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Generated
public class DataImport {
    FileInfo fileInfo;
    DataSourceInfo dataSourceInfo;
}
