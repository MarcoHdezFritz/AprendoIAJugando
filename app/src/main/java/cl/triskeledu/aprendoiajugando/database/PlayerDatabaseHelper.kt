
package cl.triskeledu.aprendoiajugando.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import cl.triskeledu.aprendoiajugando.models.*
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import java.security.SecureRandom

class PlayerDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, "triskeledu.db", null, 1) {

    companion object {
        private const val ITERATIONS = 120000
        private const val KEY_LENGTH = 256
    }

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase) {

        // JUGADORES
        db.execSQL("""
            CREATE TABLE players (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                email TEXT NOT NULL UNIQUE,
                phone TEXT NOT NULL,
                address TEXT NOT NULL,
                password_hash TEXT NOT NULL,
                password_salt TEXT NOT NULL,
                points INTEGER DEFAULT 0,
                created_at INTEGER NOT NULL
            )
        """)

        // PUNTOS TURÍSTICOS
        db.execSQL("""
            CREATE TABLE tourist_points (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                description TEXT NOT NULL,
                qr_code TEXT NOT NULL UNIQUE,
                latitude REAL NOT NULL,
                longitude REAL NOT NULL,
                active INTEGER DEFAULT 1
            )
        """)

        // VÍDEOS EDUCATIVOS
        db.execSQL("""
            CREATE TABLE videos (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT NOT NULL,
                url TEXT NOT NULL,
                description TEXT NOT NULL,
                point_id INTEGER,
                FOREIGN KEY(point_id) REFERENCES tourist_points(id)
                    ON DELETE SET NULL
            )
        """)

        // CÓDIGOS QR ESCANEADOS
        db.execSQL("""
            CREATE TABLE scans (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                player_id INTEGER NOT NULL,
                point_id INTEGER NOT NULL,
                scanned_at INTEGER NOT NULL,
                points_awarded INTEGER DEFAULT 10,
                UNIQUE(player_id, point_id),
                FOREIGN KEY(player_id) REFERENCES players(id)
                    ON DELETE CASCADE,
                FOREIGN KEY(point_id) REFERENCES tourist_points(id)
                    ON DELETE CASCADE
            )
        """)

        // SELFIES TURÍSTICAS
        db.execSQL("""
            CREATE TABLE selfies (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                player_id INTEGER NOT NULL,
                point_id INTEGER,
                image_path TEXT NOT NULL,
                created_at INTEGER NOT NULL,
                points_awarded INTEGER DEFAULT 100,
                FOREIGN KEY(player_id) REFERENCES players(id)
                    ON DELETE CASCADE,
                FOREIGN KEY(point_id) REFERENCES tourist_points(id)
                    ON DELETE SET NULL
            )
        """)

        // HISTORIAL DE ACTIVIDADES
        db.execSQL("""
            CREATE TABLE activities (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                player_id INTEGER NOT NULL,
                type TEXT NOT NULL,
                description TEXT NOT NULL,
                points INTEGER DEFAULT 0,
                created_at INTEGER NOT NULL,
                FOREIGN KEY(player_id) REFERENCES players(id)
                    ON DELETE CASCADE
            )
        """)

        insertInitialPoints(db)
    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
        // Aquí añadiremos migraciones cuando cambie la estructura.
    }

    // ------------------------------------------------
    // SEGURIDAD DE CONTRASEÑAS
    // ------------------------------------------------

    private fun generateSalt(): ByteArray {
        val salt = ByteArray(16)
        SecureRandom().nextBytes(salt)
        return salt
    }

    private fun hashPassword(
        password: String,
        salt: ByteArray
    ): String {

        val spec = PBEKeySpec(
            password.toCharArray(),
            salt,
            ITERATIONS,
            KEY_LENGTH
        )

        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")

        return factory.generateSecret(spec)
            .encoded
            .joinToString("") {
                "%02x".format(it.toInt() and 0xff)
            }
    }

    private fun bytesToHex(bytes: ByteArray): String {
        return bytes.joinToString("") {
            "%02x".format(it.toInt() and 0xff)
        }
    }

    private fun hexToBytes(hex: String): ByteArray {
        return hex.chunked(2).map {
            it.toInt(16).toByte()
        }.toByteArray()
    }

    // ------------------------------------------------
    // REGISTRO DE JUGADORES
    // ------------------------------------------------

    fun registerPlayer(
        name: String,
        email: String,
        phone: String,
        address: String,
        password: String
    ): Boolean {

        val salt = generateSalt()
        val passwordHash = hashPassword(password, salt)

        val values = ContentValues().apply {
            put("name", name.trim())
            put("email", email.trim().lowercase())
            put("phone", phone.trim())
            put("address", address.trim())
            put("password_hash", passwordHash)
            put("password_salt", bytesToHex(salt))
            put("points", 0)
            put("created_at", System.currentTimeMillis())
        }

        return try {
            writableDatabase.insertOrThrow(
                "players",
                null,
                values
            )
            true
        } catch (e: SQLiteConstraintException) {
            false
        }
    }

    // ------------------------------------------------
    // INICIO DE SESIÓN
    // ------------------------------------------------

    fun getPlayerByEmail(email: String): Player? {

        val cursor = readableDatabase.query(
            "players",
            arrayOf(
                "id", "name", "email",
                "phone", "address", "points"
            ),
            "email = ?",
            arrayOf(email.trim().lowercase()),
            null,
            null,
            null
        )

        cursor.use {
            if (it.moveToFirst()) {
                return Player(
                    it.getInt(0),
                    it.getString(1),
                    it.getString(2),
                    it.getString(3),
                    it.getString(4),
                    it.getInt(5)
                )
            }
        }

        return null
    }

    fun authenticatePlayer(
        email: String,
        password: String
    ): String? {

        val cursor = readableDatabase.query(
            "players",
            arrayOf("name", "password_hash", "password_salt"),
            "email = ?",
            arrayOf(email.trim().lowercase()),
            null,
            null,
            null
        )

        cursor.use {
            if (it.moveToFirst()) {

                val name = it.getString(0)
                val savedHash = it.getString(1)
                val salt = hexToBytes(it.getString(2))

                val enteredHash = hashPassword(password, salt)

                return if (savedHash == enteredHash) {
                    name
                } else {
                    null
                }
            }
        }

        return null
    }

    fun getAllPlayers(): List<Player> {

        val players = mutableListOf<Player>()

        val cursor = readableDatabase.rawQuery(
            """
            SELECT id, name, email, phone, address, points
            FROM players ORDER BY name
            """,
            null
        )

        cursor.use {
            while (it.moveToNext()) {
                players.add(
                    Player(
                        it.getInt(0),
                        it.getString(1),
                        it.getString(2),
                        it.getString(3),
                        it.getString(4),
                        it.getInt(5)
                    )
                )
            }
        }

        return players
    }

    fun updatePlayerName(
        email: String,
        newName: String
    ): Boolean {

        val values = ContentValues().apply {
            put("name", newName.trim())
        }

        return writableDatabase.update(
            "players",
            values,
            "email = ?",
            arrayOf(email.trim().lowercase())
        ) > 0
    }

    // ------------------------------------------------
    // PUNTOS TURÍSTICOS
    // ------------------------------------------------

    private fun insertInitialPoints(db: SQLiteDatabase) {

        val initialPoints = listOf(
            ContentValues().apply {
                put("name", "Plaza de Armas")
                put("description", "Centro histórico de Santiago.")
                put("qr_code", "PLAZA-001")
                put("latitude", -33.4372)
                put("longitude", -70.6506)
                put("active", 1)
            },
            ContentValues().apply {
                put("name", "Palacio de La Moneda")
                put("description", "Sede del gobierno de Chile.")
                put("qr_code", "MONEDA-002")
                put("latitude", -33.4429)
                put("longitude", -70.6536)
                put("active", 1)
            },
            ContentValues().apply {
                put("name", "Cerro Santa Lucía")
                put("description", "Lugar histórico y turístico de Santiago.")
                put("qr_code", "SANTALUCIA-003")
                put("latitude", -33.4378)
                put("longitude", -70.6436)
                put("active", 1)
            }
        )

        initialPoints.forEach {
            db.insert("tourist_points", null, it)
        }
    }

    fun getTouristPoints(): List<TouristPoint> {

        val result = mutableListOf<TouristPoint>()

        val cursor = readableDatabase.rawQuery(
            "SELECT * FROM tourist_points WHERE active = 1 ORDER BY name",
            null
        )

        cursor.use {
            while (it.moveToNext()) {
                result.add(
                    TouristPoint(
                        it.getInt(it.getColumnIndexOrThrow("id")),
                        it.getString(it.getColumnIndexOrThrow("name")),
                        it.getString(it.getColumnIndexOrThrow("description")),
                        it.getString(it.getColumnIndexOrThrow("qr_code")),
                        it.getDouble(it.getColumnIndexOrThrow("latitude")),
                        it.getDouble(it.getColumnIndexOrThrow("longitude")),
                        it.getInt(it.getColumnIndexOrThrow("active")) == 1
                    )
                )
            }
        }

        return result
    }

    fun addTouristPoint(
        name: String,
        description: String,
        qrCode: String,
        latitude: Double,
        longitude: Double
    ): Long {

        val values = ContentValues().apply {
            put("name", name.trim())
            put("description", description.trim())
            put("qr_code", qrCode.trim())
            put("latitude", latitude)
            put("longitude", longitude)
            put("active", 1)
        }

        return try {
            writableDatabase.insertOrThrow(
                "tourist_points",
                null,
                values
            )
        } catch (e: SQLiteConstraintException) {
            -1L
        }
    }

    // ------------------------------------------------
    // ESCANEO QR Y PUNTAJE
    // ------------------------------------------------



    fun registerScan(
        email: String,
        pointId: Int
    ): Boolean {

        val db = writableDatabase

        db.beginTransaction()

        try {
            // 1. Buscar al jugador
            val playerId = db.rawQuery(
                "SELECT id FROM players WHERE email = ?",
                arrayOf(email.trim().lowercase())
            ).use { cursor ->
                if (cursor.moveToFirst()) cursor.getInt(0) else null
            } ?: return false

            // 2. Registrar el escaneo
            val values = ContentValues().apply {
                put("player_id", playerId)
                put("point_id", pointId)
                put("scanned_at", System.currentTimeMillis())
                put("points_awarded", 10)
            }

            val scanId = db.insertWithOnConflict(
                "scans",
                null,
                values,
                SQLiteDatabase.CONFLICT_IGNORE
            )

            // Si ya existe el escaneo, no entregar puntos otra vez
            if (scanId == -1L) {
                return false
            }

            // 3. Actualizar el puntaje y comprobar filas modificadas
            val statement = db.compileStatement(
                "UPDATE players SET points = points + 10 WHERE id = ?"
            )

            val updatedRows = try {
                statement.bindLong(1, playerId.toLong())
                statement.executeUpdateDelete()
            } finally {
                statement.close()
            }

            // Si no se actualizó exactamente un jugador, cancelar
            if (updatedRows != 1) {
                return false
            }

            // 4. Registrar en el historial
            insertActivity(
                db,
                playerId,
                "QR",
                "Código QR descubierto: punto $pointId",
                10
            )

            // 5. Confirmar transacción
            db.setTransactionSuccessful()

            return true

        } finally {
            db.endTransaction()
        }
    }

    // ------------------------------------------------
    // SELFIES TURÍSTICAS
    // ------------------------------------------------

    fun registerSelfie(
        email: String,
        pointId: Int?,
        imagePath: String
    ): Boolean {

        val db = writableDatabase
        db.beginTransaction()

        try {
            val player = getPlayerByEmail(email) ?: return false

            if (pointId != null) {
                val alreadyRegistered = db.rawQuery(
                    """
                SELECT 1 FROM selfies
                WHERE player_id = ? AND point_id = ?
                LIMIT 1
                """,
                    arrayOf(player.id.toString(), pointId.toString())
                ).use { it.moveToFirst() }

                if (alreadyRegistered) return false
            }
            val values = ContentValues().apply {
                put("player_id", player.id)

                if (pointId == null) {
                    putNull("point_id")
                } else {
                    put("point_id", pointId)
                }

                put("image_path", imagePath)
                put("created_at", System.currentTimeMillis())
                put("points_awarded", 100)
            }

            val selfieId = db.insert("selfies", null, values)

            if (selfieId == -1L) return false

            db.execSQL(
                "UPDATE players SET points = points + 100 WHERE id = ?",
                arrayOf(player.id)
            )

            insertActivity(
                db,
                player.id,
                "SELFIE",
                "Selfie turística registrada",
                100
            )

            db.setTransactionSuccessful()
            return true

        } finally {
            db.endTransaction()
        }
    }

    // ------------------------------------------------
    // PUNTAJE DEL JUGADOR
    // ------------------------------------------------

    fun getPlayerPoints(email: String): Int {

        val cursor = readableDatabase.rawQuery(
            "SELECT points FROM players WHERE email = ?",
            arrayOf(email.trim().lowercase())
        )

        cursor.use {
            return if (it.moveToFirst()) it.getInt(0) else 0
        }
    }

    // ------------------------------------------------
    // HISTORIAL DE ACTIVIDADES
    // ------------------------------------------------

    private fun insertActivity(
        db: SQLiteDatabase,
        playerId: Int,
        type: String,
        description: String,
        points: Int
    ) {

        val values = ContentValues().apply {
            put("player_id", playerId)
            put("type", type)
            put("description", description)
            put("points", points)
            put("created_at", System.currentTimeMillis())
        }

        db.insert("activities", null, values)
    }

    fun getPlayerHistory(email: String): List<ActivityRecord> {

        val result = mutableListOf<ActivityRecord>()

        val cursor = readableDatabase.rawQuery(
            """
            SELECT a.id, a.type, a.description, a.points, a.created_at
            FROM activities a
            INNER JOIN players p ON a.player_id = p.id
            WHERE p.email = ?
            ORDER BY a.created_at DESC
            """,
            arrayOf(email.trim().lowercase())
        )

        cursor.use {
            while (it.moveToNext()) {
                result.add(
                    ActivityRecord(
                        it.getInt(0),
                        it.getString(1),
                        it.getString(2),
                        it.getInt(3),
                        it.getLong(4)
                    )
                )
            }
        }

        return result
    }

    // ------------------------------------------------
    // COLECCIÓN DE DESCUBRIMIENTOS
    // ------------------------------------------------

    fun getPlayerCollection(email: String): List<TouristPoint> {

        val result = mutableListOf<TouristPoint>()

        val cursor = readableDatabase.rawQuery(
            """
            SELECT tp.id, tp.name, tp.description, tp.qr_code,
                   tp.latitude, tp.longitude, tp.active
            FROM tourist_points tp
            INNER JOIN scans s ON tp.id = s.point_id
            INNER JOIN players p ON p.id = s.player_id
            WHERE p.email = ?
            ORDER BY s.scanned_at DESC
            """,
            arrayOf(email.trim().lowercase())
        )

        cursor.use {
            while (it.moveToNext()) {
                result.add(
                    TouristPoint(
                        it.getInt(0),
                        it.getString(1),
                        it.getString(2),
                        it.getString(3),
                        it.getDouble(4),
                        it.getDouble(5),
                        it.getInt(6) == 1
                    )
                )
            }
        }

        return result
    }

    // ------------------------------------------------
    // VÍDEOS EDUCATIVOS
    // ------------------------------------------------

    fun addVideo(
        title: String,
        url: String,
        description: String,
        pointId: Int?
    ): Long {

        val values = ContentValues().apply {
            put("title", title.trim())
            put("url", url.trim())
            put("description", description.trim())

            if (pointId == null) {
                putNull("point_id")
            } else {
                put("point_id", pointId)
            }
        }

        return writableDatabase.insert("videos", null, values)
    }

    fun getVideos(): List<EducationalVideo> {

        val result = mutableListOf<EducationalVideo>()

        val cursor = readableDatabase.rawQuery(
            "SELECT id, title, url, description, point_id FROM videos ORDER BY title",
            null
        )

        cursor.use {
            while (it.moveToNext()) {
                result.add(
                    EducationalVideo(
                        it.getInt(0),
                        it.getString(1),
                        it.getString(2),
                        it.getString(3),
                        if (it.isNull(4)) null else it.getInt(4)
                    )
                )
            }
        }

        return result
    }

    // ------------------------------------------------
    // ELIMINAR JUGADOR
    // ------------------------------------------------

    fun deletePlayer(email: String): Boolean {
        return writableDatabase.delete(
            "players",
            "email = ?",
            arrayOf(email.trim().lowercase())
        ) > 0
    }
}
