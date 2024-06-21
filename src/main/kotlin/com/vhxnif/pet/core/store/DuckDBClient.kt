package com.vhxnif.pet.core.store

import org.duckdb.DuckDBConnection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.ResultSet

/**
 *
 * @author chen
 * @since 2024-06-20
 */
class DuckDBClient(
    val dbFile: String
) {
    val conn: DuckDBConnection;

    init {
        Class.forName("org.duckdb.DuckDBDriver");
        conn = DriverManager.getConnection("jdbc:duckdb:$dbFile") as DuckDBConnection
    }


    class ResultWrapper(
        val statement : PreparedStatement
    ) {

        fun <R> toEntity(f: ResultSet.() -> R) : List<R> {
            return mutableListOf<R>().apply {
                statement.use {
                    it.executeQuery().use { rs ->
                        while (rs.next()) {
                            rs.apply { add(f()) }
                        }
                    }
                }
            }
        }

    }

    fun execute(sql: String): Boolean = conn.createStatement().use {
        it.execute(sql)
    }

    fun execute(sql: String, f: PreparedStatement.() -> Unit) {
        conn.prepareStatement(sql).apply { f() }.use {
            it.execute()
        }
    }

    fun executeBatch(sql: String, f: PreparedStatement.() -> Unit) {
        conn.prepareStatement(sql).apply { f() }.use {
            it.executeBatch()
        }
    }

    fun query(sql: String): ResultWrapper {
        return ResultWrapper (
            conn.prepareStatement(sql)
        )
    }

    fun  query(sql: String, f: PreparedStatement.() -> Unit): ResultWrapper {
        return ResultWrapper(
            conn.prepareStatement(sql).apply { f() }
        )
    }

    fun close() {
        if (!conn.isClosed) {
            conn.close()
        }
    }
}
