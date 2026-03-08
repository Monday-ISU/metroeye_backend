package click.metroeye.api.infrastructure.persistence.entity

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.ReadOnlyProperty
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("lines")
data class LineEntity(
    @Id
    @Column("id")
    val id: Long? = null,
    @Column("name")
    val name: String,
    @Column("code")
    val code: String,
    @Column("color")
    val color: String,
    @Column("display_order")
    val displayOrder: Int,
    @Column("created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),
    @ReadOnlyProperty
    @Column("updated_at")
    val updatedAt: LocalDateTime? = null
)
