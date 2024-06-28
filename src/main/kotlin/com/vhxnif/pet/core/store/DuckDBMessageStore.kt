package com.vhxnif.pet.core.store

/**
 *
 * @author vhxnif
 * @since 2024-06-20
 */
class DuckDBMessageStore(
    private val duckDBClient: DuckDBClient,
    private val contextCount: Int = 10
) : IMessageStore {

    init {
        if (!duckDBClient.query("show tables").toEntity { getString(1) }.contains("chat_message")) {
            duckDBClient.execute(
                """
                CREATE TABLE chat_message (
                    id VARCHAR,
                    type VARCHAR,
                    content VARCHAR,
                    action_time BIGINT
                )
            """.trimIndent()
            )
            duckDBClient.execute(
                """
                CREATE UNIQUE INDEX chat_message_id_idx ON chat_message (id)
            """.trimIndent()
            )
            duckDBClient.execute(
                """
                CREATE INDEX chat_message_action_time_idx ON chat_message (action_time)
            """.trimIndent()
            )
        }
    }


    override fun contextMessage(): List<ChatMessage> {
        return duckDBClient.query(
            """
           SELECT id, type, content, action_time FROM chat_message ORDER BY action_time Limit $contextCount
       """.trimIndent()
        ).toEntity {
            ChatMessage(
                getString(1),
                getString(2),
                getString(3),
                getLong(4)
            )
        }
    }

    override fun saveMessage(messages :Pair<ChatMessage, ChatMessage>) {
        val (first, second) = messages
        duckDBClient.executeBatch(
            """
           INSERT INTO chat_message (id, type, content, action_time) VALUES (?, ?, ?, ?)
        """.trimIndent()
        ) {
            listOf(first, second).forEach {
                it.apply {
                    setObject(1, id)
                    setObject(2, type)
                    setObject(3, content)
                    setObject(4, actionTime)
                    addBatch()
                }
            }
        }
    }

}