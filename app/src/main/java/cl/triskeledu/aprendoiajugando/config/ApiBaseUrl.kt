package cl.triskeledu.aprendoiajugando.config

object ApiBaseUrl {

    fun forPort(port: Int): String {
        return "http://127.0.0.1:$port/"
    }
}