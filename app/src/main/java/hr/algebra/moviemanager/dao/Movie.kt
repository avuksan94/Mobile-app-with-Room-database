package hr.algebra.moviemanager.dao

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate


@Entity(tableName = "movies")
data class Movie(
    @PrimaryKey(autoGenerate = true)
    var _id: Long? = null,
    var title: String? = null,
    var description: String? = null,
    var picturePath: String? = null,
    var addedDate: LocalDate = LocalDate.now(),
    var comment: String? = null // New property before migration
) {
    override fun toString() = "$title"
}