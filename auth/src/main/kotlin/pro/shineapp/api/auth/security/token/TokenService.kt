package pro.shineapp.api.auth.security.token

interface TokenService {
    fun generate(config: TokenConfig, vararg claims: Pair<String, String>): String
}