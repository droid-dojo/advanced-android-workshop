package ninja.droiddojo.rickandmorty.settings

interface SettingsCipher {
    fun encrypt(plaintext: ByteArray): ByteArray
    fun decrypt(ciphertext: ByteArray): ByteArray
}
