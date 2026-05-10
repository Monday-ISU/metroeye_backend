package click.metroeye.api.domain

import click.metroeye.api.constants.OsType

class Device private constructor(
    val id: Long?,
    val uuid: String,
    var secret: String,
    val osType: OsType,
    var refreshToken: String? = null
) {
    companion object {
        const val ACCESS_TOKEN_EXPIRATION_SECONDS = 30 * 60L
        const val REFRESH_TOKEN_EXPIRATION_SECONDS = 60 * 60 * 24 * 30L

        fun create(uuid: String, secret: String, osType: OsType, refreshToken: String): Device {
            return Device(null, uuid, secret, osType, refreshToken)
        }

        fun of(id: Long?, uuid: String, secret: String, osType: String, refreshToken: String?): Device {
            return Device(
                id,
                uuid,
                secret,
                OsType.valueOf(osType),
                refreshToken
            )
        }
    }

    fun authenticate(secret: String): Boolean {
        return this.secret == secret
    }

    fun validateRefreshToken(refreshToken: String): Boolean {
        return this.refreshToken == refreshToken
    }

    fun updateSecret(secret: String) {
        this.secret = secret
    }

    fun updateRefreshToken(refreshToken: String) {
        this.refreshToken = refreshToken
    }
}