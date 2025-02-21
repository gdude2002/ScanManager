/*
 * Copyrighted (Gareth Coles, 2024). Licensed under the EUPL-1.2
 * with the specific provision (EUPL articles 14 & 15) that the
 * applicable law is the (Republic of) Irish law and the Jurisdiction
 * Dublin.
 * Any redistribution must include the specific provision above.
 */

package me.gserv.archival.data

import me.gserv.archival.data.tables.BinderTable
import me.gserv.archival.data.tables.ImageTable
import me.gserv.archival.data.tables.SetTable
import io.github.oshai.kotlinlogging.KotlinLogging
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.dao.flushCache
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.transactions.transactionManager
import java.io.File
import org.jetbrains.exposed.sql.Database as ExposedDatabase

object Database {
    private val logger = KotlinLogging.logger { }

    var db: ExposedDatabase? = null
    var currentPath: String? = null

    fun connect(path: String) {
        synchronized(Database) {
            close()

            GlobalState.clear()

            val dbPath = if (path != "mem:test") {
                File(path, "db.h2").absolutePath
            } else {
                path
            }

            Flyway.configure()
                .driver("org.h2.Driver")
                .dataSource("jdbc:h2:$dbPath", "", "")
                .validateMigrationNaming(true)
                .load()
                .migrate()

            db = ExposedDatabase.connect("jdbc:h2:$dbPath;DB_CLOSE_DELAY=-1;", "org.h2.Driver")
            currentPath = dbPath

            if (!System.getenv().contains("NO_LOAD")) {
                GlobalState.load()
            }
        }
    }

    fun close() {
        if (db != null) {
            db.transactionManager.currentOrNull()?.apply {
                flushCache()
                close()
            }
        }

        db = null
    }

    fun printCreateStatements() {
        transaction {
            val lines = try {
                buildString {
                    appendLine("== Table creation SQL ==")
                    appendLine()

                    SchemaUtils.createStatements(BinderTable, ImageTable, SetTable).forEach(::appendLine)
                }
            } catch (e: Exception) {
                logger.error(e) { "Failed to generate table creation SQL statements" }

                return@transaction
            }

            logger.info {
                lines
            }
        }
    }

    fun <T> transaction(statement: Transaction.() -> T): T = transaction(db) {
        statement()
    }
}
