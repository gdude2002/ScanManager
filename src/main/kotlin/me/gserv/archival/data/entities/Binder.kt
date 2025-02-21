/*
 * Copyrighted (Gareth Coles, 2024). Licensed under the EUPL-1.2
 * with the specific provision (EUPL articles 14 & 15) that the
 * applicable law is the (Republic of) Irish law and the Jurisdiction
 * Dublin.
 * Any redistribution must include the specific provision above.
 */

package me.gserv.archival.data.entities

import me.gserv.archival.data.Filesystem
import me.gserv.archival.data.tables.BinderTable
import me.gserv.archival.utils.toSlug
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.io.File

class Binder(id: EntityID<String>) : Entity<String>(id) {
    companion object : EntityClass<String, Binder>(BinderTable) {
        fun create(id: String, body: Binder.() -> Unit = {}) = new(id) {
            slug = id.toSlug()

            body()
        }
    }

    var slug by BinderTable.slug
    var archived by BinderTable.archived

    val baseDirectory by lazy { Filesystem.ensureBinder(id.value) }
    val editsDirectory by lazy { File(baseDirectory, Filesystem.EDIT_FOLDER_NAME) }
    val originalsDirectory by lazy { File(baseDirectory, Filesystem.ORIGINAL_FOLDER_NAME) }

    fun getSetEditFiles(set: Long) =
        editsDirectory.listFiles { it.startsWith("${id.value}-$set-") }

    fun getSetOriginalFiles(set: Long) =
        originalsDirectory.listFiles { it.startsWith("${id.value}-$set-") }

    fun countSetScans(set: Long): Int {
        val edits = getSetEditFiles(set).map { it.name }.toSet()
        val originals = getSetOriginalFiles(set).map { it.name }.toSet()

        return (edits + originals).size
    }
}
