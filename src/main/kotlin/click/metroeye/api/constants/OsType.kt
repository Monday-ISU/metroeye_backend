package click.metroeye.api.constants

enum class OsType {
    ANDROID,
    IOS;

    companion object {
        fun validate(value: String?) {
            val isValid = entries.any {
                it.name.equals(value, ignoreCase = true)
            }

            if (!isValid) {
                throw IllegalArgumentException("지원하지 않는 OS 유형입니다.")
            }
        }
    }
}