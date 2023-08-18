package pro.shineapp.api.auth.security.hashing

import me.tatarka.inject.annotations.Inject
import org.apache.commons.codec.binary.Hex
import org.apache.commons.codec.digest.DigestUtils
import java.security.SecureRandom

interface SaltService {
    fun generateSalt(length: Int = 16): String = SecureRandom.getInstance("SHA1PRNG").generateSeed(length).let {
        Hex.encodeHexString(it)
    }
}

interface HashService{
    fun hash(value: String, salt: String): String = DigestUtils.sha256Hex(value + salt)
}

@Inject
class SHA256HashingService(
    private val saltService: SaltService,
    private val hashService: HashService,
) : HashingService {
    override fun generateSaltedHash(value: String, saltLength: Int): SaltedHash {
        val salt = saltService.generateSalt(saltLength)
        val hash = hashService.hash(value, salt)
        return SaltedHash(hash, salt)
    }

    override fun verify(value: String, saltedHash: SaltedHash): Boolean {
        return hashService.hash(value, saltedHash.salt) == saltedHash.hash
    }
}