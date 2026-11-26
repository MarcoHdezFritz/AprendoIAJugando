package cl.triskeledu.aprendoiajugando.repository

import cl.triskeledu.aprendoiajugando.config.ProductoRetrofitInstance
import cl.triskeledu.aprendoiajugando.models.Producto
import cl.triskeledu.aprendoiajugando.service.ProductoApi

class ProductoRepository(
    private val api: ProductoApi =
        ProductoRetrofitInstance.retrofit.create(ProductoApi::class.java)
) {

    suspend fun listarProductos(): Result<List<Producto>> {
        return try {
            val response = api.listarProductos()

            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Error HTTP: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun listarProductosActivos(): Result<List<Producto>> {
        return try {
            val response = api.listarProductosActivos()

            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Error HTTP: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun buscarProducto(id: Long): Result<Producto> {
        return try {
            val response = api.buscarProducto(id)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error HTTP: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun crearProducto(producto: Producto): Result<Producto> {
        return try {
            val response = api.crearProducto(producto)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error HTTP: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun actualizarProducto(
        id: Long,
        producto: Producto
    ): Result<Producto> {
        return try {
            val response = api.actualizarProducto(id, producto)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error HTTP: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun eliminarProducto(id: Long): Result<Boolean> {
        return try {
            val response = api.eliminarProducto(id)

            if (response.isSuccessful) {
                Result.success(true)
            } else {
                Result.failure(Exception("Error HTTP: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
