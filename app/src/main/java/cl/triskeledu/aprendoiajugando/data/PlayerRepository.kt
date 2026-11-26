package cl.triskeledu.aprendoiajugando.data

import android.content.Context
import cl.triskeledu.aprendoiajugando.database.PlayerDatabaseHelper

class PlayerRepository(context: Context) {

    private val database =
        PlayerDatabaseHelper(context.applicationContext)

    fun registerPlayer(
        name: String,
        email: String,
        phone: String,
        address: String,
        password: String
    ): Boolean {
        return database.registerPlayer(
            name,
            email,
            phone,
            address,
            password
        )
    }

    fun authenticatePlayer(
        email: String,
        password: String
    ): String? {
        return database.authenticatePlayer(email, password)
    }
}
