package ninja.droiddojo.rickandmorty.settings

/**
 * A tiny real cipher (byte-wise XOR) for JVM tests - the Android Keystore
 * only exists on real devices. Good enough to prove that no plaintext is
 * ever written to disk.
 */
class FakeSettingsCipher : SettingsCipher {

    override fun encrypt(plaintext: ByteArray): ByteArray =
        plaintext.map { (it.toInt() xor KEY).toByte() }.toByteArray()

    override fun decrypt(ciphertext: ByteArray): ByteArray =
        ciphertext.map { (it.toInt() xor KEY).toByte() }.toByteArray()

    companion object {
        private const val KEY = 0x5A
    }
}
