package dev.daniesteb.ded.data.imports.repository.entity;

import lombok.*;
import org.springframework.data.annotation.Id;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BtCarTrn {
    @Id
    private Long id;
}
