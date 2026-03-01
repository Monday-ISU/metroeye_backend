package click.metroeye.api.domain

import click.metroeye.api.constants.OsType

class Device private constructor(
    val idx: Long?,
    val uuid: String,
    var secret: String,
    val osType: OsType,
    var refreshToken: String? = null
) {
    companion object {
        const val ACCESS_TOKEN_EXPIRATION_SECONDS = 3 * 60L
        const val REFRESH_TOKEN_EXPIRATION_SECONDS = 5 * 60L

        fun create(uuid: String, secret: String, osType: OsType): Device {
            return Device(null, uuid, secret, osType)
        }

        fun of(idx: Long?, uuid: String, secret: String, osType: String, refreshToken: String?): Device {
            return Device(
                idx,
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