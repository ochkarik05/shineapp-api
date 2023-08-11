package pro.shineapp.api.auth.security.token

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import me.tatarka.inject.annotations.Inject
import java.time.Instant

@Inject
class JwtTokenService : TokenService {

    override fun generate(config: TokenConfig, vararg claims: Pair<String, String>): String {
        return JWT.create()
            .withAudience(config.audience)
            .withIssuer(config.issuer)
            .withExpiresAt(Instant.now().plusMillis(config.expiresIn))
            .apply {
                claims.forEach { (key, value) ->
                    withClaim(key, value)
                }
            }.sign(Algorithm.HMAC256(config.secret))
    }
}