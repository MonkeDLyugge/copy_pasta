package com.lyugge.entity;

import com.lyugge.api.enums.Access;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "id")
@Entity
@Table(name = "app_paste")
public class AppPaste {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(length = 2048)
    private String text;
    private LocalDateTime cancelDate;
    @Enumerated(EnumType.STRING)
    private Access access;
}
