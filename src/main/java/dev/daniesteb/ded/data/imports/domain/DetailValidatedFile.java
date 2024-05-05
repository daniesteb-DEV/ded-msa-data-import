package dev.daniesteb.ded.data.imports.domain;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Generated
public class DetailValidatedFile {
    List<FileDetailError> fileDetailErrors;
}
