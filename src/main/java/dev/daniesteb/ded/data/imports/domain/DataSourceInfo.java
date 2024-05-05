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
public class DataSourceInfo {
    String hostName;
    String user;
    String password;
    String database;
}
