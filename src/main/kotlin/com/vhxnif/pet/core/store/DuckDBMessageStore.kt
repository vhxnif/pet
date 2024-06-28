package com.vhxnif.pet.core.store

import java.time.Instant
import java.util.UUID

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
        duckDBClient.query("show tables").toEntity { getString(1) }.apply {
            initChatMessageTable(this)
            initChatTable(this)
            initChatMessageMappingTable(this)
        }
    }

    private fun initChatMessageTable(tables: List<String?>) {
        if (tables.contains("chat_message")) {
            return;
        }
        listOf(
            """
                CREATE TABLE chat_message (
                    id VARCHAR,
                    type VARCHAR,
                    content VARCHAR,
                    action_time BIGINT
                )
            """.trimIndent(),
            "CREATE INDEX chat_message_id_idx ON chat_message (id)",
            "CREATE INDEX chat_message_action_time_idx ON chat_message (action_time)"
        ).forEach { duckDBClient.execute(it) }
    }

    private fun initChatTable(tables: List<String?>) {
        if (tables.contains("chat")) {
            return;
        }
        listOf(
            """
                CREATE TABLE chat(
                    id VARCHAR,
                    name VARCHAR,
                    create_time BIGINT,
                    select_time BIGINT
                )
            """.trimIndent(),
            "CREATE INDEX chat_id_idx ON chat(id)",
            "CREATE INDEX chat_name_idx ON chat(name)",
            "CREATE INDEX chat_select_time_idx ON chat(select_time)",
        ).forEach { duckDBClient.execute(it) }
        newChat("Default")
    }

    private fun newChat(name: String) {
        val now = Instant.now().toEpochMilli()
        duckDBClient.execute(
            "INSERT INTO chat (id, name, create_time, select_time) VALUES ( ?, ?, ?, ?)"
        ) {
            setObject(1, UUID.randomUUID().toString())
            setObject(2, name)
            setObject(3, now)
            setObject(4, now)
        }
    }

    private fun initChatMessageMappingTable(tables: List<String?>) {
        if (tables.contains("chat_message_mapping")) {
            return;
        }
        listOf(
            """
                CREATE TABLE chat_message_mapping(
                    id VARCHAR,
                    chat_id VARCHAR,
                    chat_message_id VARCHAR 
                )
            """.trimIndent(),

            "CREATE INDEX chat_message_mapping_id_idx ON chat_message_mapping (id)",
            "CREATE INDEX chat_message_mapping_chat_id ON chat_message_mapping (chat_id)",
            "CREATE INDEX chat_message_mapping_chat_message_id ON chat_message_mapping (chat_message_id)"
        ).forEach { duckDBClient.execute(it) }
    }
    private fun chatExists(name: String): Boolean {
        return duckDBClient.query("select id, name, create_time, select_time from chat where name = ?") {
            setObject(1, name)
        }.toEntity {
            Chat(
                getString(1),
                getString(2),
                getLong(3),
                getLong(4)
            )
        }.isNotEmpty()
    }

    private fun chatExists() : Boolean{
        return duckDBClient.query("select id, name, create_time, select_time from chat").toEntity {
            Chat(
                getString(1),
                getString(2),
                getLong(3),
                getLong(4)
            )
        }.isNotEmpty()
    }

    override fun selectOrNewChat(name: String) {
        if (chatExists(name)) {
            duckDBClient.execute("update chat set select_time = ? where name = ?") {
                setObject(1, Instant.now().toEpochMilli())
                setObject(2, name)
            }
        } else {
            newChat(name)
        }
    }



    override fun delChat(name: String) {
        duckDBClient.execute(
            """
            delete from chat_message where id in (
                select b.chat_message_id from chat a 
                inner join chat_message_mapping  b
                on a.id = b.chat_id
                where a.name = ?
            )
        """.trimIndent()
        ) {
            setObject(1, name)
        }
        duckDBClient.execute(
            """
            delete from chat_message_mapping where chat_id in (
                select id from chat where name = ?
            )
        """.trimIndent()
        ) {
            setObject(1, name)
        }
        duckDBClient.execute("delete from chat where name = ?") {
            setObject(1, name)
        }
    }

    override fun chats(): List<Chat> {
        return duckDBClient.query(
            """
                select id, name, create_time, select_time from chat order by select_time desc
            """.trimIndent()
        ).toEntity {
            Chat(
                getString(1),
                getString(2),
                getLong(3),
                getLong(4)
            )
        }
    }

    override fun contextMessage(): List<ChatMessage> {
        return duckDBClient.query(
            """
           SELECT a.id, a.type, a.content, a.action_time 
           FROM chat_message a
           INNER JOIN chat_message_mapping b ON a.id = b.chat_message_id
           INNER JOIN (
                SELECT id from chat order by select_time desc limit 1
           ) c ON b.chat_id = c.id 
           ORDER BY action_time Limit $contextCount
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

    override fun saveMessage(f: () -> Pair<ChatMessage, ChatMessage>) {
        val (first, second) = f()
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
        if (!chatExists()) {
            newChat("Default")
        }
        duckDBClient.executeBatch(
            """
           INSERT INTO chat_message_mapping (id, chat_id, chat_message_id) VALUES (?, (SELECT id from chat order by select_time desc limit 1),?)
        """.trimIndent()
        ) {
            listOf(first, second).forEach {
                it.apply {
                    setObject(1, UUID.randomUUID().toString())
                    setObject(2, id)
                    addBatch()
                }
            }
        }
    }
}