package com.chiu.sgsingle.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.Hibernate;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author mingchiuli
 * @create 2022-11-27 12:56 am
 */
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name ="m_blog")
public class BlogEntity implements Serializable {

    @Serial
    private static final long serialVersionUID =  5450815487641580632L;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @NotBlank(message = "标题不能为空")
    @Column(name = "title")
    private String title;

    @NotBlank(message = "摘要不能为空")
    @Column(name = "description")
    private String description;

    @NotBlank(message = "内容不能为空")
    @Column(name = "content")
    private String content;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "created")
    private LocalDateTime created;

    @Column(name = "status")
    private Integer status;

    @Column(name = "link")
    private String link;

    @Column(name = "read_count")
    private Long readCount;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        BlogEntity that = (BlogEntity) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
