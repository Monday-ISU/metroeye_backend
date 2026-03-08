package click.metroeye.api.infrastructure.persistence.entity

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.ReadOnlyProperty
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("devices")
data class DeviceEntity(
    @Id
    @Column("id")
    val id: Long? = null,
    @Column("uuid")
    val uuid: String,
    @Column("secret")
    val secret: String,
    @Column("os_type")
    val osType: String,
    @Column("refresh_token")
    val refreshToken: String? = null,
    @Column("created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),
    @ReadOnlyProperty
    @Column("updated_at")
    val updatedAt: LocalDateTime? = null
)
